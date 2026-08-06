package eus.martico.fileTransfer.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import eus.martico.fileTransfer.config.FileTransferConstantes;

public class MyServiceContext {

	protected static final Logger logger = LoggerFactory.getLogger(MyServiceContext.class);
//	protected static ConcurrentHashMap<String, NotarioVO> mapaNotarios = new ConcurrentHashMap<String, NotarioVO>();

   public static MyServiceContext getInstance() {
    	MyServiceContext context = new MyServiceContext();
    	return context;
   }
   public static MyServiceContext getInstance(Long _userNi) {
	   	MyServiceContext context = new MyServiceContext();
	   	context.setUsuarioNi(_userNi);
	   	return context;
   }
   
   private String[] userRoles;
   //Por si en algún momento se tienen que asociar por ejemplo, userInterno con una serie de proveedores
//   private String[] almacenes;
//   private String[] transportistas;
	
//	private Long 		almacenNi = FileTransferConstantes.DEFAULT_ALMACEN_NI;	
	private Long 		empresaNi = FileTransferConstantes.DEFAULT_EMPRESA_NI;
	private Long   		usuarioNi = FileTransferConstantes.DEFAULT_USER_NI;
	private String 		username;
	private String 		sessionId;

	private MyServiceContext(){
		super();
	}
//	private MyServiceContext(String[] _almacenes){
//		super();
//		this.setAlmacenes(_almacenes);
//	}
	
	public Long getUsuarioNi() {
		return usuarioNi;
	}

	public void setUsuarioNi(Long usuarioNi) {
		this.usuarioNi = usuarioNi;
	}
//	public Long getAlmacenNi() {
//		return almacenNi;
//	}
//	public void setAlmacenNi(Long almacenNi) {
//		this.almacenNi = almacenNi;
//	}
//	public String[] getAlmacenes() {
//		return this.almacenes;
//	}
//	public String[] getAlmacenesConCodigo() {
//		if (this.almacenes==null) {
//			return null;
//		} else if (Arrays.asList(this.almacenes).contains(FileTransferConstantes.ALMACEN_SIN_CODIGO)) {
//			List<String> alms = new ArrayList<String>(Arrays.asList(this.almacenes));
//			Iterator<String> it = alms.iterator();
//			while (it.hasNext()) {
//				String almCod = it.next();
//				// Quitamos de la respuesta el codigo del 0="Sin almacen"
//				if (almCod.equals(FileTransferConstantes.ALMACEN_SIN_CODIGO)) {
//					it.remove();
//					break;
//				}
//			}
//			return alms.stream().toArray(String[]::new);
//		} else {
//			return this.almacenes;
//		}
//	}
//	public List<String> getAlmacenesAsList() {
//		if (almacenes==null)
//			return null;
//		else
//			return Arrays.asList(almacenes);
//	}
//	public void setAlmacenes(String[] almacenes) {
//		this.almacenes = almacenes;
//	}
//	public String[] getTransportistas() {
//		return transportistas;
//	}
//	public List<String> getTransportistasAsList() {
//		if (transportistas==null)
//			return null;
//		else
//			return Arrays.asList(transportistas);
//	}
//	public void setTransportistas(String[] transportistas) {
//		this.transportistas = transportistas;
//	}
	public Long getEmpresaNi() {
		return empresaNi;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String[] getUserRoles() {
		return userRoles;
	}
	public void setUserRoles(String[] userRoles) {
		this.userRoles = userRoles;
	}

	public boolean isUserRol(String _rolCodigo) {
		if (this.getUserRoles()!=null && _rolCodigo!=null) {
			for (String rol: this.getUserRoles()) {
				if (!StringUtils.isEmpty(rol) && rol.equals(_rolCodigo)) {
					return true;
				}
			}
		}
		return false;
	}
}
