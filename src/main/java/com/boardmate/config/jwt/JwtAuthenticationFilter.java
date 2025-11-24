package com.boardmate.config.jwt;

import com.boardmate.domain.user.User;
import com.boardmate.repository.UserRepository;
import io.jsonwebtoken.Claims;
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
import java.util.List;
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
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                // ✅ NextAuth가 서명한 JWT 검증
                Jws<Claims> jws = jwtTokenProvider.parseToken(token);
                Claims claims = jws.getPayload();

                // ✅ NextAuth jwt 콜백에서 token.userId = 백엔드 userId 로 넣어놨다고 가정
                Object userIdClaim = claims.get("userId");
                Long userId = null;

                if (userIdClaim instanceof Integer i) {
                    userId = i.longValue();
                } else if (userIdClaim instanceof Long l) {
                    userId = l;
                } else if (userIdClaim instanceof String s) {
                    userId = Long.parseLong(s);
                }

                // (옵션) 예전 방식처럼 sub에 userId가 들어오는 것도 함께 지원하고 싶다면:
                if (userId == null && claims.getSubject() != null) {
                    try {
                        userId = Long.parseLong(claims.getSubject());
                    } catch (NumberFormatException ignored) {}
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
                                user,      // principal
                                null,      // credentials
                                authorities
                        );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }

            } catch (Exception ex) {
                // 토큰이 유효하지 않으면 인증 정보 비우고 넘어감 → 나중에 401
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
