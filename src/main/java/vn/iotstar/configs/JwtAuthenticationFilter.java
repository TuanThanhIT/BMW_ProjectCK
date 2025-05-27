package vn.iotstar.configs;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.services.IRefreshTokenService;
import vn.iotstar.services.impl.UserServiceImpl;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserServiceImpl userDetailsService;
    @Autowired
    private IRefreshTokenService refreshTokenService;
    
    @Value("${jwt.expiration}")
    private long accessTokenExpiration;
    
    @Value("${jwt.refreshExpiration}")
    private long refreshTokenExpiration;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
   	
        String token = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromJWT(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {

            String currentIp = request.getRemoteAddr();
            String currentUserAgent = request.getHeader("User-Agent");
        	// Kiểm tra và xử lý Refresh Token nếu có
            String refreshToken = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("REFRESH_TOKEN".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                    }
                }
            }

            if (refreshToken != null && refreshTokenService.validateRefreshToken(refreshToken, currentIp, currentUserAgent)) {
                String username = refreshTokenService.findByToken(refreshToken).get().getUsername();
                String newAccessToken = jwtTokenProvider.generateAccessToken(username);

                // Xoá refresh token cũ
                refreshTokenService.revokeRefreshToken(refreshToken);
                // Sinh refresh token mới
                String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);
                String ip = request.getRemoteAddr();
				String userAgent = request.getHeader("User-Agent");
                refreshTokenService.createRefreshToken(username, newRefreshToken, ip, userAgent);

                // Gửi lại JWT mới cho client
                Cookie jwtCookie = new Cookie("JWT_TOKEN", newAccessToken);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setSecure(request.isSecure());
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge((int)(accessTokenExpiration / 1000)); // 15 phút
                jwtCookie.setAttribute("SameSite", "Strict");
                response.addCookie(jwtCookie);

                Cookie refreshCookie = new Cookie("REFRESH_TOKEN", newRefreshToken);
                refreshCookie.setHttpOnly(true);
                refreshCookie.setSecure(request.isSecure());
                refreshCookie.setPath("/");
                refreshCookie.setMaxAge((int)(refreshTokenExpiration / 1000)); 
                refreshCookie.setAttribute("SameSite", "Strict");
                response.addCookie(refreshCookie);

                // Tiếp tục filter chain sau khi làm mới token
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }
}
