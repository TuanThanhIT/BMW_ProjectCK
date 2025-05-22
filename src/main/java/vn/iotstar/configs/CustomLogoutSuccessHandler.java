package vn.iotstar.configs;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException {
        deleteCookie(response, "JSESSIONID");
        deleteCookie(response, "JWT_TOKEN");
        deleteCookie(response, "REFRESH_TOKEN");
        deleteCookie(response, "REMEMBER_ME_TOKEN");

        response.sendRedirect("/login");
    }

    private void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true); // ⚠️ THÊM
        cookie.setSecure(true);   // ⚠️ THÊM
        cookie.setAttribute("SameSite", "Strict"); // ⚠️ THÊM (Java 11+)
        response.addCookie(cookie);
    }
}
