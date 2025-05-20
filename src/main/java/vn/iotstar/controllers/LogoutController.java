package vn.iotstar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.services.IRefreshTokenService;

public class LogoutController {
	@Autowired
	private IRefreshTokenService refreshTokenService;
	
	@PostMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("JWT_TOKEN".equals(cookie.getName())) {
	                cookie.setMaxAge(0);
	                cookie.setPath("/");
	                response.addCookie(cookie);
	            }
	            if ("REFRESH_TOKEN".equals(cookie.getName())) {
	                refreshTokenService.revokeRefreshToken(cookie.getValue());
	                cookie.setMaxAge(0);
	                cookie.setPath("/");
	                response.addCookie(cookie);
	            }
	            if ("remember-me".equals(cookie.getName())) {
	                cookie.setMaxAge(0);
	                cookie.setPath("/");
	                response.addCookie(cookie);
	            }
	        }
	    }
	    return "redirect:/login";
	}
}
