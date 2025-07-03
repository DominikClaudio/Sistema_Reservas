package sistema_reservas.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name="waeatherClient", url = "https://api.openweathermap.org")
public interface WeatherClient {

    @GetMapping("/data/2.5/weather")
    Map<String, Object> obtenerClimaPorCiudad(
            @RequestParam("q") String ciudad,
            @RequestParam("appid") String apikey,
            @RequestParam("units") String unidades,
            @RequestParam("lang") String lang
    );
}
