package eus.martico.fileTransfer.dao.pedidos.validador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.dao.BaseGenDAO;
import eus.martico.fileTransfer.dao.DAOException;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesIDAO;
import eus.martico.fileTransfer.dao.usuario.UsuarioIDAO;
import eus.martico.fileTransfer.model.pedido.files.PedidoFilesVO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorDAOImpCons;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorDAOImpMapper;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorVO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.utils.TratarDatosUtils;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@Service(value = "PedidosValidadorIDAO")
public class PedidosValidadorDAOImp extends BaseGenDAO<PedidoFilesVO> implements PedidosValidadorIDAO {

	protected static final Logger logger = LoggerFactory.getLogger(PedidosValidadorDAOImp.class);
	
	@Autowired
	PedidoFilesIDAO pedidoFilesDAO;
	
	@Autowired
	UsuarioIDAO usuarioDAO;
	
	
	
	//CMRs Pendientes de Validar (1) 
	//o CMRs que NoAplican(7) solo si tienen un hermano Pendiente de validar(1)
	//o CMRs ya Validados(2) solo si tienen un hermano Pendiente de validar(1)
	private static StringBuilder condicionEstadosValidador =
			new StringBuilder(
					"(t." + PedidoProveedorDAOImpCons.BD_FCTPDNPRV_ECMR 
					+ " = "+FileTransferConstantes.ESTADO_1_FICH_SUBIDO + ") "
					+ " OR ("
					+ "t." + PedidoProveedorDAOImpCons.BD_FCTPDNPRV_ECMR 
	                + " IN (" + FileTransferConstantes.ESTADO_2_FICH_VALIDADO + ", " + FileTransferConstantes.ESTADO_7_FICH_SIN_DOC + ") "
	                + " AND t." + PedidoProveedorDAOImpCons.BD_FCTPDNPRV_TR + " LIKE 'GRULOG%' "
	                + " AND ( "
	                + "    SELECT COUNT(*) "
	                + "    FROM FCTPDNPRV h "
	                + "    WHERE SUBSTR(h." + PedidoProveedorDAOImpCons.BD_FCTPDNPRV_TR + ", 1, 13) = SUBSTR(t." + PedidoProveedorDAOImpCons.BD_FCTPDNPRV_TR + ", 1, 13) "
	                + "      AND h." + PedidoProveedorDAOImpCons.BD_FCTPDNPRV_ECMR + " = " + FileTransferConstantes.ESTADO_1_FICH_SUBIDO
	                + " ) > 0 "
	                + ")"
			);
	
	//Para sacar los Anulados
	private static StringBuilder condicionAnulados =
			new StringBuilder(
					PedidoProveedorDAOImpCons.BD_FCTPDNPRV_ESTADO
					+ " != " + FileTransferConstantes.ESTADO_9_PEDIDO_NULO
			);

	private static StringBuilder condicionOrderValidador =
			new StringBuilder(
					" ORDER BY t.FECREAL ASC "
			);
	
	
	@Override
	public ValidateDTO<PedidoFilesVO> updateAnotaciones(MyServiceContext _context, String _track, String _exp, String _anotacion) 
			throws PedidoFilesException {
		//El pedido tiene que estar ya registrado en FTARCHI
		PedidoFilesVO pedidoReg = pedidoFilesDAO.getPedidoFiles(_track, _exp);
		
		String anotaText = "";
		if(!_anotacion.equals("null")){
			anotaText = _anotacion;
			pedidoReg.setAnotaciones(FileTransferConstantes.CON_ANOTACIONES);
		} else {
			pedidoReg.setAnotaciones(FileTransferConstantes.SIN_ANOTACIONES);
		}
		
		pedidoFilesDAO.updateCMRanotaciones(_track, _exp, pedidoReg.getAnotaciones(), anotaText);
		
		ValidateDTO<PedidoFilesVO> validate = new ValidateDTO<PedidoFilesVO>();
		pedidoReg.setMotivoRechazo(anotaText);
		validate.setDatos(pedidoReg);
		if(pedidoReg.getAnotaciones().equals(FileTransferConstantes.SIN_ANOTACIONES)){
			logger.info("CMR actualizado : sin Anotaciones");
		} else {
			logger.info("CMR actualizado : con Anotaciones");
		}
		return validate;
	}
	
	@Override
	public List<PedidoProveedorVO> getPedidosFiltrados(MyServiceContext _context, String _expCod, String _provName, String _track, String _refCharge, String _fecDesde, String _fecHasta) 
			throws PedidosValidadorException, PedidoFilesException {
		//UPPER ACUMULATIVE DYNAMIC FILTER
		try {
			List<PedidoProveedorVO> lista = new ArrayList<>();
			StringBuilder sqlSelectMttoPV = new StringBuilder();
			List<Object> params = new ArrayList<>();
			StringBuilder infoLog = new StringBuilder();
			
			boolean isExterno = Arrays.asList(_context.getUserRoles()).stream()
					.noneMatch(role -> role.equals(FileTransferConstantes.PROPERTY_KEY_ROL_ADMIN) 
									|| role.equals(FileTransferConstantes.PROPERTY_KEY_ROL_INTERNO)
									|| role.equals(FileTransferConstantes.PROPERTY_KEY_ROL_CONSULTA));
			
			String[] roles = _context.getUserRoles();
			infoLog.append(roles[0]+" :: "+_context.getUsername()+" :: getPedidosFiltrados");
			//logger.info(infoLog.toString());
			
			//1.BASE COMUN
			if(isExterno){
				//EL EXTERNO AQUI NO PUEDE ENTRAR, PERO POR SI LO INTENTA
//				String sql4Externos = usuarioDAO.getSqlPedidos(isExterno);
//				sqlSelectMttoPV.append(sql4Externos);
//				params.add(_context.getUsuarioNi());
			} else {
				String sql4NoExternos = usuarioDAO.getSqlPedidos(isExterno);
				//Inyectamos el alias 't' dinámicamente para evitar ambigüedades
				String vistaName = PedidoProveedorDAOImpCons.BD_VISTA_FCTPDNPRV;
				sqlSelectMttoPV.append(sql4NoExternos.replace("FROM "+vistaName, "FROM "+vistaName+" t"));
			}
			
			//2.FILTROS
			//Filtro Expediente - Para todos excepto Externos
			if(!isExterno && _expCod != null && !_expCod.isEmpty()){
				sqlSelectMttoPV.append(" AND UPPER(TRIM("+PedidoProveedorDAOImpCons.BD_FCTPDNPRV_EXPEDIENTE+")) LIKE ? ");
				String expCodUpper = _expCod.toUpperCase() + "%";
				params.add(expCodUpper);
				infoLog.append(" :: byExp:"+_expCod);
			}
			
			//Filtro Track - Para todos excepto Externos
			if(!isExterno && _track != null && !_track.isEmpty()){
				sqlSelectMttoPV.append(" AND UPPER(TRIM("+PedidoProveedorDAOImpCons.BD_FCTPDNPRV_TR+")) LIKE ? ");
				String trackUpper = _track.trim().toUpperCase() + "%";
				params.add(trackUpper);
				infoLog.append(" :: byTrack:"+_track);
			}
			
			//Filtro Proveedor - Para todos excepto Externos
			if(!isExterno && _provName != null && !_provName.isEmpty()){
				String provUpper = _provName.toUpperCase() + "%";
				//Obtenemos TODOS los códigos que coinciden con el nombre
				List<String> clicods = usuarioDAO.getCLICODbyProv(provUpper);
				if(clicods.isEmpty()){
					sqlSelectMttoPV.append(" AND PROVEEDOR IN ?");
					params.add(provUpper);
				} else {
					//Construimos el "IN (?, ?, ?)" dinámicamente
					String placeholders = clicods.stream().map(c -> "?").collect(Collectors.joining(","));
					sqlSelectMttoPV.append(" AND PROVEEDOR IN (" +placeholders+ ")");
					params.addAll(clicods);
				}
				infoLog.append(" :: byProvName:"+_provName);
			}
			
			//Filtro Ref.Carga - Disponible para todo tipo de user
			if(_refCharge != null && !_refCharge.isEmpty()){
				sqlSelectMttoPV.append(" AND UPPER(TRIM("+PedidoProveedorDAOImpCons.BD_FCTPDNPRV_RCARGA+")) LIKE ? ");
				String rCargaUpper = _refCharge.trim().toUpperCase() + "%";
				params.add(rCargaUpper);
				infoLog.append(" :: byRefCarga:"+_refCharge);
			}
			
			//Filtro F.Entrega.Real - Desde/Hasta - Disponible para todo tipo de user
			if(_fecDesde != null && !_fecDesde.isEmpty() && _fecHasta != null && !_fecHasta.isEmpty()){
				sqlSelectMttoPV.append(" AND " +PedidoProveedorDAOImpCons.BD_FCTPDNPRV_FECREAL+ " BETWEEN ? AND ? ");
				String fDesdeTimeStamp = _fecDesde.trim() + " 00:00:00.000000";
				String fHastaTimeStamp = _fecHasta.trim() + " 23:59:59.000000";
				params.add(fDesdeTimeStamp);
				params.add(fHastaTimeStamp);
				infoLog.append(" :: byFecRealDesde:" + _fecDesde + " Hasta:" + _fecHasta);
			}
			
			//3.FINAL COMUN
			sqlSelectMttoPV.append(" AND (").append(condicionEstadosValidador).append(") ");
			sqlSelectMttoPV.append(" AND t.").append(condicionAnulados);
			sqlSelectMttoPV.append(condicionOrderValidador);
			logger.info(infoLog.toString());
			
			lista = this.jdbcTemplate.query(sqlSelectMttoPV.toString(), params.toArray(), new PedidoProveedorDAOImpMapper());
			lista = tratarListaPVal(lista);
			return lista;
		} catch (DataAccessException e) {
			logger.error("getPedidosFiltrados: {}", e.getMessage());
			throw new PedidosValidadorException(DAOException.SQLERRCODE_QUERY, e.getMessage());	
		}
	}
	
	//Tratamiento de algunos datos para mostralos en pantalla
	private List<PedidoProveedorVO> tratarListaPVal(List<PedidoProveedorVO> listaOriginal) throws PedidoFilesException {
		if(listaOriginal == null || listaOriginal.isEmpty()) return new ArrayList<>();
		
		//1.Limpiamos duplicados
		List<PedidoProveedorVO> listaFiltrada = TratarDatosUtils.tratarDuplicados(listaOriginal);
		
		//Caché temporal para no repetir consultas al mismo proveedor en esta ejecución
        Map<String, String> cachePActiv = new HashMap<>();
		//Iterador para la lista
        Iterator<PedidoProveedorVO> it = listaFiltrada.iterator();
        
        //2.Procesamiento de datos y recuperación de estados desde FTARCHI
		while(it.hasNext()) {
			PedidoProveedorVO pedido = it.next();
			//Si hay hermanos 'rezagados' con 0,3 lo sacamos del validador
			if (pedido.isGrupaje()){
				int rezagados = pedidoFilesDAO.countHermanosEnActivos(pedido.getGrupTR());
				if (rezagados > 0){
					it.remove();
					continue; 		//Saltamos al siguiente registro sin evaluar el resto de condiciones
				}
			}
			TratarDatosUtils.tratarOrigenDestino(pedido);
			TratarDatosUtils.tratarProvUser(pedido, cachePActiv, this.usuarioDAO);
			TratarDatosUtils.tratarGrupajesInicio(pedido);
			TratarDatosUtils.tratarFechasCargaDescarga(pedido);
			
			PedidoFilesVO pFiles = pedidoFilesDAO.getPedidoFiles(pedido.getTrack(), pedido.getExpediente());
			TratarDatosUtils.tratarMotRechazo(pedido, pFiles);
			TratarDatosUtils.tratarFechasSubida(pedido, pFiles);
			TratarDatosUtils.refreshPedido(pedido, pFiles);
		}
		
		//3.Tratar Grupajes con 7/7 o x/7
		TratarDatosUtils.tratarGrupSietesPValidador(listaFiltrada, this.pedidoFilesDAO);
		
		//4.Ordenar lista por la fecha de subdia del CMR :: F1FECCM1
		TratarDatosUtils.ordenarPorFechaSubidaCompleta(listaFiltrada);
		
		//5.Reordenación para grupajes: manteniendo el orden de aparición original
		return TratarDatosUtils.reordenarGrup(listaFiltrada);
	}
	
}
