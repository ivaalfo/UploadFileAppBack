package eus.martico.fileTransfer.control.bloqueo;

import eus.martico.fileTransfer.model.bloqueo.BloqueoControlVO;
import eus.martico.fileTransfer.model.bloqueo.BloqueoRespVO;
import eus.martico.fileTransfer.model.bloqueo.BloqueoVO;
import eus.martico.fileTransfer.model.vo.ValidateDTO;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.services.bloqueo.BloqueoServiceException;

public interface BloqueoComponentIService {

	ValidateDTO<BloqueoVO> iniciar(MyServiceContext _context, String _tipo, String _codigo, long _userNi) throws BloqueoServiceException;

	ValidateDTO<BloqueoVO> finalizar(MyServiceContext _context, String _tipo, String _codigo, long _userNi) throws BloqueoServiceException; 

	ValidateDTO<BloqueoVO> forzarFinalizar(MyServiceContext _context, String _tipo, String _codigo, long _userNi) throws BloqueoServiceException;

	BloqueoVO comprobarBloqueoUsuario(MyServiceContext _context, String _tipo, String _codigo, long _userNi) throws BloqueoServiceException;		

	BloqueoControlVO comprobarBloqueoUsuarioSession(MyServiceContext _context, String _tipo, String _codigo, final long _userNi) throws BloqueoServiceException;		

	ValidateDTO<BloqueoVO> reiniciar(MyServiceContext _context, String _tipo, String _codigo, long _userNi) throws BloqueoServiceException;		

	int marcarAccion (MyServiceContext _context, BloqueoVO _bloqueo, byte _accion) throws BloqueoServiceException;

	ValidateDTO<BloqueoRespVO> getSavedLastUser(MyServiceContext _context, String _tipo, String _codigo) throws BloqueoServiceException;
}
