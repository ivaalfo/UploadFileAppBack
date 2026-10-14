package eus.martico.fileTransfer.dao.pedido.files;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.dao.BaseGenDAO;
import eus.martico.fileTransfer.dao.DAOException;
import eus.martico.fileTransfer.dao.pedidos.activos.PedidosActivosIDAO;
import eus.martico.fileTransfer.model.pedido.files.PedidoFilesVO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoGrupajeVO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoGrupajeVO.GrupajeItem;
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
@Service(value = "PedidoFilesIDAO")
public class PedidoFilesDAOImp extends BaseGenDAO<PedidoFilesVO> implements PedidoFilesIDAO {

	protected static final Logger logger = LoggerFactory.getLogger(PedidoFilesDAOImp.class);
	
	@Autowired
	PedidosActivosIDAO pedidosActivosDAO;


	private static StringBuilder sqlSelectAll = new StringBuilder("SELECT * FROM ");
	private static StringBuilder sqlSelectPedidoByTrAndExp;
	private static StringBuilder sqlSelectPFilesByTrAndExp;
	
	private static StringBuilder sqlUpdateCMR;
	private static StringBuilder sqlUpdateFAC;
	private static StringBuilder sqlUpdateOTR;
	private static StringBuilder sqlUpdateEstado;
	private static StringBuilder sqlUpdateAnot;
	
	private static StringBuilder sqlUpdateGrupEstados;
	private static StringBuilder sqlSelectInVistaByGrup;
	
	
	static {
		
		sqlSelectPedidoByTrAndExp = new StringBuilder("SELECT distinct * FROM ");
		sqlSelectPedidoByTrAndExp.append(PedidoProveedorDAOImpCons.BD_VISTA_FCTPDNPRV);
		sqlSelectPedidoByTrAndExp.append(" WHERE ");
		sqlSelectPedidoByTrAndExp.append(" TRIM("+PedidoProveedorDAOImpCons.BD_FCTPDNPRV_TR+")");
		sqlSelectPedidoByTrAndExp.append(" = ? ");
		sqlSelectPedidoByTrAndExp.append(" AND ");
		sqlSelectPedidoByTrAndExp.append(" TRIM("+PedidoProveedorDAOImpCons.BD_FCTPDNPRV_EXPEDIENTE+")");
		sqlSelectPedidoByTrAndExp.append(" = ? ");
		
		List<String> camposUpdateCMR = new ArrayList<String>(Arrays.asList(
				PedidoFilesDAOImpCons.BD_FTARCHI_RUTCMR,
				PedidoFilesDAOImpCons.BD_FTARCHI_USECMR,
				PedidoFilesDAOImpCons.BD_FTARCHI_ESTCMR,
				PedidoFilesDAOImpCons.BD_FTARCHI_FECCM1,
				PedidoFilesDAOImpCons.BD_FTARCHI_FECCM2,
				PedidoFilesDAOImpCons.BD_FTARCHI_USUCMR,
				PedidoFilesDAOImpCons.BD_FTARCHI_MOTREC,
				PedidoFilesDAOImpCons.BD_FTARCHI_ANOCMR
				));

		List<String> camposUpdateFAC = new ArrayList<String>(Arrays.asList(
				PedidoFilesDAOImpCons.BD_FTARCHI_RUTFAC,
				PedidoFilesDAOImpCons.BD_FTARCHI_USEFAC,
				PedidoFilesDAOImpCons.BD_FTARCHI_ESTFAC,
				PedidoFilesDAOImpCons.BD_FTARCHI_FECFA1,
				PedidoFilesDAOImpCons.BD_FTARCHI_FECFA2,
				PedidoFilesDAOImpCons.BD_FTARCHI_USUFAC,
				PedidoFilesDAOImpCons.BD_FTARCHI_MOTREC,
				PedidoFilesDAOImpCons.BD_FTARCHI_ANOCMR
				));

		List<String> camposUpdateOTR = new ArrayList<String>(Arrays.asList(
				PedidoFilesDAOImpCons.BD_FTARCHI_RUTOTR,
				PedidoFilesDAOImpCons.BD_FTARCHI_USEOTR,
				PedidoFilesDAOImpCons.BD_FTARCHI_FECOT1
				));
		
		List<String> camposUpdateESTADO = new ArrayList<String>(Arrays.asList(
				PedidoFilesDAOImpCons.BD_FTARCHI_ESTADO
				));
		
		List<String> camposUpdateAnot = new ArrayList<String>(Arrays.asList(
				PedidoFilesDAOImpCons.BD_FTARCHI_ANOCMR,
				PedidoFilesDAOImpCons.BD_FTARCHI_MOTREC
				));
		
		List<String> camposUpdateGrup = new ArrayList<String>(Arrays.asList(
				PedidoFilesDAOImpCons.BD_FTARCHI_ESTCMR,
				PedidoFilesDAOImpCons.BD_FTARCHI_ESTFAC,
				PedidoFilesDAOImpCons.BD_FTARCHI_ESTADO
				));

		sqlSelectPFilesByTrAndExp = new StringBuilder("SELECT distinct * FROM ");
		sqlSelectPFilesByTrAndExp.append(PedidoFilesDAOImpCons.BD_TABLA_FTARCHI);
		sqlSelectPFilesByTrAndExp.append(" WHERE ");
		sqlSelectPFilesByTrAndExp.append(" TRIM("+PedidoFilesDAOImpCons.BD_FTARCHI_REFPED+")");
		sqlSelectPFilesByTrAndExp.append(" = ? ");
		sqlSelectPFilesByTrAndExp.append(" AND ");
		sqlSelectPFilesByTrAndExp.append(" TRIM("+PedidoFilesDAOImpCons.BD_FTARCHI_EXPEDI+")");
		sqlSelectPFilesByTrAndExp.append(" = ? ");
		
		sqlUpdateCMR = SqlUtils.generarSqlUpdate(PedidoFilesDAOImpCons.BD_TABLA_FTARCHI, camposUpdateCMR, PedidoFilesDAOImpCons.BD_FTARCHI_REFPED, PedidoFilesDAOImpCons.BD_FTARCHI_EXPEDI);
		sqlUpdateFAC = SqlUtils.generarSqlUpdate(PedidoFilesDAOImpCons.BD_TABLA_FTARCHI, camposUpdateFAC, PedidoFilesDAOImpCons.BD_FTARCHI_REFPED, PedidoFilesDAOImpCons.BD_FTARCHI_EXPEDI);
		sqlUpdateOTR = SqlUtils.generarSqlUpdate(PedidoFilesDAOImpCons.BD_TABLA_FTARCHI, camposUpdateOTR, PedidoFilesDAOImpCons.BD_FTARCHI_REFPED, PedidoFilesDAOImpCons.BD_FTARCHI_EXPEDI);
		
		sqlUpdateEstado = SqlUtils.generarSqlUpdate(
				PedidoFilesDAOImpCons.BD_TABLA_FTARCHI, 
				camposUpdateESTADO, 
				PedidoFilesDAOImpCons.BD_FTARCHI_REFPED, 
				PedidoFilesDAOImpCons.BD_FTARCHI_EXPEDI);
		
		sqlUpdateAnot = SqlUtils.generarSqlUpdate(
				PedidoFilesDAOImpCons.BD_TABLA_FTARCHI, 
				camposUpdateAnot, 
				PedidoFilesDAOImpCons.BD_FTARCHI_REFPED, 
				PedidoFilesDAOImpCons.BD_FTARCHI_EXPEDI);
		
		sqlUpdateGrupEstados = SqlUtils.generarSqlUpdate(
				PedidoFilesDAOImpCons.BD_TABLA_FTARCHI, 
				camposUpdateGrup, 
				PedidoFilesDAOImpCons.BD_FTARCHI_REFPED, 
				PedidoFilesDAOImpCons.BD_FTARCHI_EXPEDI);
		
		
		sqlSelectInVistaByGrup = new StringBuilder(sqlSelectAll);
		sqlSelectInVistaByGrup.append(PedidoProveedorDAOImpCons.BD_VISTA_FCTPDNPRV);
		sqlSelectInVistaByGrup.append(" WHERE ");
		sqlSelectInVistaByGrup.append(PedidoProveedorDAOImpCons.BD_FCTPDNPRV_TR);
		sqlSelectInVistaByGrup.append(" LIKE ? ");
		sqlSelectInVistaByGrup.append(" ORDER BY " +PedidoProveedorDAOImpCons.BD_FCTPDNPRV_EXPEDIENTE);
	}
	
	
	@Override
	public PedidoProveedorVO getPedidoProv(MyServiceContext _context, String _pedidoTrack, String _pedidoExp) 
			throws PedidoFilesException {
		logger.info("getPedido: TR:{} :: EXP:{}", _pedidoTrack, _pedidoExp);
		try {
			String trimTR = _pedidoTrack.trim();
			String trimEXP = _pedidoExp.trim();
			List<PedidoProveedorVO> listPedidoProv = this.jdbcTemplate.query(sqlSelectPedidoByTrAndExp.toString(), new PedidoProveedorDAOImpMapper(), trimTR, trimEXP);
			if(listPedidoProv.isEmpty()){
				return null;
			} else {
				PedidoProveedorVO pProv = listPedidoProv.get(0);
				PedidoFilesVO pFiles = getPedidoFiles(pProv.getTrack(), pProv.getExpediente());
				TratarDatosUtils.tratarMotRechazo(pProv, pFiles);
				return pProv;
			}
		} catch (DataAccessException e) {
			logger.error(e.getMessage());
			throw new PedidoFilesException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}

	
	@Override
	public PedidoFilesVO getPedidoFiles(String _track, String _exp) throws PedidoFilesException {
		//logger.info("getPedidoFiles: TR:{} :: EXP:{}", _track, _exp);
		try {
			String trimTR = _track.trim();
			String trimEXP = _exp.trim();
			List<PedidoFilesVO> listPFiles = this.jdbcTemplate.query(sqlSelectPFilesByTrAndExp.toString(), 
					new PedidoFilesDAOImpMapper(), trimTR, trimEXP);
			if(listPFiles.isEmpty()){
				return null;
			} else {
				return listPFiles.get(0);
			}
		} catch (DataAccessException e) {
			logger.error(e.getMessage());
			throw new PedidoFilesException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}
	
	@Override
	public PedidoFilesVO setPedidoInicial(MyServiceContext _context, String _track, String _exp, LocalDateTime _fecEntRealDate) {
		//Generamos un pedidoInicial solo con track, exp, user y fecEntRealDate
		PedidoFilesVO pedidoAux = new PedidoFilesVO();
		pedidoAux.setRefPed(_track);
		pedidoAux.setExpediente(_exp);
		
		//Datos CMR vacio
		pedidoAux.setRutaCMR("");
		pedidoAux.setUsuSubidaCMR(_context.getUsername());
		if(_track.startsWith("GRU")){
			pedidoAux.setEstadoCMR(FileTransferConstantes.ESTADO_8_INIT_GRUP);
		} else {
			pedidoAux.setEstadoCMR(FileTransferConstantes.ESTADO_0_FICH_NO_SUBIDO);
		}
		pedidoAux.setFechaSubidaCMR(LocalDateTime.parse("0000-01-01T00:00:00"));
		pedidoAux.setAnotaciones(FileTransferConstantes.SIN_ANOTACIONES);
		pedidoAux.setMotivoRechazo("");
		pedidoAux.setFechaEstadoCMR(LocalDateTime.parse("0000-01-01T00:00:00"));
		pedidoAux.setUsuOkCMR("");
		//Datos FAC vacio
		pedidoAux.setRutaFAC("");
		pedidoAux.setUsuSubidaFAC("");
		if(_track.startsWith("GRU")){
			pedidoAux.setEstadoFAC(FileTransferConstantes.ESTADO_8_INIT_GRUP);
		} else {
			pedidoAux.setEstadoFAC(FileTransferConstantes.ESTADO_0_FICH_NO_SUBIDO);
		}
		pedidoAux.setFechaSubidaFAC(LocalDateTime.parse("0000-01-01T00:00:00"));
		pedidoAux.setFechaEstadoFAC(LocalDateTime.parse("0000-01-01T00:00:00"));
		pedidoAux.setUsuOkFAC("");
		//Datos OTR vacio
		pedidoAux.setRutaOTR("");
		pedidoAux.setUsuSubidaOTR("");
		pedidoAux.setFechaSubidaOTR(LocalDateTime.parse("0000-01-01T00:00:00"));
		
		pedidoAux.setEstadoFacturacion(FileTransferConstantes.ESTADO_0_FICH_NO_SUBIDO);
		pedidoAux.setFechaEntReal(_fecEntRealDate);
		
		return pedidoAux;
	}
	
	@Override
	public PedidoFilesVO updateCMR(PedidoFilesVO _pedidoF) throws PedidoFilesException {
		try {
			//CHAPUZAS HEREDADAS VARIAS: W2USUARIO precision 40; F1USECMR, F1USEFAC, F1USEOTR, todos precision 10
			final String usuFTarchiCMR;
			if(_pedidoF.getUsuSubidaCMR().length()>10){
				usuFTarchiCMR = _pedidoF.getUsuSubidaCMR().substring(0, 10);
			} else {
				usuFTarchiCMR = _pedidoF.getUsuSubidaCMR();
			}
			this.jdbcTemplate.update(sqlUpdateCMR.toString(),
					new PreparedStatementSetter(){
				public void setValues(PreparedStatement ps)
						throws SQLException {
					int i=1;
					ps.setString(i++, _pedidoF.getRutaCMR());
					ps.setString(i++, usuFTarchiCMR);
					ps.setLong(i++, _pedidoF.getEstadoCMR());
					SqlUtils.setLocalDateTimeToPs(ps, i++, _pedidoF.getFechaSubidaCMR());
					SqlUtils.setLocalDateTimeToPs(ps, i++, _pedidoF.getFechaEstadoCMR());
					ps.setString(i++, _pedidoF.getUsuOkCMR());
					ps.setString(i++, _pedidoF.getMotivoRechazo());
					ps.setLong(i++, _pedidoF.getAnotaciones());

					ps.setString(i++, _pedidoF.getRefPed());
					ps.setString(i++, _pedidoF.getExpediente());
				}
			});
			return _pedidoF;
		} catch (DataAccessException e) {
			logger.error("updateCMR: ", e.getMessage());
			throw new PedidoFilesException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}

	@Override
	public PedidoFilesVO updateFAC(PedidoFilesVO _pedidoF) throws PedidoFilesException {
		try {
			//CHAPUZAS HEREDADAS VARIAS: W2USUARIO precision 40; F1USECMR, F1USEFAC, F1USEOTR, todos precision 10
			final String usuFTarchiFAC;
			if(_pedidoF.getUsuSubidaFAC().length()>10){
				usuFTarchiFAC = _pedidoF.getUsuSubidaFAC().substring(0, 10);
			} else {
				usuFTarchiFAC = _pedidoF.getUsuSubidaFAC();
			}
			this.jdbcTemplate.update(sqlUpdateFAC.toString(),
					new PreparedStatementSetter(){
				public void setValues(PreparedStatement ps)
						throws SQLException {
					int i=1;
					ps.setString(i++, _pedidoF.getRutaFAC());
					ps.setString(i++, usuFTarchiFAC);
					ps.setLong(i++, _pedidoF.getEstadoFAC());
					SqlUtils.setLocalDateTimeToPs(ps, i++, _pedidoF.getFechaSubidaFAC());
					SqlUtils.setLocalDateTimeToPs(ps, i++, _pedidoF.getFechaEstadoFAC());
					ps.setString(i++, _pedidoF.getUsuOkFAC());
					ps.setString(i++, _pedidoF.getMotivoRechazo());
					ps.setLong(i++, _pedidoF.getAnotaciones());

					ps.setString(i++, _pedidoF.getRefPed());
					ps.setString(i++, _pedidoF.getExpediente());
				}
			});
			return _pedidoF;
		} catch (DataAccessException e) {
			logger.error("updateFAC: ", e.getMessage());
			throw new PedidoFilesException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}

	@Override
	public PedidoFilesVO updateOTR(PedidoFilesVO _pedidoF) throws PedidoFilesException {
		try {
			//CHAPUZAS HEREDADAS VARIAS: W2USUARIO precision 40; F1USECMR, F1USEFAC, F1USEOTR, todos precision 10
			final String usuFTarchiOTR;
			if(_pedidoF.getUsuSubidaOTR().length()>10){
				usuFTarchiOTR = _pedidoF.getUsuSubidaOTR().substring(0, 10);
			} else {
				usuFTarchiOTR = _pedidoF.getUsuSubidaOTR();
			}
			this.jdbcTemplate.update(sqlUpdateOTR.toString(),
					new PreparedStatementSetter(){
				public void setValues(PreparedStatement ps)
						throws SQLException {
					int i=1;
					ps.setString(i++, _pedidoF.getRutaOTR());
					ps.setString(i++, usuFTarchiOTR);
					SqlUtils.setLocalDateTimeToPs(ps, i++, _pedidoF.getFechaSubidaOTR());

					ps.setString(i++, _pedidoF.getRefPed());
					ps.setString(i++, _pedidoF.getExpediente());
				}
			});
			return _pedidoF;
		} catch (DataAccessException e) {
			logger.error("updateOTR: ", e.getMessage());
			throw new PedidoFilesException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}

	@Override
	public Integer updateESTADO(String _track, String _exp, Integer _estado) throws PedidoFilesException {
		logger.info("updateESTADO :: REFPED: {} : EXPEDI: {} : ESTADO: {}", _track, _exp, _estado);
		try {
			int x = this.jdbcTemplate.update(sqlUpdateEstado.toString(),
					new PreparedStatementSetter(){
				public void setValues(PreparedStatement ps)
						throws SQLException {
					int i=1;
					ps.setLong(i++, _estado);
					ps.setString(i++, _track);
					ps.setString(i++, _exp);
				}
			});
			return x;
		} catch (DataAccessException e) {
			logger.error("updateESTADO: ", e.getMessage());
			throw new PedidoFilesException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}

	@Override
	public Integer updateCMRanotaciones(String _track, String _exp, Integer _flag, String _anotacion) 
			throws PedidoFilesException {
		logger.info("updateCMRanotaciones :: REFPED: {} :: EXPEDI: {} :: ANOCMR: {} :: ANOTA: {} ", _track, _exp, _flag, _anotacion);
		try {
			int x = this.jdbcTemplate.update(sqlUpdateAnot.toString(),
					new PreparedStatementSetter(){
				public void setValues(PreparedStatement ps)
				throws SQLException {
					int i=1;
					ps.setLong(i++, _flag);
					ps.setString(i++, _anotacion);
					
					ps.setString(i++, _track);
					ps.setString(i++, _exp);
				}
			});
			return x;
		} catch (DataAccessException e) {
			logger.error("updateCMRanotaciones: ", e.getMessage());
			throw new PedidoFilesException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}

	@Override
	public Integer updateEstados(String _track, String _exp, Long _estadoCMR, Long _estadoFAC, Long _estadoGEN) 
			throws PedidoFilesException {
		logger.info("updateEstados :: REFPED: {} : EXP: {} : E_CMR: {} : E_FAC: {} : ESTADO: {}", _track, _exp, _estadoCMR, _estadoFAC, _estadoGEN);
		try {
			int x = this.jdbcTemplate.update(sqlUpdateGrupEstados.toString(),
					new PreparedStatementSetter(){
				public void setValues(PreparedStatement ps)
				throws SQLException {
					int i=1;
					ps.setLong(i++, _estadoCMR);
					ps.setLong(i++, _estadoFAC);
					ps.setLong(i++, _estadoGEN);
					
					ps.setString(i++, _track);
					ps.setString(i++, _exp);
				}
			});
			return x;
		} catch (DataAccessException e) {
			logger.error("updateEstados: ", e.getMessage());
			throw new PedidoFilesException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}

	@Override
	public PedidoGrupajeVO getGrupList(MyServiceContext _context, String _track) throws PedidoFilesException {
		try {
			//Al trackBase le quitamos los últimos 3
			String grupTR = _track.substring(0, _track.length() - 3);
			String likeGruTR = grupTR.concat("%");
			
			//Buscamos todo el GRUP en FCTPDNPRV
			List<PedidoProveedorVO> listaInicial = this.jdbcTemplate.query(sqlSelectInVistaByGrup.toString(), new PedidoProveedorDAOImpMapper(), likeGruTR);;
			
			List<PedidoProveedorVO> listaLimpia = TratarDatosUtils.tratarDuplicados(listaInicial);
			
			String[] userRol = _context.getUserRoles();
			logger.info("{} :: {} :: getGrupList :: grupTR :: {}", userRol[0].toString(), _context.getUsername(), grupTR);
			
			PedidoGrupajeVO pGr = new PedidoGrupajeVO();
			pGr.setGrupTR(grupTR);
			pGr.setSeleccionados(new ArrayList<GrupajeItem>());
			
			if(listaLimpia != null) {
				for(PedidoProveedorVO pedidoAct : listaLimpia) {
					String trackBase = pedidoAct.getTrack();
					String expActual = pedidoAct.getExpediente();
					
					//Verificación e inserción en FTARCHI
					PedidoFilesVO pedidoReg = this.getPedidoFiles(trackBase, expActual);
					if(pedidoReg == null) {
						LocalDateTime fechaInic = LocalDateTime.parse("0000-01-01T00:00:00");
						PedidoFilesVO nuevo = this.setPedidoInicial(_context, trackBase, expActual, fechaInic);
						pedidosActivosDAO.insertFecREAL(nuevo);
						//Actualizamos el objeto en memoria para que la lógica de checks de abajo use los datos nuevos
	                    pedidoAct.setHasCMR(new Long(FileTransferConstantes.ESTADO_8_INIT_GRUP));
	                    pedidoAct.setHasFAC(new Long(FileTransferConstantes.ESTADO_8_INIT_GRUP));
					} else {
						//Si ya existe, cargamos sus estados reales
						pedidoAct.setHasCMR(pedidoReg.getEstadoCMR().longValue());
						pedidoAct.setHasFAC(pedidoReg.getEstadoFAC().longValue());
					}

					GrupajeItem gItem = new GrupajeItem();
					String grupNum = trackBase.substring(trackBase.length() - 3);
					
					gItem.setGrupNum(grupNum);
					gItem.setExp(expActual);
					gItem.setRefCarga(pedidoAct.getRefCarga());
					
					//Lógica de checks
					if(pedidoAct.getHasCMR() == 8) {
						gItem.setOpcionCMR(true);
						gItem.setOpcionFAC(true);
					} else {
						gItem.setOpcionCMR(pedidoAct.getHasCMR() == 0);
						gItem.setOpcionFAC(pedidoAct.getHasFAC() == 0);
					}
					pGr.getSeleccionados().add(gItem);
				}
			}
			return pGr;
		} catch (DataAccessException e) {
			logger.error("getGrupList: ", e.getMessage());
			throw new PedidoFilesException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}
	
	@Override
	public int countHermanosEnActivos(String currentGrupTR){
		//Busca si queda algun hermano que debe ser validado, pero todavia no tiene documentacion adjunta
		String sql = "SELECT COUNT(*) FROM FTARCHI WHERE F1REFPED LIKE ? AND F1ESTCMR IN (0, 3)";
	    String likeGruTR = currentGrupTR + "%";
	    
	    try {
	        return this.jdbcTemplate.queryForObject(sql, Integer.class, likeGruTR);
	    } catch (DataAccessException e) {
	        logger.error("Error en countHermanosEnActivosParaValidar: {}", e.getMessage());
	        return 0;
	    }
		
	}
	
	@Override
	public int countHermanosPorValidarCMR(String currentGrupTR) {
		String sql = "SELECT COUNT(*) FROM FTARCHI WHERE F1REFPED LIKE ? AND F1ESTCMR NOT IN (2, 7)";
		String likeGruTR = currentGrupTR + "%";
		
		try {
			return this.jdbcTemplate.queryForObject(sql, Integer.class, likeGruTR);
		} catch (DataAccessException e) {
			logger.error("Error en countHermanosPendientes: {}", e.getMessage());
			return 0;
		}
	}
	
	@Override
	public int countHermanosNoValidados(String currentGrupTR) {
		String sql = "SELECT COUNT(*) FROM FTARCHI WHERE F1REFPED LIKE ? AND F1ESTADO != 2";
		String likeGruTR = currentGrupTR + "%";
		
		try {
			return this.jdbcTemplate.queryForObject(sql, Integer.class, likeGruTR);
		} catch (DataAccessException e) {
			logger.error("Error en countHermanosSinEstadoDos: {}", e.getMessage());
			return 0;
		}
	}
	
}
