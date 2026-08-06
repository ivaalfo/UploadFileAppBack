package eus.martico.fileTransfer.dao.bloqueo;

import eus.martico.fileTransfer.model.bloqueo.BloqueoVO;

public interface BloqueoIDAO {

	BloqueoVO iniciar(long _tipoNi, String _codigo, Long _usuarioNi, String _sessionId) throws BloqueoDAOException;
	
	BloqueoVO getActivo(String _tipo, String _codigo) throws BloqueoDAOException;
	BloqueoVO getActivo(long _tipoNi, String _codigo) throws BloqueoDAOException;
	
	BloqueoVO getBloqueo(long _bloqueoNi) throws BloqueoDAOException;
	
	int finalizar(long _tipoNi, String _codigo, Long _usuarioNi) throws BloqueoDAOException;
//	int finalizar(Long _tipoNi, Long _usuarioNi) throws BloqueoDAOException;
	int finalizar(long _tipoNi) throws BloqueoDAOException;
	int finalizarAll() throws BloqueoDAOException;
	
	int marcarAccion(BloqueoVO _bloqueo, byte _accion) throws BloqueoDAOException;
	
	BloqueoVO getSavedLastUser(String _tipo, String _codigo) throws BloqueoDAOException;
}
