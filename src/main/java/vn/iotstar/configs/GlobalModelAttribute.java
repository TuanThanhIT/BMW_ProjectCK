package vn.iotstar.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import vn.iotstar.entity.User;
import vn.iotstar.services.IUserService;

@ControllerAdvice
public class GlobalModelAttribute {

    @Autowired
    private IUserService userService;

    @ModelAttribute
    public void addUserToModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName(); // lấy username từ SecurityContext
            User user = userService.loadUserByUsername(username); // lấy entity User từ DB
            if (user != null) {
                model.addAttribute("user", user);
            }
        }
    }
}