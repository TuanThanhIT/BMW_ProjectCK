package vn.iotstar.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        // Ghi log lỗi
        logger.error("Lỗi xảy ra: ", ex);
        model.addAttribute("message", "Đã xảy ra lỗi, vui lòng thử lại sau.");
        return "error"; // Trả lỗi về trang error.htmlbv
    }
}
