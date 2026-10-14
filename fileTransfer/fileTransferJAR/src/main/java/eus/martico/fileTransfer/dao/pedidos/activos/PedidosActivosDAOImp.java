package eus.martico.fileTransfer.dao.pedidos.activos;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.dao.BaseGenDAO;
import eus.martico.fileTransfer.dao.DAOException;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesDAOImpCons;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesIDAO;
import eus.martico.fileTransfer.dao.usuario.UsuarioIDAO;
import eus.martico.fileTransfer.model.pedido.files.PedidoFilesVO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorDAOImpCons;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorDAOImpMapper;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorVO;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.sql.SqlUtils;
import eus.martico.fileTransfer.utils.TratarDatosUtils;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@Service(value = "PedidosActivosIDAO")
public class PedidosActivosDAOImp extends BaseGenDAO<PedidoProveedorVO> implements PedidosActivosIDAO {
	
	protected static final Logger logger = LoggerFactory.getLogger(PedidosActivosDAOImp.class);
	
	@Autowired
	PedidoFilesIDAO pedidoFilesDAO;
	
	@Autowired
	UsuarioIDAO usuarioDAO;	
	
	
	
	private static StringBuilder sqlInsertFTarchi;
	private static StringBuilder sqlUpdateFecREAL;
	
	
	//Pedidos NO validados o CMRs o FACs que NoAplican
	//... AND (ESTADO != 2 OR E_CMR = 7 OR E_FAC = 7)
	private static StringBuilder condicionEstadosActivos = 
			new StringBuilder(
					PedidoProveedorDAOImpCons.BD_FCTPDNPRV_ESTADO
					+ " != " + FileTransferConstantes.ESTADO_2_FICH_VALIDADO
					+ " OR "
					+ PedidoProveedorDAOImpCons.BD_FCTPDNPRV_ECMR
					+ " = " + FileTransferConstantes.ESTADO_7_FICH_SIN_DOC
					+ " OR "
					+ PedidoProveedorDAOImpCons.BD_FCTPDNPRV_EFAC
					+ " = " + FileTransferConstantes.ESTADO_7_FICH_SIN_DOC
			);
	
	private static StringBuilder condicionOrderActivos = 
			new StringBuilder(
					" ORDER BY F_CARGA DESC "
					+ " FETCH FIRST 1000 ROWS ONLY "
			);

	static {
		
		List<String> camposInsert = new ArrayList<String>(Arrays.asList(
				PedidoFilesDAOImpCons.BD_FTARCHI_REFPED,
				PedidoFilesDAOImpCons.BD_FTARCHI_EXPEDI,
				PedidoFilesDAOImpCons.BD_FTARCHI_ESTADO,
				PedidoFilesDAOImpCons.BD_FTARCHI_ANOCMR,
				PedidoFilesDAOImpCons.BD_FTARCHI_FECENT,
				PedidoFilesDAOImpCons.BD_FTARCHI_RUTCMR,
				PedidoFilesDAOImpCons.BD_FTARCHI_USECMR,
				PedidoFilesDAOImpCons.BD_FTARCHI_ESTCMR,
				PedidoFilesDAOImpCons.BD_FTARCHI_FECCM1,
				PedidoFilesDAOImpCons.BD_FTARCHI_FECCM2,
				PedidoFilesDAOImpCons.BD_FTARCHI_USUCMR,
				PedidoFilesDAOImpCons.BD_FTARCHI_MOTREC,
				PedidoFilesDAOImpCons.BD_FTARCHI_RUTFAC,
				PedidoFilesDAOImpCons.BD_FTARCHI_USEFAC,
				PedidoFilesDAOImpCons.BD_FTARCHI_ESTFAC,
				PedidoFilesDAOImpCons.BD_FTARCHI_FECFA1,
				PedidoFilesDAOImpCons.BD_FTARCHI_FECFA2,
				PedidoFilesDAOImpCons.BD_FTARCHI_USUFAC,
				PedidoFilesDAOImpCons.BD_FTARCHI_RUTOTR,
				PedidoFilesDAOImpCons.BD_FTARCHI_USEOTR,
				PedidoFilesDAOImpCons.BD_FTARCHI_FECOT1
				));
		
		List<String> camposInsertFTarchi = new ArrayList<String>(camposInsert);

		List<String> camposUpdateFReal = new ArrayList<String>(Arrays.asList(
				PedidoFilesDAOImpCons.BD_FTARCHI_FECENT
				));
		
		sqlInsertFTarchi = SqlUtils.generarSqlInsert(PedidoFilesDAOImpCons.BD_TABLA_FTARCHI, camposInsertFTarchi);

		sqlUpdateFecREAL = SqlUtils.generarSqlUpdate(PedidoFilesDAOImpCons.BD_TABLA_FTARCHI, camposUpdateFReal, 
				PedidoFilesDAOImpCons.BD_FTARCHI_REFPED, PedidoFilesDAOImpCons.BD_FTARCHI_EXPEDI);

	}
	
	
	@Override
	public Integer insertFecREAL(final PedidoFilesVO _pedidoF) throws PedidoFilesException {
		//Registro INICIAL en FTARCHI
		logger.info("insertFecREAL :: REFPED: {} : ESTADO: {} : FECENT: {}", _pedidoF.getRefPed(), _pedidoF.getEstadoFacturacion(), _pedidoF.getFechaEntReal());
		
		try {
			//CHAPUZAS HEREDADAS VARIAS: W2USUARIO precision 40; F1USECMR, F1USEFAC, F1USEOTR, todos precision 10
			final String usuFTarchi;
			if(_pedidoF.getUsuSubidaCMR().length()>10){
				usuFTarchi = _pedidoF.getUsuSubidaCMR().substring(0, 10);
			} else {
				usuFTarchi = _pedidoF.getUsuSubidaCMR();
			}
			int x = this.jdbcTemplate.update(sqlInsertFTarchi.toString(),
					new PreparedStatementSetter(){
				public void setValues(PreparedStatement ps)
						throws SQLException {
					int i=1;
					ps.setString(i++, _pedidoF.getRefPed());
					ps.setString(i++, _pedidoF.getExpediente());
					ps.setLong(i++, _pedidoF.getEstadoFacturacion());
					ps.setLong(i++, _pedidoF.getAnotaciones());
					SqlUtils.setLocalDateTimeToPs(ps, i++, _pedidoF.getFechaEntReal());

					ps.setString(i++, _pedidoF.getRutaCMR());
					ps.setString(i++, usuFTarchi);
					ps.setLong(i++, _pedidoF.getEstadoCMR());
					SqlUtils.setLocalDateTimeToPs(ps, i++, _pedidoF.getFechaSubidaCMR());
					SqlUtils.setLocalDateTimeToPs(ps, i++, _pedidoF.getFechaEstadoCMR());
					ps.setString(i++, _pedidoF.getUsuOkCMR());
					ps.setString(i++, _pedidoF.getMotivoRechazo());

					ps.setString(i++, _pedidoF.getRutaFAC());
					ps.setString(i++, _pedidoF.getUsuSubidaFAC());
					ps.setLong(i++, _pedidoF.getEstadoFAC());
					SqlUtils.setLocalDateTimeToPs(ps, i++, _pedidoF.getFechaSubidaFAC());
					SqlUtils.setLocalDateTimeToPs(ps, i++, _pedidoF.getFechaEstadoFAC());
					ps.setString(i++, _pedidoF.getUsuOkFAC());

					ps.setString(i++, _pedidoF.getRutaOTR());
					ps.setString(i++, _pedidoF.getUsuSubidaOTR());
					SqlUtils.setLocalDateTimeToPs(ps, i++, _pedidoF.getFechaSubidaOTR());
				}
			});
			return new Integer(x);
		} catch (DataAccessException e){
			logger.error("insertFecREAL", e.getMessage());
			throw new PedidoFilesException(DAOException.SQLERRCODE_INSERT, e.getMessage());
		}
	}
	
	@Override
	public Integer updateFecREAL(MyServiceContext _context, String _pedidoTrack, String _pedidoExp, LocalDateTime _fechaEntReal) throws PedidoFilesException {
		logger.info("updateFecREAL :: REFPED: {} : EXPEDI: {} : FECENT: {}", _pedidoTrack, _pedidoExp, _fechaEntReal);
		try {
			int x = this.jdbcTemplate.update(sqlUpdateFecREAL.toString(),
					new PreparedStatementSetter(){
				public void setValues(PreparedStatement ps)
						throws SQLException {
					int i=1;
					SqlUtils.setLocalDateTimeToPs(ps, i++, _fechaEntReal);

					ps.setString(i++, _pedidoTrack);
					ps.setString(i++, _pedidoExp);
				}
			});
			return new Integer(x);
		} catch (DataAccessException e) {
			logger.error("updateFecREAL", e.getMessage());
			throw new PedidoFilesException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}
	
	/**
	 * anularPedido
	 * 
	 * @param _context MyServiceContext
	 * @param _pedidoTrack String
	 * @param _pedidoExp String
	 * @param _motivo String
	 * 
	 * @return int
	 * @throws PedidoFilesException
	 */
	@Override
	public Integer anularPedido(MyServiceContext _context, String _pedidoTrack, String _pedidoExp, String _motivo) throws PedidoFilesException {
		logger.info("anularPedido :: TR:{} :: EXP:{}", _pedidoTrack, _pedidoExp);
		//Desde el FRONT: NO sa podrá anular si ya tiene la factura en verde, F1ESTFAC=2
		int z = 0;
		try {
			PedidoFilesVO pedidoReg = pedidoFilesDAO.getPedidoFiles(_pedidoTrack, _pedidoExp);
			PedidoFilesVO pAux = pedidoReg;
			//Si no tiene registro en FTARCHI no tendrá docs
			if (pedidoReg == null){
				//Hago un registro inicial sin fechaEntReal
				LocalDateTime fecEntRealDate = LocalDateTime.parse("0000-01-01T00:00:00");
				pAux = pedidoFilesDAO.setPedidoInicial(_context, _pedidoTrack, _pedidoExp, fecEntRealDate);
				insertFecREAL(pAux);
			}
			
			//Ahora ya existe registro en FTARCHI
			//Se modifica solo el estado general = 9, sin tocar las rutas para mantener lo que hubiese
			//y el estado de la fac = 9 solo si ya tiene un 5 para que no salga en AS400
			//al reactivar se mantiene todo, salvo si la fac tiene 9 pasa a 5 
			//y se recalcula el estado general
			
			pAux.setFechaEstadoCMR(LocalDateTime.now());
			pAux.setUsuOkCMR(_context.getUsername());
			
			pAux.setMotivoRechazo("Pedido deshabilitado: " +_motivo);
			pAux.setAnotaciones(FileTransferConstantes.CON_ANOTACIONES);
			PedidoFilesVO pf = pedidoFilesDAO.updateCMR(pAux);
			
			Long estCMR = new Long(pAux.getEstadoCMR());
			Long estFAC = new Long(0);
			if(pAux.getEstadoFAC().equals(FileTransferConstantes.ESTADO_5_FAC_SIN_CONTABILIZAR)){
				estFAC  = new Long(FileTransferConstantes.ESTADO_9_PEDIDO_NULO);
			} else {
				estFAC = new Long(pAux.getEstadoFAC());
			}
        	Long estGEN = new Long(FileTransferConstantes.ESTADO_9_PEDIDO_NULO);
			z = pedidoFilesDAO.updateEstados(pf.getRefPed(), pf.getExpediente(), estCMR, estFAC, estGEN);
			
			return new Integer(z);
		} catch (PedidoFilesException ex){
			logger.error("anularPedido: ", ex.getMessage());
			throw new PedidoFilesException(DAOException.SQLERRCODE_QUERY, ex.getMessage());
		}
	}
	
	@Override
	public List<PedidoProveedorVO> getPedidosFiltrados(MyServiceContext _context, String _expCod, String _provName, String _track, String _refCharge, String _fecDesde, String _fecHasta) 
			throws PedidosActivosException, PedidoFilesException {
		//UPPER ACUMULATIVE DYNAMIC FILTER
		try{
			List<PedidoProveedorVO> lista = new ArrayList<>();
			StringBuilder sqlSelectMttoPA = new StringBuilder();
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
				sqlSelectMttoPA.append(sql4Externos);
				params.add(_context.getUsuarioNi());
			} else {
				String sql4NoExternos = usuarioDAO.getSqlPedidos(isExterno);
				sqlSelectMttoPA.append(sql4NoExternos);
			}
			
			//2.FILTROS DINAMICOS ACUMULATIVOS UPPER
			//Filtro Expediente - Disponible para todo tipo de user
			if(_expCod != null && !_expCod.isEmpty()){
				sqlSelectMttoPA.append(" AND UPPER(TRIM("+PedidoProveedorDAOImpCons.BD_FCTPDNPRV_EXPEDIENTE+")) LIKE ? ");
				String expCodUpper = _expCod.toUpperCase() + "%";
				params.add(expCodUpper);
				infoLog.append(" :: byExp:"+_expCod);
			}
			
			//Filtro Track - Disponible para todo tipo de user
			if(_track != null && !_track.isEmpty()){
				sqlSelectMttoPA.append(" AND UPPER(TRIM("+PedidoProveedorDAOImpCons.BD_FCTPDNPRV_TR+")) LIKE ? ");
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
					sqlSelectMttoPA.append(" AND PROVEEDOR IN ?");
					params.add(provUpper);
				} else {
					//Construimos el "IN (?, ?, ?)" dinámicamente
					String placeholders = clicods.stream().map(c -> "?").collect(Collectors.joining(","));
					sqlSelectMttoPA.append(" AND PROVEEDOR IN (" +placeholders+ ")");
					params.addAll(clicods);
				}
				infoLog.append(" :: byProvName:"+_provName);
			}
			
			//Filtro Ref.Carga - Disponible para todo tipo de user
			if(_refCharge != null && !_refCharge.isEmpty()){
				sqlSelectMttoPA.append(" AND UPPER(TRIM("+PedidoProveedorDAOImpCons.BD_FCTPDNPRV_RCARGA+")) LIKE ? ");
				String rCargaUpper = _refCharge.trim().toUpperCase() + "%";
				params.add(rCargaUpper);
				infoLog.append(" :: byRefCarga:"+_refCharge);
			}
			
			//Filtro F.Entrega.Real - Desde/Hasta - Disponible para todo tipo de user
			if(_fecDesde != null && !_fecDesde.isEmpty() && _fecHasta != null && !_fecHasta.isEmpty()){
				sqlSelectMttoPA.append(" AND " +PedidoProveedorDAOImpCons.BD_FCTPDNPRV_FECREAL+ " BETWEEN ? AND ? ");
				String fDesdeTimeStamp = _fecDesde.trim() + " 00:00:00.000000";
				String fHastaTimeStamp = _fecHasta.trim() + " 23:59:59.000000";
				params.add(fDesdeTimeStamp);
				params.add(fHastaTimeStamp);
				infoLog.append(" :: byFecRealDesde:" + _fecDesde + " Hasta:" + _fecHasta);
			}
			
			//3.FINAL COMUN
			sqlSelectMttoPA.append(" AND (").append(condicionEstadosActivos).append(") ");
			sqlSelectMttoPA.append(condicionOrderActivos);
			logger.info(infoLog.toString());
			
			lista = this.jdbcTemplate.query(sqlSelectMttoPA.toString(), params.toArray(), new PedidoProveedorDAOImpMapper());
			lista = tratarListaPActiv(lista);
			return lista;
		} catch (DataAccessException e) {
			logger.error("getPedidosFiltrados: {}", e.getMessage());
			throw new PedidosActivosException(DAOException.SQLERRCODE_QUERY, e.getMessage());	
		}
	}

	//Tratamiento de algunos datos para mostralos en pantalla
	private List<PedidoProveedorVO> tratarListaPActiv(List<PedidoProveedorVO> listaOriginal) throws PedidoFilesException {
		if(listaOriginal == null || listaOriginal.isEmpty()) return new ArrayList<>();
		
		//1.Limpiamos duplicados
		List<PedidoProveedorVO> listaSinDuplicados = TratarDatosUtils.tratarDuplicados(listaOriginal);
		
		//2.1.Limpiar los anulados
		List<PedidoProveedorVO> listaSinAnulados = TratarDatosUtils.tratarDeshabilitados(listaSinDuplicados);
		//2.2.Limpiar anteriores a Marzo
		List<PedidoProveedorVO> listaFiltrada = TratarDatosUtils.tratarFechaCorte(listaSinAnulados);
		
		//Caché temporal para no repetir consultas al mismo proveedor en esta ejecución
        Map<String, String> cachePActiv = new HashMap<>();
		
		//3.Procesamiento de datos y recuperación de estados desde FTARCHI
		for(PedidoProveedorVO pedido : listaFiltrada) {
			TratarDatosUtils.tratarOrigenDestino(pedido);
			TratarDatosUtils.tratarProvUser(pedido, cachePActiv, this.usuarioDAO);
			TratarDatosUtils.tratarGrupajesInicio(pedido);
			TratarDatosUtils.tratarFechasCargaDescarga(pedido);
			
			PedidoFilesVO pFiles = pedidoFilesDAO.getPedidoFiles(pedido.getTrack(), pedido.getExpediente());
			TratarDatosUtils.tratarMotRechazo(pedido, pFiles);			
			TratarDatosUtils.refreshPedido(pedido, pFiles);
		}
		
		//4.Tratar Grupajes con 7/7 o x/7	
		TratarDatosUtils.tratarGrupSietesPActivos(listaFiltrada, this.pedidoFilesDAO);
		
		//5.Reordenación para grupajes: manteniendo el orden de aparición original
		return TratarDatosUtils.reordenarGrup(listaFiltrada);
	}
	
}
