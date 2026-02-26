package com.bank.apigateway.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/v1/auth/**")
                        .uri("lb://auth-service"))

                .route("account-service", r -> r.path("/api/v1/accounts/**")
                        .filters(f -> f.filter(jwtAuthFilter))
                        .uri("lb://account-service"))

                .route("transaction-service", r -> r.path("/api/v1/transactions/**")
                        .filters(f -> f.filter(jwtAuthFilter))
                        .uri("lb://transaction-service"))

                .route("notification-service", r -> r.path("/api/v1/notifications/**")
                        .filters(f -> f.filter(jwtAuthFilter))
                        .uri("lb://notification-service"))

                .route("audit-service", r -> r.path("/api/v1/audit/**")
                        .filters(f -> f.filter(jwtAuthFilter))
                        .uri("lb://audit-service"))

                .route("loan-service", r -> r.path("/api/v1/loans/**")
                        .filters(f -> f.filter(jwtAuthFilter))
                        .uri("lb://loan-service"))

                .build();
    }
}
