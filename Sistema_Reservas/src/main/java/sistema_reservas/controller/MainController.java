package sistema_reservas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MainController {

	@GetMapping("/")
	public String inicio(HttpServletRequest request, Model model) {
		model.addAttribute("currentPath", request.getRequestURI());
		return "index";
	}
	@GetMapping("/habitaciones")
	public String habitaciones(HttpServletRequest request, Model model) {
		model.addAttribute("currentPath", request.getRequestURI());
		return "habitaciones";
	}



}
