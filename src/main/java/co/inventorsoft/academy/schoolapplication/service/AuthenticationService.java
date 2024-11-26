package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.AuthenticationRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.AuthenticationResponseDto;
import co.inventorsoft.academy.schoolapplication.dto.RefreshTokenRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.RefreshTokenResponseDto;
import co.inventorsoft.academy.schoolapplication.dto.user.UserResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.user.AccountStatus;
import co.inventorsoft.academy.schoolapplication.entity.user.User;
import co.inventorsoft.academy.schoolapplication.exception.UserIsNotActiveException;
import co.inventorsoft.academy.schoolapplication.mapper.UserMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    JwtService jwtService;

    PasswordEncoder passwordEncoder;

    UserService userService;

    UserMapper userMapper = UserMapper.MAPPER;

    public void authenticate(UserDetails user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities());

        authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequestDto) {
        String authHeader = refreshTokenRequestDto.getRefreshToken();

        RefreshTokenResponseDto refreshTokenResponseDto = new RefreshTokenResponseDto();

        if (StringUtils.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Missing refresh token");
        }

        String refreshToken = authHeader.substring(7);

        if (jwtService.extractType(refreshToken).equals("accessToken")) {
            throw new JwtException("Access token cannot be used for refreshing token");
        }

        UserResponseDto userResponseDto = userService.getUserByEmail(jwtService.extractEmail(refreshToken));
        User user = userMapper.userResponseDtoToUser(userResponseDto);

        if (user.getAccountStatus().equals(AccountStatus.ACTIVE)) {
            refreshTokenResponseDto.setAccessToken(jwtService.generateAccessToken(user));
            refreshTokenResponseDto.setRefreshToken(jwtService.generateRefreshToken(user));

            return refreshTokenResponseDto;
        } else {
            throw new UserIsNotActiveException("User account is not valid");
        }
    }

    public AuthenticationResponseDto login(AuthenticationRequestDto authenticationRequestDto) {
        UserResponseDto userResponseDto = userService.getUserByEmail(authenticationRequestDto.getEmail());
        User user = userMapper.userResponseDtoToUser(userResponseDto);

        if (user.getAccountStatus().equals(AccountStatus.ACTIVE)) {
            AuthenticationResponseDto authenticationResponseDto = new AuthenticationResponseDto();

            if (passwordEncoder.matches(authenticationRequestDto.getPassword(), user.getPassword())) {
                authenticationResponseDto.setAccessToken(jwtService.generateAccessToken(user));
                authenticationResponseDto.setRefreshToken(jwtService.generateRefreshToken(user));

                return authenticationResponseDto;
            } else {
                throw new BadCredentialsException("Wrong password");
            }
        } else {
            throw new UserIsNotActiveException("User account is not valid");
        }
    }
}