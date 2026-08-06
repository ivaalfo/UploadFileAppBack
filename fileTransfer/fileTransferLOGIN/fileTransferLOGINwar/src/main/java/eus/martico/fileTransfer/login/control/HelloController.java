package eus.martico.fileTransfer.login.control;

import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hello")
public class HelloController {

	@GetMapping
	public String goHomeView(HttpServletRequest req) {
        X509Certificate[] certs = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");
        if (null != certs && certs.length > 0) {
            return "hello";
        }
		return "hello";
	}
}
