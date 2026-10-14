package eus.martico.fileTransfer.services.pedidos.activos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.dao.DAOException;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesException;
import eus.martico.fileTransfer.dao.pedido.files.PedidoFilesIDAO;
import eus.martico.fileTransfer.dao.pedidos.activos.PedidosActivosException;
import eus.martico.fileTransfer.dao.pedidos.activos.PedidosActivosIDAO;
import eus.martico.fileTransfer.model.pedido.files.PedidoFilesVO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoGrupajeVO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoGrupajeVO.GrupajeItem;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoProveedorVO;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.utils.FileTransferUtils;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
@Service(value = "PedidosActivosIService")
public class PedidosActivosServiceImp implements PedidosActivosIService {
		
	@Autowired
	PedidosActivosIDAO pedidosActivosDAO;

	@Autowired
	PedidoFilesIDAO pedidoFilesDAO;
	
	
	
	/**
	 * getGrupaje
	 * 
	 * @param _context
	 * @param _track
	 * @return
	 * @throws PedidoFilesException
	 */
	@Override
	public PedidoGrupajeVO getGrupaje(MyServiceContext _context, String _track) throws PedidoFilesException {
		try {
			return pedidoFilesDAO.getGrupList(_context, _track);
		} catch (PedidoFilesException e) {
			throw new PedidoFilesException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}

	/**
	 * setGrupaje
	 * 
	 * @param _context
	 * @param _grupTR
	 * @param _seleccionados
	 * @return
	 * @throws PedidoFilesException
	 */
	@Override
	public Integer setGrupaje(MyServiceContext _context, String _grupTR, List<GrupajeItem> _seleccionados) throws PedidoFilesException {
		
		int z = 0;
		
		for(GrupajeItem pedidoGrup : _seleccionados){
			String refPed = _grupTR.concat(pedidoGrup.grupNum);
			//Como tienen que tener fecReal estarán registrados en FTARCHI
			PedidoFilesVO pedidoF = pedidoFilesDAO.getPedidoFiles(refPed, pedidoGrup.expediente);
			
			if(pedidoF != null){
				//Interesa guardar los pedidos que no tendrán algun doc (estado = 7 = sinDoc)
				if(pedidoGrup.opcionCMR == false){
					Integer e_cmr = FileTransferConstantes.ESTADO_7_FICH_SIN_DOC;
					pedidoF.setEstadoCMR(e_cmr);
				} else {
					pedidoF.setEstadoCMR(new Integer(0));
				}
				if(pedidoGrup.opcionFAC == false){
					Integer e_fac = FileTransferConstantes.ESTADO_7_FICH_SIN_DOC;
					pedidoF.setEstadoFAC(e_fac);
				} else {
					pedidoF.setEstadoFAC(new Integer(0));
				}
				
				Integer estado = FileTransferUtils.determinarEstadoGeneral(pedidoF, refPed, pedidoF.getExpediente());
				pedidoF.setEstadoFacturacion(estado);
				
				Long estCMR = new Long(pedidoF.getEstadoCMR());
            	Long estFAC = new Long(pedidoF.getEstadoFAC());
            	Long estGEN = new Long(estado);
            	String exp = pedidoF.getExpediente();
				//Hago un update por cada uno con el E_CMR, el E_FAC y el ESTADO
				z = pedidoFilesDAO.updateEstados(pedidoF.getRefPed(), exp, estCMR, estFAC, estGEN);
				
			}
		}
//		lo devuelvo al front, pinto icono de sinDoc/bloq
//		si es la fac la que tiene el 7, no llegara al AS400, se valida el cmr
//		si es el cmr el que tiene el 7, no habra que validarlo, el AS400 tendra que tratarlo como 2 para validar la factura
		return z;
	}
	
	/**
	 * setFecReal
	 * 
	 * @param _context
	 * @param _pedidoTrack
	 * @param _pedidoExp
	 * @param _fechaEntReal
	 * @return
	 * @throws PedidoFilesException
	 */
	@Override
	@Transactional
	public Integer setFecReal(final MyServiceContext _context, String _pedidoTrack, String _pedidoExp, String _fechaEntReal) 
			throws PedidoFilesException {
		int z = 0;
		
		String anio = _fechaEntReal.substring(0,4);
		String mes = _fechaEntReal.substring(4,6);
		String dia = _fechaEntReal.substring(6,8);
		String fecString = anio.concat("-").concat(mes).concat("-").concat(dia);
		
		DateTimeFormatter dateFormaterA = DateTimeFormatter.ofPattern(FileTransferConstantes.PROCEDURE_DATE_FORMAT);
		DateTimeFormatter dateFormaterB = new DateTimeFormatterBuilder().append(dateFormaterA)
				.parseDefaulting(ChronoField.HOUR_OF_DAY, 23)
				.parseDefaulting(ChronoField.MINUTE_OF_HOUR, 59)
				.parseDefaulting(ChronoField.SECOND_OF_MINUTE, 59)
				.toFormatter();
		
		LocalDateTime fecEntRealDate = LocalDateTime.parse(fecString, dateFormaterB);
		
		PedidoFilesVO pedidoReg = pedidoFilesDAO.getPedidoFiles(_pedidoTrack, _pedidoExp);
		if(pedidoReg == null){
			//Registro inicial
			PedidoFilesVO pedidoAux = pedidoFilesDAO.setPedidoInicial(_context, _pedidoTrack, _pedidoExp, fecEntRealDate);
			z = pedidosActivosDAO.insertFecREAL(pedidoAux);
		} else {
			pedidoReg.setFechaEntReal(fecEntRealDate);
			z = pedidosActivosDAO.updateFecREAL(_context, _pedidoTrack, _pedidoExp, pedidoReg.getFechaEntReal());
		}
		
		return z;
	}
	
	/**
	 * setPedidoNulo
	 * 
	 * @param _context MyServiceContext
	 * @param _pedidoTrack String
	 * @param _pedidoExp String
	 * @param _motivo String
	 * 
	 * @return Integer
	 * @throws PedidoFilesException
	 */
	@Override
	public Integer setPedidoNulo(MyServiceContext _context, String _pedidoTrack, String _pedidoExp, String _motivo)
			throws PedidoFilesException {
		try {
			if(_pedidoTrack.startsWith("GRU")){
				PedidoGrupajeVO grupo = getGrupaje(_context, _pedidoTrack);
				if(grupo.getSeleccionados() != null){
					//si gru por cada pedido del grupo
					for(GrupajeItem pGR : grupo.arrayGrulog){
						String completeTR = grupo.grupTR.concat(pGR.grupNum);
						String realGruExp = pGR.expediente;
						pedidosActivosDAO.anularPedido(_context, completeTR, realGruExp, _motivo);
					}
				}
				return 1;
			} else {
				return pedidosActivosDAO.anularPedido(_context, _pedidoTrack, _pedidoExp, _motivo);
			}
		} catch (PedidoFilesException e) {
			throw new PedidoFilesException(DAOException.SQLERRCODE_QUERY, e.getMessage());
		}
	}

	/**
	 * getPedidosFiltrados
	 * 
	 * @param _context MyServiceContext
	 * @param _expCod String
	 * @param _provName String
	 * @param _track String
	 * @param _refCharge String
	 * @param _fecDesde String
	 * @param _fecHasta String
	 * 
	 * @return List<PedidoProveedorVO>
	 * @throws PedidosActivosServiceException
	 * @throws PedidoFilesException
	 */
	@Override
	public List<PedidoProveedorVO> getPedidosFiltrados(MyServiceContext _context, String _expCod, String _provName, String _track, String _refCharge, String _fecDesde, String _fecHasta) 
			throws PedidosActivosServiceException, PedidoFilesException {
		try {
			return pedidosActivosDAO.getPedidosFiltrados(_context, _expCod, _provName, _track, _refCharge, _fecDesde, _fecHasta);
		} catch (PedidosActivosException e) {
			throw new PedidosActivosServiceException(e.getMessage());
		}
	}
	
}
