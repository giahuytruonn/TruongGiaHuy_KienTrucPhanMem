package iuh.fit.bai2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/api/admin/dashboard")
    @ResponseBody
    public String adminDashboard() {
        return "Welcome to Admin Dashboard";
    }

    @GetMapping("/api/staff/info")
    @ResponseBody
    public String staffInfo() {
        return "Staff Information Access";
    }

    @GetMapping("/api/public/hello")
    @ResponseBody
    public String hello() {
        return "Hello World!";
    }
}
