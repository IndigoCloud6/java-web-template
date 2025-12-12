package com.example.template.security.filter;

import com.example.template.security.jwt.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final String tokenHeader;
    private final String tokenPrefix;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenUtil.validateToken(jwt)) {
                String username = jwtTokenUtil.extractUsername(jwt);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Set authentication for user: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("Could not set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from request header
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(tokenHeader);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(tokenPrefix)) {
            return bearerToken.substring(tokenPrefix.length());
        }
        return null;
    }

}
