package iuh.ratelimiter.controller;

import iuh.ratelimiter.service.ServiceA;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    private final ServiceA serviceA;

    public TestController(ServiceA serviceA) {
        this.serviceA = serviceA;
    }

    @GetMapping("/test")
    public String test() {
        return serviceA.callServiceB();
    }
}
