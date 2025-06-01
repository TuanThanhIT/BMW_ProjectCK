package vn.iotstar.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Autowired
	private RateLimitFilter rateLimitFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.requiresChannel(channel -> channel
					.anyRequest().requiresSecure())
				.sessionManagement(session -> session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/", "/login/**", "/register/**", "/forgot-password/**", "/reset-password/**", "/verify-otp/**", "/verify-otp-password/**","/css/**", "/js/**", "/img/**", "/lib/**", "/script/**", "/scss/**").permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.requestMatchers("/user/**").hasAnyRole("ADMIN", "USER", "SELLER", "SHIPPER")
						.requestMatchers("/seller/**").hasRole("SELLER")
						.requestMatchers("/shipper/**").hasRole("SHIPPER")
						.anyRequest().authenticated()
				)
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessHandler(new CustomLogoutSuccessHandler())
						.invalidateHttpSession(true)
						.clearAuthentication(true)
						.permitAll())
				.addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.headers(headers -> headers
						.contentSecurityPolicy(csp -> csp
								.policyDirectives(
										"default-src 'self'; " +
												"script-src 'self' 'unsafe-inline' https://code.jquery.com https://cdnjs.cloudflare.com https://cdn.jsdelivr.net https://ajax.googleapis.com https://static.elfsight.com https://cdn2.fptshop.com.vn https://www.youtube.com https://unpkg.com https://stackpath.bootstrapcdn.com https://www.google.com https://www.gstatic.com /js/register.js; " + 
												"style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://cdnjs.cloudflare.com https://cdn.jsdelivr.net https://stackpath.bootstrapcdn.com https://use.fontawesome.com https://unpkg.com https://www.google.com; " +
												"font-src 'self' https://fonts.gstatic.com https://cdnjs.cloudflare.com https://cdn.jsdelivr.net https://use.fontawesome.com https://unpkg.com; " +
												"img-src 'self' data: https://cdn2.fptshop.com.vn https://source.unsplash.com https://www.youtube.com https://oola.vn https://www.facebook.com https://www.twitter.com https://www.instagram.com https://undraw.co; " +
												"frame-src 'self' https://www.google.com; " + // Thêm frame-src cho iframe reCAPTCHA
												"connect-src 'self' https://www.google.com; " +
												"object-src 'none'; " +
												"base-uri 'self'; " +
												"frame-ancestors 'none';"
								)
						)
				);

		return http.build();
	}
}

// Có thể xóa unsafe-inline nếu không cần thiết để chống XSS