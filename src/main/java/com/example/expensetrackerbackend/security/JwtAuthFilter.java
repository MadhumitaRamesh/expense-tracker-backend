package com.example.expensetrackerbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        System.out.println("DEBUG: incoming request to " + request.getRequestURI());
        String token = null;
        String username = null;

        // Expect header: Authorization: Bearer <token>
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim();
            System.out.println("DEBUG: Token found, length: " + token.length());
            try {
                username = jwtService.extractUsername(token);
                System.out.println("DEBUG: Extracted username: " + username);
            } catch (Exception e) {
                System.out.println("DEBUG: Failed to extract username: " + e.getMessage());
            }
        } else {
            System.out.println("DEBUG: No Bearer token in Authorization header. Header exists: " + (authHeader != null));
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(token, userDetails.getUsername())) {
                    System.out.println("Token is valid for user: " + username);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println("Token is INVALID for user: " + username);
                }
            } catch (Exception e) {
                System.out.println("Error loading user Details: " + e.getMessage());
            }
        } else if (username != null) {
            System.out.println("Authentication already set in SecurityContext for user: " + username);
        } else {
            System.out.println("No username extracted from token or no token present.");
        }

        System.out.println("JwtAuthFilter - Finalizing request for: " + request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}
