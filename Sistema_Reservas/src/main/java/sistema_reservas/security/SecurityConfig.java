package sistema_reservas.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import sistema_reservas.login.ManejadorLogin;
import sistema_reservas.model.Usuario;
import sistema_reservas.repository.UsuarioRepository;
import sistema_reservas.service.UsuarioService;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
	
	@Autowired
    private UserDetailsService userDetailsService;
	@Autowired
	private ManejadorLogin successHandler; 
		   
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
		.authorizeHttpRequests(auth -> auth 
		        .requestMatchers(
		                "/css/**",
		                "/imagenes/**",
		                "/",                  
		                "/login/**", 
		                "/login/registroUsuario",
		                "/habitaciones",       
		                "/habitaciones/**"     
		        ).permitAll()
		        .requestMatchers("/admin/**").hasRole("ADMIN")
		        .requestMatchers("/cliente/**").hasRole("CLIENTE")
		        .anyRequest().authenticated()
				)
		.formLogin(form -> form
				.loginPage("/login")
				.successHandler(successHandler)
				.failureUrl("/login?error=true")
				.permitAll()
				)
		.logout(logout -> logout
                .logoutUrl("/logout") 
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID") 
                .permitAll()
            );
		System.out.println("Security configurado con UserDetailsService: " + userDetailsService.getClass().getSimpleName());

		return http.build();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	}
	
	

}
