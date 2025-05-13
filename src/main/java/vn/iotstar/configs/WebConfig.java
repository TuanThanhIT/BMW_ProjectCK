package vn.iotstar.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vn.iotstar.interceptor.SessionInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SessionInterceptor sessionInterceptor;

    public WebConfig(SessionInterceptor sessionInterceptor) {
        this.sessionInterceptor = sessionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor)
                .addPathPatterns("/admin/**", "/seller/**", "/user/**", "/shipper/**") // Kiểm tra các đường dẫn này
                .excludePathPatterns("/login", "/register", "/public/**"); // Loại trừ các đường dẫn công khai như login, register
    }
}
