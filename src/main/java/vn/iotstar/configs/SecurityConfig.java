package vn.iotstar.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import vn.iotstar.services.IUserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private IUserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt CSRF vì dùng JWT
//            .sessionManagement(session -> session
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không dùng session
            .requiresChannel(channel -> channel
                .anyRequest().requiresSecure()) // Ép buộc HTTPS
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers("/", "/login/**", "/register/**", "/css/**", "/js/**", "/img/**", "/lib/**", "/script/**", "/scss/**").permitAll()// Cho phép đăng nhập/đăng ký
            	.requestMatchers("/admin/**").hasRole("ADMIN")
            	.requestMatchers("/user/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/seller/**").hasRole("SELLER")
                .requestMatchers("/shipper/**").hasRole("SHIPPER")
                .anyRequest().authenticated()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID", "JWT_TOKEN", "REFRESH_TOKEN")
                .permitAll())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
 
}
