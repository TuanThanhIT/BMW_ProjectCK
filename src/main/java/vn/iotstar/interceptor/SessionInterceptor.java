package vn.iotstar.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.User;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("account") != null) {
            User user = (User) session.getAttribute("account");
            request.setAttribute("user", user); // Lưu thông tin người dùng vào request scope

            // Lấy role từ đối tượng User
            String role = user.getRole().getRoleName();
            String requestURI = request.getRequestURI();

            // Kiểm tra quyền truy cập của người dùng vào các trang
            if (requestURI.startsWith("/admin") && !"ADMIN".equals(role)) {
                session.invalidate(); // Xóa session nếu người dùng không phải ADMIN
                response.sendRedirect("/login");
                return false;
            }
            if (requestURI.startsWith("/seller") && !"SELLER".equals(role)) {
                session.invalidate(); // Xóa session nếu người dùng không phải SELLER
                response.sendRedirect("/login");
                return false;
            }
            if (requestURI.startsWith("/shipper") && !"SHIPPER".equals(role)) {
                session.invalidate(); // Xóa session nếu người dùng không phải SHIPPER
                response.sendRedirect("/login");
                return false;
            }
            if (requestURI.startsWith("/user") && !("USER".equals(role) || "ADMIN".equals(role) || "SELLER".equals(role) || "SHIPPER".equals(role))) {
                session.invalidate(); // Xóa session nếu người dùng không phải USER, ADMIN, SELLER, hoặc SHIPPER
                response.sendRedirect("/login");
                return false;
            }
        } else {
            // Kiểm tra khi không có session, không cho phép truy cập vào các đường dẫn của user từ Admin/Seller/Shipper
            String requestURI = request.getRequestURI();
            // Nếu không có session, không cho phép vào bất kỳ trang nào ngoại trừ login và register
            if (requestURI.startsWith("/user")) {
                response.sendRedirect("/login"); // Nếu không có session, chuyển hướng về trang login
                return false;
            }
            
            // Kiểm tra các đường dẫn login và register
            if (!requestURI.startsWith("/login") && !requestURI.startsWith("/register")) {
                response.sendRedirect("/login"); // Chuyển hướng nếu chưa đăng nhập
                return false;
            }
        }

        return true; // Tiếp tục request nếu không có vấn đề
    }
}
