package eus.martico.fileTransfer.seguridad.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class CorsFilter extends OncePerRequestFilter {

    @SuppressWarnings("unused")
	private static final String ORIGIN = "Origin";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
    	
    	response.addHeader("Access-Control-Allow-Origin", "*");
    	response.setHeader("Access-Control-Allow-Credentials", "true");
    	response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
    	response.addHeader("Access-Control-Allow-Headers", "Authorization, clientejavascript, Content-Type");
    	response.addHeader("Access-Control-Expose-Headers", "Content-Disposition, Authorization, authorization, WWW-Authenticate, clientejavascript, Content-Type");          

    	
    	String uri = request.getRequestURI();
    	//Solo forzamos no-cache para el index o si es la raíz
    	if(uri.endsWith("index.html") || uri.endsWith("/") || uri.contains("version.json") || uri.contains("/i18n/")) {
    	    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
    	    response.setHeader("Pragma", "no-cache");
    	    response.setDateHeader("Expires", 0);
    	}

            
        if(request.getMethod().equals("OPTIONS")) {
            try {
                response.getWriter().print("OK");
                response.getWriter().flush();
            } catch (IOException e) {
            	e.printStackTrace();
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }


 }
