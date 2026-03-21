package iuh.ratelimiter.service;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;

@Service
public class ServiceA {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ServiceA.class);

    private final ServiceB serviceB;

    public ServiceA(ServiceB serviceB) {
        this.serviceB = serviceB;
    }

    @RateLimiter(
        name = "serviceBRateLimiter",
        fallbackMethod = "rateLimiterFallback"
    )
    public String callServiceB() {
        logger.info("Calling Service B...");
        return serviceB.process();
    }

    public String rateLimiterFallback(RequestNotPermitted ex) {
        logger.warn("Rate limit exceeded! Fallback triggered.");
        return "Too many requests! Please try again later.";
    }
}
