//package eus.martico.fileTransfer.dao.firmador;
//
//import java.util.List;
//
//import eus.martico.fileTransfer.dao.usuario.UsuarioDAOException;
//import eus.martico.fileTransfer.model.firma.PedidoFirmadorVO;
//import eus.martico.fileTransfer.model.firma.PipelineFirmaDTO;
//import eus.martico.fileTransfer.model.vo.ValidateDTO;
//import eus.martico.fileTransfer.services.MyServiceContext;
//
///**
// * XXX descartado firmador
// * @author Martico S.L. :: ialfonso
// * @version 1.0
// * @see https://www.martico.es/
// *
// */
//public interface FirmadorIDAO {
//	
//	/**
//	 * getPedidoFirma
//	 * @param _track String
//	 * @param _exp String
//	 * @return PedidoFirmadorVO
//	 * @throws FirmadorException
//	 */
//	PedidoFirmadorVO getPedidoFirma(String _track, String _exp) throws FirmadorException;
//	
//	/**
//	 * updateEstadoFirma
//	 * @param _track String
//	 * @param _exp String
//	 * @param _estadoCMRfirma Long
//	 * @return Integer
//	 * @throws FirmadorException
//	 */
//	Integer updateEstadoFirma(String _track, String _exp, Long _estadoCMRfirma) throws FirmadorException;
//	
//	/**
//	 * getPedidosFiltrados
//	 * @param _context MyServiceContext
//	 * @param _expCod String
//	 * @param _provName String
//	 * @param _track String
//	 * @param _refCharge String
//	 * @return List<PedidoFirmadorVO>
//	 * @throws FirmadorException
//	 * @throws UsuarioDAOException
//	 */
//	List<PedidoFirmadorVO> getPedidosFiltrados(MyServiceContext _context, String _expCod, String _provName, String _track, String _refCharge) 
//			throws FirmadorException, UsuarioDAOException;
//
//	/**
//	 * añadirFirmaIncremental
//	 * @param _context MyServiceContext
//	 * @param _firmaDTO PipelineFirmaDTO
//	 * @return ValidateDTO<PedidoFirmadorVO>
//	 * @throws FirmadorException
//	 */
//	ValidateDTO<PedidoFirmadorVO> añadirFirmaIncremental(MyServiceContext _context, PipelineFirmaDTO _firmaDTO)
//			throws FirmadorException;
//
//}
