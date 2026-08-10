//package eus.martico.fileTransfer.seguridad.control;
//
//import static lombok.AccessLevel.PACKAGE;
//import static lombok.AccessLevel.PRIVATE;
//
//import java.io.IOException;
//import java.util.Date;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
////import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.ModelAndView;
//
//import eus.martico.fileTransfer.seguridad.auth.api.UserAuthenticationService;
//import eus.martico.fileTransfer.seguridad.service.user.LoginUserIService;
//import lombok.AllArgsConstructor;
//import lombok.NonNull;
//import lombok.experimental.FieldDefaults;
//
//@Controller
//@RequestMapping("/public/users")
//@FieldDefaults(level = PRIVATE, makeFinal = true)
//@AllArgsConstructor(access = PACKAGE)
//final class PublicUsersController {
//	
//	@NonNull
//	UserAuthenticationService authentication;
//	@NonNull
//	LoginUserIService loginUser;
//
//	@RequestMapping(value="/hello")
//	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//
//		String now = (new Date()).toString();
//		ModelAndView home = new ModelAndView("index", "now", now);
//
//		return home;
//	}
//
//} OLD BORRAR
