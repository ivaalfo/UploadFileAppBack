//package eus.martico.fileTransfer.dao.firmador;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Base64;
//import java.util.Iterator;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataAccessException;
//import org.springframework.jdbc.core.PreparedStatementSetter;
//import org.springframework.stereotype.Service;
//
//import eus.martico.fileTransfer.config.FileTransferConfiguration;
//import eus.martico.fileTransfer.config.FileTransferConstantes;
//import eus.martico.fileTransfer.dao.BaseGenDAO;
//import eus.martico.fileTransfer.dao.DAOException;
//import eus.martico.fileTransfer.dao.usuario.UsuarioDAOException;
//import eus.martico.fileTransfer.dao.usuario.UsuarioIDAO;
//import eus.martico.fileTransfer.model.firma.PedidoFirmadorVO;
//import eus.martico.fileTransfer.model.firma.PipelineFirmaDTO;
//import eus.martico.fileTransfer.model.mensaje.vo.MensajeVO;
//import eus.martico.fileTransfer.model.vo.ValidateDTO;
//import eus.martico.fileTransfer.services.MyServiceContext;
//import eus.martico.fileTransfer.services.firma.FileSystemCertSignService;
//import eus.martico.fileTransfer.sql.SqlUtils;
//import eus.martico.fileTransfer.utils.FileTransferUtils;
//import eus.martico.fileTransfer.utils.TratarDatosUtils;
//
///**
// * NEW descartado
// * @author Martico S.L. :: ialfonso
// * @version 1.0
// * @see https://www.martico.es/
// *
// */
//@Service(value = "FirmadorIDAO")
//public class FirmadorDAOImp extends BaseGenDAO<PedidoFirmadorVO> implements FirmadorIDAO {
//
//	protected static final Logger logger = LoggerFactory.getLogger(FirmadorDAOImp.class);
//	
//	
//	@Autowired
//	UsuarioIDAO usuarioDAO;
//	
//	
//	private static StringBuilder sqlSelectPFirmaByTrAndExp;
//	
//	private static StringBuilder sqlUpdateEstadoCMRfirma;
//	
//		
//	//Pedidos por firmar (firma1, firma2, firma3)
//	private static StringBuilder condicionEstados = 
//			new StringBuilder(
//					FirmadorDAOImpCons.BD_FTCMR_ESTCMR
//					+ " = " + FileTransferConstantes.ESTADO_FIRMA_MARTICO
//					+ " OR "
//					+ FirmadorDAOImpCons.BD_FTCMR_ESTCMR
//					+ " = " + FileTransferConstantes.ESTADO_FIRMA_TRANSPORTISTA
//					+ " OR "
//					+ FirmadorDAOImpCons.BD_FTCMR_ESTCMR
//					+ " = " + FileTransferConstantes.ESTADO_FIRMA_DESTINATARIO
//			);
//
//	private static StringBuilder condicionOrder = 
//			new StringBuilder(
//					" ORDER BY F3FECCM1 DESC "
//							+ " FETCH FIRST 1000 ROWS ONLY "
//					);
//	
//
//	static {
//		sqlSelectPFirmaByTrAndExp = new StringBuilder("SELECT distinct * FROM ");
//		sqlSelectPFirmaByTrAndExp.append(FirmadorDAOImpCons.BD_TABLA_FTCMR);
//		sqlSelectPFirmaByTrAndExp.append(" WHERE ");
//		sqlSelectPFirmaByTrAndExp.append(" TRIM("+FirmadorDAOImpCons.BD_FTCMR_REFPED+")");
//		sqlSelectPFirmaByTrAndExp.append(" = ? ");
//		sqlSelectPFirmaByTrAndExp.append(" AND ");
//		sqlSelectPFirmaByTrAndExp.append(" TRIM("+FirmadorDAOImpCons.BD_FTCMR_EXPEDI+")");
//		sqlSelectPFirmaByTrAndExp.append(" = ? ");
//
//
//		List<String> camposUpdateESTADO = new ArrayList<String>(Arrays.asList(
//				FirmadorDAOImpCons.BD_FTCMR_ESTCMR
//				));
//
//		sqlUpdateEstadoCMRfirma = SqlUtils.generarSqlUpdate(
//				FirmadorDAOImpCons.BD_TABLA_FTCMR, 
//				camposUpdateESTADO, 
//				FirmadorDAOImpCons.BD_FTCMR_REFPED, 
//				FirmadorDAOImpCons.BD_FTCMR_EXPEDI);
//	}
//
//	
//	@Override
//	public PedidoFirmadorVO getPedidoFirma(String _track, String _exp) throws FirmadorException {
//		//logger.info("getPedidoFirma: TR:{} :: EXP:{}", _track, _exp);
//		try {
//			String trimTR = _track.trim();
//			String trimEXP = _exp.trim();
//			List<PedidoFirmadorVO> listPFirma = this.jdbcTemplate.query(sqlSelectPFirmaByTrAndExp.toString(), 
//					new FirmadorDAOImpMapper(), trimTR, trimEXP);
//			if(listPFirma.isEmpty()){
//				return null;
//			} else {
//				return listPFirma.get(0);
//			}
//		} catch (DataAccessException e) {
//			logger.error(e.getMessage());
//			throw new FirmadorException(DAOException.SQLERRCODE_QUERY, e.getMessage());
//		}
//	}
//	
//	@Override
//	public Integer updateEstadoFirma(String _track, String _exp, Long _estadoCMRfirma)
//			throws FirmadorException {
//		logger.info("updateEstadoFirma :: REFPED: {} : EXP: {} : F3ESTCMR: {} : ", _track, _exp, _estadoCMRfirma);
//		try {
//			int x = this.jdbcTemplate.update(sqlUpdateEstadoCMRfirma.toString(),
//					new PreparedStatementSetter(){
//				public void setValues(PreparedStatement ps)
//				throws SQLException {
//					int i=1;
//					ps.setLong(i++, _estadoCMRfirma);
//					
//					ps.setString(i++, _track);
//					ps.setString(i++, _exp);
//				}
//			});
//			return x;
//		} catch (DataAccessException e) {
//			logger.error("updateEstadoFirma: ", e.getMessage());
//			throw new FirmadorException(DAOException.SQLERRCODE_QUERY, e.getMessage());
//		}
//	}
//	
//	@Override
//	public List<PedidoFirmadorVO> getPedidosFiltrados(MyServiceContext _context, String _expCod, String _provName, String _track, String _refCharge) 
//			throws FirmadorException, UsuarioDAOException {
//		//UPPER ACUMULATIVE DYNAMIC FILTER
//		try {
//			List<PedidoFirmadorVO> lista = new ArrayList<>();
//			StringBuilder sqlSelectMttoFirma = new StringBuilder();
//			List<Object> params = new ArrayList<>();
//			StringBuilder infoLog = new StringBuilder();
//			
//			boolean isExterno = Arrays.asList(_context.getUserRoles()).stream()
//					.noneMatch(role -> role.equals(FileTransferConstantes.PROPERTY_KEY_ROL_ADMIN) 
//									|| role.equals(FileTransferConstantes.PROPERTY_KEY_ROL_INTERNO)
//									|| role.equals(FileTransferConstantes.PROPERTY_KEY_ROL_CONSULTA));
//			
//			String[] roles = _context.getUserRoles();
//			infoLog.append(roles[0]+" :: "+_context.getUsername()+" :: getPedidosFiltrados");
//			//logger.info(infoLog.toString());
//			
//			//1.BASE COMUN
//			if(isExterno){
//				String sql4Externos = usuarioDAO.getSqlPedidosFirma(isExterno);
//				sqlSelectMttoFirma.append(sql4Externos);
//				params.add(_context.getUsuarioNi());
//			} else {
//				String sql4NoExternos = usuarioDAO.getSqlPedidosFirma(isExterno);
//				sqlSelectMttoFirma.append(sql4NoExternos);
//			}
//			
//			//2.FILTROS DINAMICOS ACUMULATIVOS UPPER
//			//Filtro Expediente - Disponible para todo tipo de user
//			if(_expCod != null && !_expCod.isEmpty()){
//				sqlSelectMttoFirma.append(" AND UPPER(TRIM("+FirmadorDAOImpCons.BD_FTCMR_EXPEDI+")) LIKE ? ");
//				String expCodUpper = _expCod.toUpperCase() + "%";
//				params.add(expCodUpper);
//				infoLog.append(" :: byExp:"+_expCod);
//			}
//			
//			//Filtro Track - Disponible para todo tipo de user
//			if(_track != null && !_track.isEmpty()){
//				sqlSelectMttoFirma.append(" AND UPPER(TRIM("+FirmadorDAOImpCons.BD_FTCMR_REFPED+")) LIKE ? ");
//				String trackUpper = _track.trim().toUpperCase() + "%";
//				params.add(trackUpper);
//				infoLog.append(" :: byTrack:"+_track);
//			}
//			
//			//Filtro Proveedor - Para todos excepto Externos
//			if(!isExterno && _provName != null && !_provName.isEmpty()){
//				String provUpper = _provName.toUpperCase() + "%";
//				//Obtenemos TODOS los códigos que coinciden con el nombre
//				List<String> clicods = usuarioDAO.getCLICODbyProv(provUpper);
//				if(clicods.isEmpty()){
//					sqlSelectMttoFirma.append(" AND F3CLICOD IN ?");
//					params.add(provUpper);
//				} else {
//					//Construimos el "IN (?, ?, ?)" dinámicamente
//					String placeholders = clicods.stream().map(c -> "?").collect(Collectors.joining(","));
//					sqlSelectMttoFirma.append(" AND F3CLICOD IN (" +placeholders+ ")");
//					params.addAll(clicods);
//				}
//				infoLog.append(" :: byProvName: "+_provName);
//			}
//			
//			//Filtro Ref.Carga - Disponible para todo tipo de user
//			if(_refCharge != null && !_refCharge.isEmpty()){
//				sqlSelectMttoFirma.append(" AND UPPER(TRIM("+FirmadorDAOImpCons.BD_FTCMR_RCARGA+")) LIKE ? ");
//				String rCargaUpper = _refCharge.trim().toUpperCase() + "%";
//				params.add(rCargaUpper);
//				infoLog.append(" :: byRefCarga:"+_refCharge);
//			}
//			
//			//3.FINAL COMUN
//			sqlSelectMttoFirma.append(" AND (").append(condicionEstados).append(") ");
//			sqlSelectMttoFirma.append(condicionOrder);
//			logger.info(infoLog.toString());
//			
//			lista = this.jdbcTemplate.query(sqlSelectMttoFirma.toString(), params.toArray(), new FirmadorDAOImpMapper());
//			lista = tratarListaPFirm(lista);
//			return lista;
//		} catch (DataAccessException e) {
//			logger.error("getPedidosFiltrados: {}" + e.getMessage());
//			throw new FirmadorException(DAOException.SQLERRCODE_QUERY, e.getMessage());	
//		}
//	}
//	
//	@Override
//	public ValidateDTO<PedidoFirmadorVO> añadirFirmaIncremental(MyServiceContext _context, PipelineFirmaDTO _firmaDTO)
//			throws FirmadorException {
//		ValidateDTO<PedidoFirmadorVO> validate = new ValidateDTO<PedidoFirmadorVO>();
//		String track = _firmaDTO.getTrack();
//		String exp = _firmaDTO.getExp();
//		PedidoFirmadorVO pFirma = this.getPedidoFirma(track, exp);
//		
//		if (pFirma == null) {
//	        throw new FirmadorException("No se ha encontrado el registro de firma para el pedido indicado.");
//	    }
//		int estadoActual = pFirma.getF3estado();
//	    int numeroFirma = 0;
//	    int siguienteEstado = 0;
//	    String rolFirmante = "";
//	    
//	    if (estadoActual == 1) {
//	    	//Firmado por Martico/Almacén -> Toca firma del Transportista (Firma 2)
//	    	numeroFirma = 2;
//	        siguienteEstado = 2;
//	        rolFirmante = "Transportista Efectivo";
//	    } else if (estadoActual == 2) {
//	        //Firmado por Martico + Transportista -> Toca firma del Destinatario (Firma 3)
//	    	numeroFirma = 3;
//	        siguienteEstado = 3;
//	        rolFirmante = "Destinatario / Receptor";
//	    } else {
//	        throw new FirmadorException("El documento ya se encuentra en un estado que no admite más firmas acumulativas (Estado actual: " + estadoActual + ").");
//	    }
//		
//	    try {
//	    	//LEER EL ARCHIVO PDF ACTUAL
//	    	Path filePath = Paths.get(pFirma.getF3rutCMR());
//	    	byte[] pdfConFirmasPrevias = FileTransferUtils.getByteFile(filePath);
//	    	
//	    	//CONVERTIR EL BASE64 DEL CANVAS A BYTES REALES (PNG)
//	        byte[] pngFirmaBytes = null;
//	        if (_firmaDTO.getImagenFirmaBase64() != null && !_firmaDTO.getImagenFirmaBase64().isEmpty()) {
//	            pngFirmaBytes = Base64.getDecoder().decode(_firmaDTO.getImagenFirmaBase64());
//	        } else {
//	            throw new FirmadorException("Es obligatorio adjuntar el trazo o sello visual de la firma.");
//	        }
//
//	        //RECUPERAR RUTA Y CREDENCIALES DEL CERTIFICADO
//	        String p12certPath = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_PATH);
//			String p12certPass = FileTransferConfiguration.getProperty(FileTransferConstantes.CERT_PASS);
//	        
//	        //LLAMAMOS a la función de FIRMA INCREMENTAL que añade los bytes al final sin romper nada
//	        byte[] pdfResultadoIncremental = FileSystemCertSignService.firmadoDSSIncrementalAcumulativo(
//	                pdfConFirmasPrevias, 
//	                pngFirmaBytes, 
//	                p12certPath, 
//	                p12certPass, 
//	                numeroFirma, 
//	                rolFirmante
//	        );
//	        
//	        //GUARDAMOS el nuevo pdf en el Servidor
//	        FileTransferUtils.storeByteFile(filePath, pdfResultadoIncremental);
//
//	        //UPDATE F3ESTADO en BBDD
//	        Long _estadoFirma = new Long(siguienteEstado);
//	        this.updateEstadoFirma(track, exp, _estadoFirma);
//	        
//	        logger.info("añadirFirmaIncremental :: Firma acumulativa num." + numeroFirma + " integrada con éxito para el TR: " + track);
//	        validate.addMensaje(new MensajeVO("OK", "CMR Firmado correctamente (Firma " + numeroFirma + "). TR: " + track));
//	        
//		} catch (Exception e) {
//	        logger.error("ERROR al procesar la firma acumulativa incremental: " + e.getMessage());
//	        throw new FirmadorException("Error al procesar la firma acumulativa incremental: " + e.getMessage());
//	    }
//		return validate;
//	}
//	
//	
//	//Tratamiento de algunos datos para mostralos en pantalla
//	private List<PedidoFirmadorVO> tratarListaPFirm(List<PedidoFirmadorVO> listaOriginal) 
//			throws FirmadorException, UsuarioDAOException {
//		if(listaOriginal == null || listaOriginal.isEmpty()) return new ArrayList<>();
//
//		//TO-DO FIRMADOR TRATAMIENTO DATOS
//		
//		//1.Limpiamos duplicados
////		List<PedidoProveedorVO> listaFiltrada = TratarDatosUtils.tratarDuplicados(listaOriginal);
//
//		//Caché temporal para no repetir consultas al mismo proveedor en esta ejecución
////		Map<String, String> cachePActiv = new HashMap<>();
//		//Iterador para la lista
//		Iterator<PedidoFirmadorVO> it = listaOriginal.iterator();
//
////		//2.Procesamiento de datos y recuperación de estados desde FTARCHI
//		while(it.hasNext()) {
//			PedidoFirmadorVO pedido = it.next();
////			//Si hay hermanos 'rezagados' con 0,3 lo sacamos del validador
////			if (pedido.isGrupaje()){
////				int rezagados = pedidoFilesDAO.countHermanosEnActivos(pedido.getGrupTR());
////				if (rezagados > 0){
////					it.remove();
////					continue; 		//Saltamos al siguiente registro sin evaluar el resto de condiciones
////				}
////			}
//			
//			TratarDatosUtils.obtenerProvNameDestName(pedido, usuarioDAO);
////			TratarDatosUtils.tratarProvUser(pedido, cachePActiv, this.usuarioDAO);
////			TratarDatosUtils.tratarGrupajesInicio(pedido);
////
////			PedidoFilesVO pFiles = pedidoFilesDAO.getPedidoFiles(pedido.getTrack(), pedido.getExpediente());
////			TratarDatosUtils.tratarMotRechazo(pedido, pFiles);
////			TratarDatosUtils.tratarFechasSubida(pedido, pFiles);
////			TratarDatosUtils.refreshPedido(pedido, pFiles);
//		}
////
////		//3.Tratar Grupajes con 7/7 o x/7
////		TratarDatosUtils.tratarGrupSietesPValidador(listaFiltrada, this.pedidoFilesDAO);
////
////		//4.Ordenar lista por la fecha de subdia del CMR :: F1FECCM1
////		TratarDatosUtils.ordenarPorFechaSubidaCompleta(listaFiltrada);
////
////		//5.Reordenación para grupajes: manteniendo el orden de aparición original
////		return TratarDatosUtils.reordenarGrup(listaFiltrada);
//		
//		//MOCK
//		return listaOriginal;
//	}
//
//}
