package eus.martico.fileTransfer.seguridad.control;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@RequestMapping(value="/home")
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String now = (new Date()).toString();
		logger.debug("Returning hello view with " + now);

		ModelAndView home = new ModelAndView("home", "now", now);

		return home;

	}
}
