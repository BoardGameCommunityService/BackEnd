package com.boardmate.config.jwt;

import com.boardmate.domain.user.User;
import com.boardmate.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // /api/test/** 경로는 토큰 검증 건너뛰기 (만료되어도 접근 가능)
        if (requestURI.startsWith("/api/test/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                // JWT 검증
                Jws<Claims> jws = jwtTokenProvider.parseToken(token);
                Claims claims = jws.getPayload();

                // 토큰 클레임에 userId가 들어있다고 가정
                Object userIdClaim = claims.get("userId");
                Long userId = null;

                if (userIdClaim instanceof Integer i) {
                    userId = i.longValue();
                } else if (userIdClaim instanceof Long l) {
                    userId = l;
                } else if (userIdClaim instanceof String s) {
                    userId = Long.parseLong(s);
                }

                if (userId == null && claims.getSubject() != null) {
                    try {
                        userId = Long.parseLong(claims.getSubject());
                    } catch (NumberFormatException ignored) {
                    }
                }

                if (userId != null) {
                    Optional<User> optionalUser = userRepository.findById(userId);
                    if (optionalUser.isPresent()) {
                        User user = optionalUser.get();

                        // DB에 role: "USER" / "ADMIN" 이런 식으로 있다고 가정
                        String role = user.getRole();
                        String roleName = role != null ? "ROLE_" + role : "ROLE_USER";

                        var authorities = List.of(new SimpleGrantedAuthority(roleName));

                        var authentication = new UsernamePasswordAuthenticationToken(
                                user, // principal
                                null, // credentials
                                authorities);

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }

            } catch (ExpiredJwtException ex) {
                // 액세스 토큰 만료 → 401 (재인증 가능)
                SecurityContextHolder.clearContext();
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "TOKEN_EXPIRED", "Access token has expired. Please refresh your token.");
                return;
            } catch (Exception ex) {
                // 기타 토큰 오류 (변조, 형식 오류 등) → 403 (재인증해도 접근 불가)
                SecurityContextHolder.clearContext();
                sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN,
                        "INVALID_TOKEN", "Invalid or malformed token.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String error, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}
