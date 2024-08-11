package br.com.cognito_teste.filter;

import br.com.cognito_teste.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {


    @Autowired
    private JwtUtil jwtUtil;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/users/sign-up",
            "/api/users/sign-in"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean result = false;
        for (String endPoint : PUBLIC_ENDPOINTS) {
            if (request.getRequestURI().equals(endPoint)) {
                result = true;
                break;
            }
        }
        if (result) {
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader("Authorization");
            String accessToken = null;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                accessToken = authorizationHeader.substring("Bearer ".length());
                try {
                    Map<String, Object> map = jwtUtil.getValueFromJwt(accessToken);
                    if (map != null) {
                        Date expirationTime = map.get("exp") != null ? new Date((long) map.get("exp") * 1000) : null;
                        if (expirationTime != null && expirationTime.before(new Date())) {
                            response.sendError(401, "Unauthorized");
                        }
                        String username = (String) map.get("sub");
                        List<GrantedAuthority> authorities = new ArrayList<>();
                        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        filterChain.doFilter(request, response);
                    } else {
                        response.sendError(401, "Unauthorized");
                    }
                } catch (Exception e) {
                    log.error("Exception {}", e.getMessage());
                    response.sendError(401, "Unauthorized");
                }
            } else {
                response.sendError(401, "Unauthorized");
            }
        }
    }
}
