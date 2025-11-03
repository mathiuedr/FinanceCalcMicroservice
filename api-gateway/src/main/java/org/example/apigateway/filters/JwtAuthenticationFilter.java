package org.example.apigateway.filters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.apigateway.entities.AuthenticatedUser;
import org.example.apigateway.utils.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;

    private final List<String> openEndpoints = List.of(
            "/user/login",
            "/user/register"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        log.debug("Processing request to: {}", path);

        // Пропускаем запросы к открытым endpoint'ам
        if (isOpenEndpoint(path)) {
            log.debug("Open endpoint, skipping authentication");
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return onError(exchange, "Missing or invalid Authorization header",
                    HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        try {
            if (!jwtUtil.validateToken(token) || jwtUtil.isTokenExpired(token)) {
                log.warn("Invalid or expired JWT token");
                return onError(exchange, "Invalid or expired JWT token",
                        HttpStatus.UNAUTHORIZED);
            }

            AuthenticatedUser user = extractUserFromToken(token);
            log.info(String.valueOf(user));
            ServerHttpRequest modifiedRequest = addUserHeaders(request, user);

            log.debug("Successfully authenticated user: {}", user.getUsername());

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            log.error("JWT processing error: {}", e.getMessage());
            return onError(exchange, "JWT processing error", HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isOpenEndpoint(String path) {
        log.info(path);
        return openEndpoints.stream().anyMatch(path::startsWith);
    }

    private AuthenticatedUser extractUserFromToken(String token) {
        return AuthenticatedUser.builder()
                .userId(jwtUtil.extractUserId(token))
                .username(jwtUtil.extractUsername(token))
                .build();
    }

    private ServerHttpRequest addUserHeaders(ServerHttpRequest request, AuthenticatedUser user) {
        log.info(String.valueOf(user.getUserId()));
        return request.mutate()
                .header("User-Id", String.valueOf(user.getUserId()))
                .header("User-Name", user.getUsername())
                .build();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);

        String errorResponse = String.format(
                "{\"error\": \"%s\", \"timestamp\": \"%s\"}",
                error, Instant.now().toString()
        );

        DataBuffer buffer = response.bufferFactory()
                .wrap(errorResponse.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }
}