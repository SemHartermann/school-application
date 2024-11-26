package co.inventorsoft.academy.schoolapplication.controller;

import co.inventorsoft.academy.schoolapplication.dto.AuthenticationRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.AuthenticationResponseDto;
import co.inventorsoft.academy.schoolapplication.dto.RefreshTokenRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.RefreshTokenResponseDto;
import co.inventorsoft.academy.schoolapplication.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(
            @RequestBody AuthenticationRequestDto authenticationRequestDto
    ) {
        AuthenticationResponseDto authenticationResponseDto = authenticationService.login(authenticationRequestDto);

        return ResponseEntity
                .ok()
                .body(authenticationResponseDto);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponseDto> refreshToken(
            HttpServletRequest request
    ) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto(authHeader);

        RefreshTokenResponseDto refreshTokenResponseDto = authenticationService.refreshToken(refreshTokenRequestDto);

        return ResponseEntity
                .ok()
                .body(refreshTokenResponseDto);
    }
}
