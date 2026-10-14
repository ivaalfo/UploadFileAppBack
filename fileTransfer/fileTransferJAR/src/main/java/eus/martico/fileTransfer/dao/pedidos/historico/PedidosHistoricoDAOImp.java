package eus.martico.fileTransfer.dao.pedidos.historico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import eus.martico.fileTransfer.model.pedido.factura.PedidoFacturaDAOImpCons;
import eus.martico.fileTransfer.model.pedido.files.PedidoFilesVO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorDAOImpCons;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorDAOImpMapper;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorVO;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.utils.FileTransferUtils;
import eus.martico.fileTransfer.utils.TratarDatosUtils;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@Service(value = "PedidosHistoricoIDAO")
public class PedidosHistoricoDAOImp extends BaseGenDAO<PedidoFilesVO> implements PedidosHistoricoIDAO {

	protected static final Logger logger = LoggerFactory.getLogger(PedidosHistoricoDAOImp.class);
	
	@Autowired
	PedidoFilesIDAO pedidoFilesDAO;
	
	@Autowired
	UsuarioIDAO usuarioDAO;

	

	private static StringBuilder sqlFacturaFilter = 
			new StringBuilder(
					"SELECT 1 FROM "
							+ PedidoFacturaDAOImpCons.BD_TABLA_FTRCONTA
							+ " f "
							+ "WHERE f.F2FACTEX LIKE ? "
							+ "AND f.F2TR = FCTPDNPRV.TR "
							+ "AND f.F2EXPE = FCTPDNPRV.EXPEDIENTE"
					);
	
	private static StringBuilder sqlFacturaNoContaFilter = 
			new StringBuilder(
					"SELECT 1 FROM FAIMPINV fnc "
		                    + "WHERE REPLACE(TRIM(CHAR(fnc.INVOICE_NUMBER)), ' ', '') LIKE ? "
		                    + "AND fnc.DOCCOMMENT = FCTPDNPRV.TR"
					);

	
	private static StringBuilder condicionEstados = 	//Traemos el 1 para que el 7 sepa si debe esconderse
			new StringBuilder(							//AND (E_CMR = 2 OR E_CMR = 7 OR E_CMR = 1)
					PedidoProveedorDAOImpCons.BD_FCTPDNPRV_ECMR
					+ " = "+FileTransferConstantes.ESTADO_2_FICH_VALIDADO
					+ " OR "
					+ PedidoProveedorDAOImpCons.BD_FCTPDNPRV_ECMR
					+ " = "+FileTransferConstantes.ESTADO_7_FICH_SIN_DOC
					+ " OR "
					+ PedidoProveedorDAOImpCons.BD_FCTPDNPRV_ECMR
					+ " = "+FileTransferConstantes.ESTADO_1_FICH_SUBIDO
					+ " OR "
					+ PedidoProveedorDAOImpCons.BD_FCTPDNPRV_ESTADO
					+ " = "+FileTransferConstantes.ESTADO_9_PEDIDO_NULO
			);

	
	private static StringBuilder condicionOrder = 
			new StringBuilder(
					" ORDER BY F_CARGA DESC "
					+ " FETCH FIRST 1000 ROWS ONLY "
			);
	
	
	/**
	 * reactivarPedido
	 * 
	 * @param _context MyServiceContext
	 * @param _pedidoTrack String
	 * @param _pedidoExp String
	 * 
	 * @return int
	 * @throws PedidoFilesException
	 */
	@Override
	public Integer reactivarPedido(MyServiceContext _context, String _pedidoTrack, String _pedidoExp) throws PedidoFilesException {
		logger.info("reactivarPedido :: TR:{} :: EXP:{}", _pedidoTrack, _pedidoExp);
		int z = 0;
		try {
			PedidoFilesVO pedidoReg = pedidoFilesDAO.getPedidoFiles(_pedidoTrack, _pedidoExp);
			PedidoFilesVO pAux = pedidoReg;
			//Se modifica solo el estado general = 9, sin tocar las rutas para mantener lo que hubiese
			//y el estado de la fac = 9 solo si ya tiene un 5 para que no salga en AS400
			//al reactivar se mantiene todo, salvo si la fac tiene 9 pasa a 5 
			//y se recalcula el estado general
			Integer estCMR = pedidoReg.getEstadoCMR();
			Integer estFAC = 0;
			if(pedidoReg.getEstadoFAC().equals(FileTransferConstantes.ESTADO_9_PEDIDO_NULO)){
				estFAC  = FileTransferConstantes.ESTADO_5_FAC_SIN_CONTABILIZAR;
			} else {
				estFAC = pedidoReg.getEstadoFAC();
			}
			pAux.setEstadoFAC(estFAC);
        	int estGEN = FileTransferUtils.determinarEstadoGeneral(pAux, _pedidoTrack, _pedidoExp);
			
			pAux.setMotivoRechazo("Pedido reactivado desde DFC");
			pAux.setAnotaciones(FileTransferConstantes.CON_ANOTACIONES);
			pedidoFilesDAO.updateCMR(pAux);
			
			z = pedidoFilesDAO.updateEstados(_pedidoTrack, _pedidoExp, new Long(estCMR), new Long(estFAC), new Long(estGEN));
			
			return new Integer(z);
		} catch (PedidoFilesException ex){
			logger.error("reactivarPedido: ", ex.getMessage());
			throw new PedidoFilesException(DAOException.SQLERRCODE_QUERY, ex.getMessage());
		}
	}
	
	@Override
	public List<PedidoProveedorVO> getPedidosFiltrados(MyServiceContext _context, String _expCod, String _provName, String _track, String _facNum, String _refCharge, String _fecDesde, String _fecHasta) 
			throws PedidosHistoricoException, PedidoFilesException {
		//UPPER ACUMULATIVE DYNAMIC FILTER
		try{
			List<PedidoProveedorVO> lista = new ArrayList<>();
			StringBuilder sqlSelectMttoPH = new StringBuilder();
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
				String sql4Externos = usuarioDAO.getSqlPedidos(isExterno);
				sqlSelectMttoPH.append(sql4Externos);
				params.add(_context.getUsuarioNi());
			} else {
				String sql4NoExternos = usuarioDAO.getSqlPedidos(isExterno);
				sqlSelectMttoPH.append(sql4NoExternos);
			}
			
			//2.FILTROS DINAMICOS
			//Filtro Expediente - Disponible para todo tipo de user
			if(_expCod != null && !_expCod.isEmpty()){
				sqlSelectMttoPH.append(" AND UPPER(TRIM("+PedidoProveedorDAOImpCons.BD_FCTPDNPRV_EXPEDIENTE+")) LIKE ? ");
				String expCodUpper = _expCod.toUpperCase() + "%";
				params.add(expCodUpper);
				infoLog.append(" :: byExp:"+_expCod);
			}
			
			//Filtro Track - Disponible para todo tipo de user
			if(_track != null && !_track.isEmpty()){
				sqlSelectMttoPH.append(" AND UPPER(TRIM("+PedidoProveedorDAOImpCons.BD_FCTPDNPRV_TR+")) LIKE ? ");
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
					sqlSelectMttoPH.append(" AND PROVEEDOR IN ?");
					params.add(provUpper);
				} else {
					//Construimos el "IN (?, ?, ?)" dinámicamente
					String placeholders = clicods.stream().map(c -> "?").collect(Collectors.joining(","));
					sqlSelectMttoPH.append(" AND PROVEEDOR IN (" +placeholders+ ")");
					params.addAll(clicods);
				}
				infoLog.append(" :: byProvName:"+_provName);
			}
			
			//Filtro Factura - Para todos excepto Externos
			if(!isExterno && _facNum != null && !_facNum.isEmpty()){
				//OJO las (/) son reemplazadas en el FRONT por (~)
				_facNum = _facNum.replaceAll("~", "/");
				sqlSelectMttoPH.append(" AND (");
				
				//1.Consulta en FTRCONTA FACs contabilizadas				
				sqlSelectMttoPH.append("EXISTS (").append(sqlFacturaFilter).append(") ");
				//EXISTS Se supone mas rapido que recorrer con un for casando FTRCONTA con FCTPDNPRV
				
				//2.Consulta en FAIMPINV FACs NO contabilizadas
				sqlSelectMttoPH.append(" OR EXISTS (").append(sqlFacturaNoContaFilter).append(") ");
				sqlSelectMttoPH.append(") ");
				
				String facCodUpper = _facNum.toUpperCase() + "%";
				params.add(facCodUpper);
				params.add(facCodUpper);
				infoLog.append(" :: byFacNum (Conta y No Conta): "+_facNum);
			}
			
			//Filtro Ref.Carga - Disponible para todo tipo de user
			if(_refCharge != null && !_refCharge.isEmpty()){
				sqlSelectMttoPH.append(" AND UPPER(TRIM("+PedidoProveedorDAOImpCons.BD_FCTPDNPRV_RCARGA+")) LIKE ? ");
				String rCargaUpper = _refCharge.trim().toUpperCase() + "%";
				params.add(rCargaUpper);
				infoLog.append(" :: byRefCarga:"+_refCharge);
			}
			
			//Filtro F.Entrega.Real - Desde/Hasta - Disponible para todo tipo de user
			if(_fecDesde != null && !_fecDesde.isEmpty() && _fecHasta != null && !_fecHasta.isEmpty()){
				sqlSelectMttoPH.append(" AND " +PedidoProveedorDAOImpCons.BD_FCTPDNPRV_FECREAL+ " BETWEEN ? AND ? ");
				String fDesdeTimeStamp = _fecDesde.trim() + " 00:00:00.000000";
				String fHastaTimeStamp = _fecHasta.trim() + " 23:59:59.000000";
				params.add(fDesdeTimeStamp);
				params.add(fHastaTimeStamp);
				infoLog.append(" :: byFecRealDesde:" + _fecDesde + " Hasta:" + _fecHasta);
			}
			
			//3.FINAL COMUN
			sqlSelectMttoPH.append(" AND (").append(condicionEstados).append(") ");
			sqlSelectMttoPH.append(condicionOrder);
			logger.info(infoLog.toString());
			
			lista = this.jdbcTemplate.query(sqlSelectMttoPH.toString(), params.toArray(), new PedidoProveedorDAOImpMapper());
			lista = tratarListaPHist(lista);
			return lista;
		} catch (DataAccessException e) {
			logger.error("getPedidosFiltrados: {}", e.getMessage());
			throw new PedidosHistoricoException(DAOException.SQLERRCODE_QUERY, e.getMessage());	
		}
	}
	
	//Tratamiento de algunos datos para mostralos en pantalla
	private List<PedidoProveedorVO> tratarListaPHist(List<PedidoProveedorVO> listaOriginal) throws PedidoFilesException {
		if(listaOriginal == null || listaOriginal.isEmpty()) return new ArrayList<>();
		
		//1.Limpiamos duplicados
		List<PedidoProveedorVO> listaFiltrada = TratarDatosUtils.tratarDuplicados(listaOriginal);
		
		//Caché temporal para no repetir consultas al mismo proveedor en esta ejecución
        Map<String, String> cachePActiv = new HashMap<>();
        
		//2.Procesamiento de datos y recuperación de estados desde FTARCHI
		for(PedidoProveedorVO pedido : listaFiltrada) {
			TratarDatosUtils.tratarOrigenDestino(pedido);
			TratarDatosUtils.tratarProvUser(pedido, cachePActiv, this.usuarioDAO);
			TratarDatosUtils.tratarGrupajesInicio(pedido);
			TratarDatosUtils.tratarFechasCargaDescarga(pedido);
			
			PedidoFilesVO pFiles = pedidoFilesDAO.getPedidoFiles(pedido.getTrack(), pedido.getExpediente());
			TratarDatosUtils.tratarMotRechazo(pedido, pFiles);
			TratarDatosUtils.tratarFechasSubida(pedido, pFiles);
			TratarDatosUtils.obtenerFacNumber(pedido, this.jdbcTemplate);
			TratarDatosUtils.obtenerFacAmount(pedido, this.jdbcTemplate);
			TratarDatosUtils.refreshPedido(pedido, pFiles);
		}
		
		//3.Tratar Grupajes con 7/7 o 7/x
		TratarDatosUtils.tratarGrupSietesPHistorico(listaFiltrada, this.pedidoFilesDAO);
		
		//4.Reordenación para grupajes: manteniendo el orden de aparición original
		return TratarDatosUtils.reordenarGrup(listaFiltrada);
	}

}
