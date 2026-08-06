package eus.martico.fileTransfer.dao.pedido.files;

import java.time.LocalDateTime;

import eus.martico.fileTransfer.model.pedido.files.PedidoFilesVO;
import eus.martico.fileTransfer.model.pedido.proveedor.PedidoGrupajeVO;
import eus.martico.fileTransfer.services.MyServiceContext;

/**
 * 
 * @author Martico S.L. :: ialfonso
 * @version 1.0
 * @see https://www.martico.es/
 *
 */
public interface PedidoFilesIDAO {
	
	PedidoFilesVO getPedidoFiles(String _track, String _exp) throws PedidoFilesException;
	PedidoFilesVO setPedidoInicial(MyServiceContext _context, String _track, String _exp, LocalDateTime _fecEntRealDate);
	
	PedidoFilesVO updateCMR(PedidoFilesVO _pedidoF) throws PedidoFilesException;
	PedidoFilesVO updateFAC(PedidoFilesVO _pedidoF) throws PedidoFilesException;
	PedidoFilesVO updateOTR(PedidoFilesVO _pedidoF) throws PedidoFilesException;

	Integer updateESTADO(String _track, String _exp, Integer _estado) throws PedidoFilesException;
	Integer updateCMRanotaciones(String _track, String _exp, Integer _flag, String _anotacion) throws PedidoFilesException;
	
	Integer updateEstados(String _track, String _exp, Long _estCMR, Long _estFAC, Long _estadoGEN) throws PedidoFilesException;
	PedidoGrupajeVO getGrupList(MyServiceContext _context, String _track) throws PedidoFilesException;

	int countHermanosEnActivos(String currentGrupTR);
	int countHermanosPorValidarCMR(String currentGrupTR);
	int countHermanosNoValidados(String currentGrupTR);
	
}
