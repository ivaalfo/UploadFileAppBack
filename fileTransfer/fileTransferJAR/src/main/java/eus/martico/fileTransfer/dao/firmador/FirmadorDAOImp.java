package eus.martico.fileTransfer.dao.firmador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.dao.BaseGenDAO;
import eus.martico.fileTransfer.dao.DAOException;
import eus.martico.fileTransfer.dao.usuario.UsuarioDAOException;
import eus.martico.fileTransfer.dao.usuario.UsuarioIDAO;
import eus.martico.fileTransfer.model.error.vo.ErrorVO;
import eus.martico.fileTransfer.model.firma.PedidoFirmadorVO;
import eus.martico.fileTransfer.model.firma.PipelineFirmaDTO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.utils.TratarDatosUtils;

/**
 * NEW
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@Service(value = "FirmadorIDAO")
public class FirmadorDAOImp extends BaseGenDAO<PedidoFirmadorVO> implements FirmadorIDAO {

	protected static final Logger logger = LoggerFactory.getLogger(FirmadorDAOImp.class);
	
	
	@Autowired
	UsuarioIDAO usuarioDAO;
	
	
	private static StringBuilder sqlSelectPFirmaByTrAndExp;
	
		
	//Pedidos por firmar (firma1, firma2, firma3)
	private static StringBuilder condicionEstados = 
			new StringBuilder(
					FirmadorDAOImpCons.BD_FTCMR_ESTCMR
					+ " = " + FileTransferConstantes.ESTADO_FIRMADO_A
					+ " OR "
					+ FirmadorDAOImpCons.BD_FTCMR_ESTCMR
					+ " = " + FileTransferConstantes.ESTADO_FIRMADO_B
					+ " OR "
					+ FirmadorDAOImpCons.BD_FTCMR_ESTCMR
					+ " = " + FileTransferConstantes.ESTADO_FIRMADO_C
			);

	private static StringBuilder condicionOrder = 
			new StringBuilder(
					" ORDER BY F3FECCM1 DESC "
							+ " FETCH FIRST 1000 ROWS ONLY "
					);

	static {
		sqlSelectPFirmaByTrAndExp = new StringBuilder("SELECT distinct * FROM ");
		sqlSelectPFirmaByTrAndExp.append(FirmadorDAOImpCons.BD_TABLA_FTCMR);
		sqlSelectPFirmaByTrAndExp.append(" WHERE ");
		sqlSelectPFirmaByTrAndExp.append(" TRIM("+FirmadorDAOImpCons.BD_FTCMR_REFPED+")");
		sqlSelectPFirmaByTrAndExp.append(" = ? ");
		sqlSelectPFirmaByTrAndExp.append(" AND ");
		sqlSelectPFirmaByTrAndExp.append(" TRIM("+FirmadorDAOImpCons.BD_FTCMR_EXPEDI+")");
		sqlSelectPFirmaByTrAndExp.append(" = ? ");
	}
	
	
	@Override
	public PedidoFirmadorVO getPedidoFirma(String _track, String _exp) throws FirmadorException {
		//logger.info("getPedidoFirma: TR:{} :: EXP:{}", _track, _exp);
		try {
			String trimTR = _track.trim();
			String trimEXP = _exp.trim();
			List<PedidoFirmadorVO> listPFirma = this.jdbcTemplate.query(sqlSelectPFirmaByTrAndExp.toString(), 
					new FirmadorDAOImpMapper(), trimTR, trimEXP);
			if(listPFirma.isEmpty()){
				return null;
			} else {
				return listPFirma.get(0);
			}
		} catch (DataAccessException e) {
			logger.error(e.getMessage());
			throw new FirmadorException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}
	
	@Override
	public List<PedidoFirmadorVO> getPedidosFiltrados(MyServiceContext _context, String _expCod, String _provName, String _track, String _refCharge) 
			throws FirmadorException, UsuarioDAOException {
		//UPPER ACUMULATIVE DYNAMIC FILTER
		try {
			List<PedidoFirmadorVO> lista = new ArrayList<>();
			StringBuilder sqlSelectMttoFirma = new StringBuilder();
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
				String sql4Externos = usuarioDAO.getSqlPedidosFirma(isExterno);
				sqlSelectMttoFirma.append(sql4Externos);
				params.add(_context.getUsuarioNi());
			} else {
				String sql4NoExternos = usuarioDAO.getSqlPedidosFirma(isExterno);
				sqlSelectMttoFirma.append(sql4NoExternos);
			}
			
			//2.FILTROS DINAMICOS ACUMULATIVOS UPPER
			//Filtro Expediente - Disponible para todo tipo de user
			if(_expCod != null && !_expCod.isEmpty()){
				sqlSelectMttoFirma.append(" AND UPPER(TRIM("+FirmadorDAOImpCons.BD_FTCMR_EXPEDI+")) LIKE ? ");
				String expCodUpper = _expCod.toUpperCase() + "%";
				params.add(expCodUpper);
				infoLog.append(" :: byExp:"+_expCod);
			}
			
			//Filtro Track - Disponible para todo tipo de user
			if(_track != null && !_track.isEmpty()){
				sqlSelectMttoFirma.append(" AND UPPER(TRIM("+FirmadorDAOImpCons.BD_FTCMR_REFPED+")) LIKE ? ");
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
					sqlSelectMttoFirma.append(" AND F3CLICOD IN ?");
					params.add(provUpper);
				} else {
					//Construimos el "IN (?, ?, ?)" dinámicamente
					String placeholders = clicods.stream().map(c -> "?").collect(Collectors.joining(","));
					sqlSelectMttoFirma.append(" AND F3CLICOD IN (" +placeholders+ ")");
					params.addAll(clicods);
				}
				infoLog.append(" :: byProvName: "+_provName);
			}
			
			//Filtro Ref.Carga - Disponible para todo tipo de user
			if(_refCharge != null && !_refCharge.isEmpty()){
				sqlSelectMttoFirma.append(" AND UPPER(TRIM("+FirmadorDAOImpCons.BD_FTCMR_RCARGA+")) LIKE ? ");
				String rCargaUpper = _refCharge.trim().toUpperCase() + "%";
				params.add(rCargaUpper);
				infoLog.append(" :: byRefCarga:"+_refCharge);
			}
			
			//3.FINAL COMUN
			sqlSelectMttoFirma.append(" AND (").append(condicionEstados).append(") ");
			sqlSelectMttoFirma.append(condicionOrder);
			logger.info(infoLog.toString());
			
			lista = this.jdbcTemplate.query(sqlSelectMttoFirma.toString(), params.toArray(), new FirmadorDAOImpMapper());
			lista = tratarListaPFirm(lista);
			return lista;
		} catch (DataAccessException e) {
			logger.error("getPedidosFiltrados: {}", e.getMessage());
			throw new FirmadorException(DAOException.SQLERRCODE_QUERY, e.getMessage());	
		}
	}
	
	@Override
	public ValidateDTO<PedidoFirmadorVO> añadirFirmaIncremental(MyServiceContext _context, PipelineFirmaDTO _firmaDTO)
			throws FirmadorException {
		ValidateDTO<PedidoFirmadorVO> validate = new ValidateDTO<PedidoFirmadorVO>();
		String track = _firmaDTO.getTrack();
		//TODO FIRMADOR
		//Si estado es 1 = firmado por Martico
		//Lógica para firmar el archivo por el Proveedor (2a)
		//Leemos el ya firmado por nosotros
		//Aplicamos el garabato que viene aqui sobre el original Firmado
		//Update estado = 2
		//TODO FIRMADOR
		//Si estado es 2 = firmado por Martico + Proveedor
		//Lógica para firmar el archivo por el Destino (3a)
		//Leemos el ya firmado
		//Aplicamos el garabato que viene aqui sobre el Firmado(2o)
		//Update estado = 3
		
		
		
//	    String exp = firmaDTO.getExp();
//	    String rCarga = firmaDTO.getRCarga();
//	    String base64Firma = firmaDTO.getImagenFirmaBase64();
//	    Boolean isFirmaAcumulativa = firmaDTO.getIsFirmaAcumulativa();
//	    
//	    //CONVERSIÓN: Convertir el texto Base64 a bytes reales de imagen
//	    byte[] imagenFirmaBytes = null;
//	    if (base64Firma != null && !base64Firma.isEmpty()) {
//	        try {
//	            //Decodificamos el String Base64 puro a un array de bytes (PNG)
//	            imagenFirmaBytes = Base64.getDecoder().decode(base64Firma);
//	            logger.info("Imagen de firma descodificada correctamente. Tamaño: " + imagenFirmaBytes.length + " bytes.");
//	        } catch (IllegalArgumentException e) {
//	            throw new PedidoFilesException("El formato de la imagen de la firma no es un Base64 válido.");
//	        }
//	    }
//
//	    // 3. Continuar con tu lógica de negocio...
//	    // Aquí es donde comprobarás:
//	    // if (isFirmaAcumulativa) { ... llamar al nuevo flujo incremental con SD-DSS ... }
//	    // else { ... tu flujo de primera firma actual ... }
	    
		validate.addError(new ErrorVO("XXXX", "NEW SIGN_FILE_SERVICE TR: " +track));
		return validate;
	}
	
	
	//Tratamiento de algunos datos para mostralos en pantalla
	private List<PedidoFirmadorVO> tratarListaPFirm(List<PedidoFirmadorVO> listaOriginal) 
			throws FirmadorException, UsuarioDAOException {
		if(listaOriginal == null || listaOriginal.isEmpty()) return new ArrayList<>();

		//TODO FIRMADOR
		
		//1.Limpiamos duplicados
//		List<PedidoProveedorVO> listaFiltrada = TratarDatosUtils.tratarDuplicados(listaOriginal);

		//Caché temporal para no repetir consultas al mismo proveedor en esta ejecución
//		Map<String, String> cachePActiv = new HashMap<>();
		//Iterador para la lista
		Iterator<PedidoFirmadorVO> it = listaOriginal.iterator();

//		//2.Procesamiento de datos y recuperación de estados desde FTARCHI
		while(it.hasNext()) {
			PedidoFirmadorVO pedido = it.next();
//			//Si hay hermanos 'rezagados' con 0,3 lo sacamos del validador
//			if (pedido.isGrupaje()){
//				int rezagados = pedidoFilesDAO.countHermanosEnActivos(pedido.getGrupTR());
//				if (rezagados > 0){
//					it.remove();
//					continue; 		//Saltamos al siguiente registro sin evaluar el resto de condiciones
//				}
//			}
			
			TratarDatosUtils.obtenerProvNameDestName(pedido, usuarioDAO);
//			TratarDatosUtils.tratarProvUser(pedido, cachePActiv, this.usuarioDAO);
//			TratarDatosUtils.tratarGrupajesInicio(pedido);
//
//			PedidoFilesVO pFiles = pedidoFilesDAO.getPedidoFiles(pedido.getTrack(), pedido.getExpediente());
//			TratarDatosUtils.tratarMotRechazo(pedido, pFiles);
//			TratarDatosUtils.tratarFechasSubida(pedido, pFiles);
//			TratarDatosUtils.refreshPedido(pedido, pFiles);
		}
//
//		//3.Tratar Grupajes con 7/7 o x/7
//		TratarDatosUtils.tratarGrupSietesPValidador(listaFiltrada, this.pedidoFilesDAO);
//
//		//4.Ordenar lista por la fecha de subdia del CMR :: F1FECCM1
//		TratarDatosUtils.ordenarPorFechaSubidaCompleta(listaFiltrada);
//
//		//5.Reordenación para grupajes: manteniendo el orden de aparición original
//		return TratarDatosUtils.reordenarGrup(listaFiltrada);
		
		//MOCK
		return listaOriginal;
	}

}
