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
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
	        .headers(headers -> headers
	                .contentSecurityPolicy(csp -> csp
	                    .policyDirectives(
	                        "default-src 'self'; " +
	                        "script-src 'self' https://code.jquery.com https://cdnjs.cloudflare.com https://cdn.jsdelivr.net https://ajax.googleapis.com https://static.elfsight.com https://cdn2.fptshop.com.vn https://www.youtube.com https://unpkg.com https://stackpath.bootstrapcdn.com; " +
	                        "style-src 'self' https://fonts.googleapis.com https://cdnjs.cloudflare.com https://cdn.jsdelivr.net https://stackpath.bootstrapcdn.com https://use.fontawesome.com https://unpkg.com; " +
	                        "font-src 'self' https://fonts.gstatic.com https://cdnjs.cloudflare.com https://cdn.jsdelivr.net https://use.fontawesome.com https://unpkg.com; " +
	                        "img-src 'self' data: https://cdn2.fptshop.com.vn https://source.unsplash.com https://www.youtube.com https://oola.vn https://www.facebook.com https://www.twitter.com https://www.instagram.com https://undraw.co; " +
	                        "object-src 'none'; " +
	                        "frame-ancestors 'none'; " +
	                        "base-uri 'self';"
	                    )
	                )
	            );

        return http.build();
    }
 
}
