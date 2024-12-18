package co.inventorsoft.academy.schoolapplication.filter;

import co.inventorsoft.academy.schoolapplication.entity.user.User;
import co.inventorsoft.academy.schoolapplication.service.AuthenticationService;
import co.inventorsoft.academy.schoolapplication.service.JwtService;
import co.inventorsoft.academy.schoolapplication.util.EndpointMaster;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    JwtService jwtService;

    AuthenticationService authenticationService;

    EndpointMaster endpointMaster;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return endpointMaster.isEndpointMatchedWithPattern(request, "/api/public/**");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (endpointMaster.isHandlerNotExist(request)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (StringUtils.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing auth token");

            return;
        }

        String jwt = authHeader.split(" ")[1].trim();

        try {
            if (jwtService.extractType(jwt).equals("refreshToken")) {
                throw new JwtException("Refresh token cannot be used for authentication");
            }

            UserDetails userDetails = User.builder()
                    .id(jwtService.extractId(jwt))
                    .email(jwtService.extractEmail(jwt))
                    .role(jwtService.extractRole(jwt))
                    .build();

            authenticationService.authenticate(userDetails, request);
        } catch (JwtException ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid access token");

            return;
        }

        filterChain.doFilter(request, response);
    }
}
