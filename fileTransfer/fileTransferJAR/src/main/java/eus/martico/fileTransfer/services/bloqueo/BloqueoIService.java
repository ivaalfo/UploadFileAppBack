package eus.martico.fileTransfer.services.bloqueo;

import eus.martico.fileTransfer.model.bloqueo.BloqueoVO;
import eus.martico.fileTransfer.services.MyServiceContext;

public interface BloqueoIService {

	BloqueoVO iniciar(MyServiceContext _context, String _tipo, String _codigo) throws BloqueoServiceException;
	BloqueoVO iniciar(MyServiceContext _context, String _tipo, String _codigo, long _userBloqNi) throws BloqueoServiceException;
//	BloqueoVO iniciar(MyServiceContext _context, long _tipoNi, String _codigo, long _userBloqNi) throws BloqueoServiceException;
	
	BloqueoVO getActivo(MyServiceContext _context, String _tipo, String _codigo) throws BloqueoServiceException;
//	BloqueoVO getActivo(MyServiceContext _context, long _tipoNi, String _codigo) throws BloqueoServiceException;
	
	int finalizar(MyServiceContext _context, String _tipo, String _codigo) throws BloqueoServiceException;
	int finalizar(MyServiceContext _context, String _tipo, String _codigo, long _userBloqNi) throws BloqueoServiceException;
	int finalizar(String _tipo) throws BloqueoServiceException;
	int finalizarAll() throws BloqueoServiceException;
	
	int marcarAccion(MyServiceContext _context, BloqueoVO _bloqueo, byte _accion) throws BloqueoServiceException;
	
	BloqueoVO getSavedLastUser(MyServiceContext _context, String _tipo, String _codigo)	throws BloqueoServiceException;
	
}
