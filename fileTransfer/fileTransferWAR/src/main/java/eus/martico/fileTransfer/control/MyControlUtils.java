package eus.martico.fileTransfer.control;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Iterator;
//import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eus.martico.fileTransfer.seguridad.config.SEGConstantes;
import eus.martico.fileTransfer.seguridad.user.entity.SegurUser;
import eus.martico.fileTransfer.services.MyServiceContext;
import eus.martico.fileTransfer.utils.CryptographyGenerator;

public class MyControlUtils {

	protected static final Logger logger = LoggerFactory.getLogger(MyControlUtils.class);

	public static MyServiceContext getServiceContext(SegurUser _user) {
		MyServiceContext context = MyServiceContext.getInstance();
		if (_user!=null) {
			context.setUsuarioNi(_user.getNi());
			context.setUsername(_user.getUsername());
			context.setUserRoles(_user.getArrayAuthorities());
		}
		return context;
	}
	
	public static MyServiceContext getServiceContext(SegurUser _user, HttpServletRequest _request) {
		MyServiceContext context = getServiceContext(_user) ;
		if (_request.getSession()!=null)
			context.setSessionId(_request.getSession().getId());
		if (_request.getAttribute(SEGConstantes.TOKEN_TOVERIFY_KEY)!=null) {
			try {
				CryptographyGenerator cryptGen = new CryptographyGenerator();
				// generamos un hash con el token para identificar la sesion del usuario
				String sessionId = cryptGen.generateMD2((String)_request.getAttribute(SEGConstantes.TOKEN_TOVERIFY_KEY), 32);
				context.setSessionId(sessionId);
			} catch (NoSuchAlgorithmException e) {

			}
		}
		return context;
	}

	public static void copyInputStreamToOutputStream( final InputStream _in, OutputStream _out ) {
		try {
			byte[] buf = new byte[8*1024];
			int len;
			while((len=_in.read(buf))>0){
				_out.write(buf,0,len);
			}
			_out.close();
			_in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
