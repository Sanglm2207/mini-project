package com.kaidev99.training.miniproject.interceptor;

import com.kaidev99.training.miniproject.security.JwtService;
import com.kaidev99.training.miniproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String query = request.getURI().getQuery();
        if (query != null && query.contains("accessToken=")) {
            String token = query.substring(query.indexOf("accessToken=") + 12);
            try {
                String username = jwtService.extractUsername(token);
                if (username != null) {
                    UserDetails userDetails = userService.userDetailsService().loadUserByUsername(username);
                    if (jwtService.isTokenValid(token, userDetails)) {
                        attributes.put("userId", username);
                        log.info("User {} authenticated successfully for WebSocket.", username);
                        return true;
                    }
                }
            } catch (Exception e) {
                log.error("WebSocket handshake failed due to invalid token: {}", e.getMessage());
                return false;
            }
        }
        log.warn("WebSocket handshake failed: No token provided.");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // No-op
    }
}
