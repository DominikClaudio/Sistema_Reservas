package sistema_reservas.login;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class Login {

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model, @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("currentPath", request.getRequestURI());
        if (error != null) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
        }
        System.out.println("Mostrando página de login (GET)");
        return "login/login";
    }
}
