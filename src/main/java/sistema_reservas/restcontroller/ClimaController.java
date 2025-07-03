package sistema_reservas.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sistema_reservas.feign.WeatherClient;

import java.util.Map;

@RestController
@RequestMapping("/api/clima")
public class ClimaController {

    @Autowired
    private WeatherClient weatherClient;

    private final String API_KEY = "28d1a4616c67509a76beb8c9fabf0c8d";

    @GetMapping("/{ciudad}")
    public ResponseEntity<?> climaPorCiudad(@PathVariable String ciudad){
        Map<String, Object> datos = weatherClient.obtenerClimaPorCiudad(ciudad, API_KEY, "metric", "es");
        return ResponseEntity.ok(datos);
    }
}
