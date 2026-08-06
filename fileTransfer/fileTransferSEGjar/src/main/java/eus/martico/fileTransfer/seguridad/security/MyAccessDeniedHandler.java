package eus.martico.fileTransfer.seguridad.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

public class MyAccessDeniedHandler implements AccessDeniedHandler {

	protected static final Logger logger = LoggerFactory.getLogger(MyAccessDeniedHandler.class);

  @Override
  public void handle(
    HttpServletRequest request,
    HttpServletResponse response, 
    AccessDeniedException exc) throws IOException, ServletException {
       
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if(auth != null){
    	  String error = "User: " + auth.getName() 
          + " attempted to access the protected URL: "
          + request.getRequestURI();
    	  logger.warn(error);
          request.setAttribute("error", error);
      }
      
      response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
      
      //response.sendRedirect(request.getContextPath() + "/error");
  }

}
