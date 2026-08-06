package eus.martico.fileTransfer.login.control;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class PreventSessions implements HttpSessionListener {

	@Override
    public void sessionCreated(HttpSessionEvent se) {
		if (se.getSession()!=null)
			se.getSession().invalidate();
//        throw new UnsupportedOperationException("sessions are not allowed");      
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        throw new UnsupportedOperationException("sessions are not allowed");
    }
}
