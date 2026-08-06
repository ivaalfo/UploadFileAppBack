package eus.martico.fileTransfer.services.usuario.rol;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eus.martico.fileTransfer.config.FileTransferConstantes;
import eus.martico.fileTransfer.dao.usuario.rol.UsuarioRolDAOException;
import eus.martico.fileTransfer.dao.usuario.rol.UsuarioRolIDAO;
import eus.martico.fileTransfer.model.rol.RolVO;
import eus.martico.fileTransfer.model.tabladetablas.vo.TablaDetalleVO;
import eus.martico.fileTransfer.model.usuario.rol.UsuarioRolVO;
import eus.martico.fileTransfer.services.tabladetablas.TablaDeTablasIService;
import eus.martico.fileTransfer.services.tabladetablas.TablaDeTablasServiceException;

@Service(value="UsuarioRolService")
public class UsuarioRolServiceImp implements UsuarioRolIService {

	protected static final Logger logger = LoggerFactory.getLogger(UsuarioRolServiceImp.class);
	
	private static Object sincronizador = new Object();
	private static List<TablaDetalleVO> roles = null;
	
	@Autowired
	UsuarioRolIDAO usuarioRolDAO;

	@Autowired
	TablaDeTablasIService tablaDeTablasService;
	
	public UsuarioRolServiceImp(UsuarioRolIDAO usuarioRolDAO, TablaDeTablasIService tablaDeTablasService) 
			throws UsuarioRolServiceException {
		super();
		this.usuarioRolDAO = usuarioRolDAO;
		this.tablaDeTablasService = tablaDeTablasService;
//		initUsuarioRoles();
	}

	@Override
	@Transactional(rollbackFor = {UsuarioRolServiceException.class} )
	public UsuarioRolVO alta(UsuarioRolVO _usuarioRol) throws UsuarioRolServiceException {
		try {
			return usuarioRolDAO.alta(_usuarioRol);
		} catch (UsuarioRolDAOException e) {
			throw new UsuarioRolServiceException(e.getMessage());
		}
	}

	@Override
	public Integer eliminar(long ni) throws UsuarioRolServiceException {
		try {
			return usuarioRolDAO.eliminar(ni);
		} catch (UsuarioRolDAOException e) {
			throw new UsuarioRolServiceException(e.getMessage());
		}
	}	

	@Override
	public RolVO getRolByCodigo(String _codigoRol) throws UsuarioRolServiceException {
		if (UsuarioRolServiceImp.roles==null)
			initUsuarioRoles();
		// Buscamos en  la lista
		Optional<TablaDetalleVO> resultadoRol = roles.stream()
				.filter((rol) -> rol.getCodigo().equalsIgnoreCase(_codigoRol)).findFirst();
		if (resultadoRol.isPresent()) {
			RolVO rol = new RolVO();
			rol.setNi(resultadoRol.get().getNi());
			rol.setCodigo(resultadoRol.get().getCodigo());
			return rol;
		}
		else
			return null;
	}
	
	private synchronized void initUsuarioRoles() throws UsuarioRolServiceException {
		synchronized (UsuarioRolServiceImp.sincronizador) {
			if (UsuarioRolServiceImp.roles==null) {
				try {
					UsuarioRolServiceImp.roles = Collections.synchronizedList(tablaDeTablasService.getByCodigoCabecera(FileTransferConstantes.TT_AUTOR_ROLES));
				} catch (TablaDeTablasServiceException ex) {
					logger.error("Error leyendo los roles", ex);
				}
				if (UsuarioRolServiceImp.roles==null)
					logger.error("No se han encontrado los roles");
					throw new UsuarioRolServiceException("No se han encontrado los roles");
			}
		}
	}

}
