package eus.martico.fileTransfer.services.bloqueo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.dao.bloqueo.BloqueoDAOException;
import eus.martico.fileTransfer.dao.bloqueo.BloqueoIDAO;
import eus.martico.fileTransfer.model.bloqueo.BloqueoVO;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.services.tabladetablas.TablaDeTablasIService;
import eus.martico.fileTransfer.services.tabladetablas.TablaDeTablasServiceException;

@Service(value = "BloqueoService")
public class BloqueoServiceImp implements BloqueoIService {
	
	protected static final Logger logger = LoggerFactory.getLogger(BloqueoServiceImp.class);

	@Autowired()
	private BloqueoIDAO bloqueoDAO;
	
	@Autowired()
	private TablaDeTablasIService tablaDeTablasService;

	@Override
	@Transactional(rollbackFor = BloqueoServiceException.class)
	public BloqueoVO iniciar(MyServiceContext _context, String _tipo, String _codigo) throws BloqueoServiceException {
		// recibimos el codigo del tipo y lo convertimos a Ni
		return iniciar(_context, _tipo, _codigo, _context.getUsuarioNi());
	}
	
	@Override
	@Transactional(rollbackFor = BloqueoServiceException.class)
	public BloqueoVO iniciar(MyServiceContext _context, String _tipo, String _codigo, long _userBloqNi) throws BloqueoServiceException {
		// recibimos el codigo del tipo y lo convertimos a Ni
		try {
			BloqueoVO bloqueo = iniciar(_context, tablaDeTablasService.getNi(FileTransferConstantes.TT_TIPOS_BLOQUEOS, _tipo), _codigo, _userBloqNi);
			return bloqueo;
		} catch (TablaDeTablasServiceException e) {
			throw new BloqueoServiceException(e);
		}
	}

	@Transactional(rollbackFor = BloqueoServiceException.class)
	private BloqueoVO iniciar(MyServiceContext _context, long _tipoNi, String _codigo, long _userBloqNi ) throws BloqueoServiceException {
		// recibimos el codigo del tipo y lo convertimos a Ni
		try {
			return bloqueoDAO.iniciar(_tipoNi, _codigo, _userBloqNi, _context.getSessionId());
		} catch (BloqueoDAOException e) {
			logger.error("iniciar :: "+e.getMsgError());
			throw new BloqueoServiceException(e);
		}
	}

	@Override
	public BloqueoVO getActivo(MyServiceContext _context, String _tipo, String _codigo) throws BloqueoServiceException {
		try {
			return bloqueoDAO.getActivo(_tipo, _codigo);
		} catch (BloqueoDAOException e) {
			logger.error("getActivo :: "+e.getMsgError());
			throw new BloqueoServiceException(e);
		}
	}

//	@Override
//	public BloqueoVO getActivo(MyServiceContext _context, long _tipoNi, String _codigo) throws BloqueoServiceException {
//		try {
//			return bloqueoDAO.getActivo(_tipoNi, _codigo);
//		} catch (BloqueoDAOException e) {
//			logger.error("getActivo :: "+e.getMsgError());
//			throw new BloqueoServiceException(e);
//		}
//	}

	@Override
	public int finalizar(MyServiceContext _context, String _tipo, String _codigo) throws BloqueoServiceException {
		// recibimos el codigo del tipo y lo convertimos a Ni
		return finalizar(_context, _tipo, _codigo, _context.getUsuarioNi());
	}
	
	@Override
	public int finalizar(MyServiceContext _context, String _tipo, String _codigo, long _userBloqNi) throws BloqueoServiceException {
		// recibimos el codigo del tipo y lo convertimos a Ni
		try {
			return bloqueoDAO.finalizar(tablaDeTablasService.getNi(FileTransferConstantes.TT_TIPOS_BLOQUEOS, _tipo), _codigo, _userBloqNi);
		} catch (BloqueoDAOException | TablaDeTablasServiceException e) {
			logger.error("finalizar :: "+e.getMsgError());
			throw new BloqueoServiceException(e);
		}
	}
	
	@Override
	public int finalizar(String _tipo) throws BloqueoServiceException {
		try {
			return bloqueoDAO.finalizar(tablaDeTablasService.getNi(FileTransferConstantes.TT_TIPOS_BLOQUEOS, _tipo));
		} catch (BloqueoDAOException | TablaDeTablasServiceException e) {
			logger.error("finalizar :: "+e.getMsgError());
			throw new BloqueoServiceException(e);
		}
	}
	
	@Override
	public int finalizarAll() throws BloqueoServiceException {
		// recibimos el codigo del tipo y lo convertimos a Ni
		try {
			return bloqueoDAO.finalizarAll();
		} catch (BloqueoDAOException e) {
			logger.error("finalizarAll :: "+e.getMsgError());
			throw new BloqueoServiceException(e);
		}
	}
	
	@Override
	public int marcarAccion(MyServiceContext _context, BloqueoVO _bloqueo, byte _accion) throws BloqueoServiceException {
		try {
			return bloqueoDAO.marcarAccion(_bloqueo, _accion);
		} catch (BloqueoDAOException e) {
			logger.error("marcarAccion :: "+e.getMsgError());
			throw new BloqueoServiceException(e);
		}
	}
	
	@Override
	public BloqueoVO getSavedLastUser(MyServiceContext _context, String _tipo, String _codigo) throws BloqueoServiceException {
		try {
			return bloqueoDAO.getSavedLastUser(_tipo, _codigo);
		} catch (BloqueoDAOException e) {
			logger.error("getSavedLastUser :: "+e.getMsgError());
			throw new BloqueoServiceException(e);
		}
	}

}
