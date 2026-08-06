package eus.martico.fileTransfer.seguridad.security;

import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
class NoRedirectStrategy implements RedirectStrategy {

  @Override
  public void sendRedirect(final HttpServletRequest request, final HttpServletResponse response, final String url) throws IOException {
      // No redirect is required with pure REST
  }
}