package iuh.ratelimiter.service;

import org.springframework.stereotype.Service;

@Service
public class ServiceB {

    public String process() {
        return "Service B processed!";
    }
}
