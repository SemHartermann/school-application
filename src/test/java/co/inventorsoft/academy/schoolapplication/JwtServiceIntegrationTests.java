package co.inventorsoft.academy.schoolapplication;

import co.inventorsoft.academy.schoolapplication.entity.user.AccountStatus;
import co.inventorsoft.academy.schoolapplication.entity.user.Role;
import co.inventorsoft.academy.schoolapplication.entity.user.User;
import co.inventorsoft.academy.schoolapplication.service.JwtService;
import co.inventorsoft.academy.schoolapplication.util.JwtProperties;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class JwtServiceIntegrationTests {
    @MockBean
    JwtProperties jwtProperties;

    @Autowired
    JwtService jwtService;

    @Test
    void givenUser_WhenGeneratingAccessTokenAndExtractingData_ThenReturnGivenData() {
        User user = User
                .builder()
                .email("test@example.com")
                .role(Role.PARENT)
                .build();

        when(jwtProperties.getAccessTokenExpiration()).thenReturn(1000000L);
        when(jwtProperties.getSecretKey()).thenReturn("koxelJPNOmdGTDaHkqL1D+vAhA9PXiddCi00q1UDEL0TEoB3RxlKQ1p0bPX9esoJjNWqL6yoPwjjBhbqPOpRXTZl4ceDypJaEsjVFJgp+LE=");

        String accessToken = jwtService.generateAccessToken(user);
        Role extractedRole = jwtService.extractRole(accessToken);
        String extractedEmail = jwtService.extractEmail(accessToken);
        String extractedType = jwtService.extractType(accessToken);

        assertThat(accessToken).isNotNull();
        assertThat(extractedRole).isEqualTo(user.getRole());
        assertThat(extractedEmail).isEqualTo(user.getUsername());
        assertThat(extractedType).isEqualTo("accessToken");
    }

    @Test
    void givenUser_WhenGeneratingRefreshTokenAndExtractingData_ThenReturnGivenData() {
        User user = User
                .builder()
                .email("test@example.com")
                .role(Role.PARENT)
                .build();

        when(jwtProperties.getRefreshTokenExpiration()).thenReturn(1000000L);
        when(jwtProperties.getSecretKey()).thenReturn("koxelJPNOmdGTDaHkqL1D+vAhA9PXiddCi00q1UDEL0TEoB3RxlKQ1p0bPX9esoJjNWqL6yoPwjjBhbqPOpRXTZl4ceDypJaEsjVFJgp+LE=");

        String refreshToken = jwtService.generateRefreshToken(user);
        Role extractedRole = jwtService.extractRole(refreshToken);
        String extractedEmail = jwtService.extractEmail(refreshToken);
        String extractedType = jwtService.extractType(refreshToken);

        assertThat(refreshToken).isNotNull();
        assertThat(extractedRole).isEqualTo(user.getRole());
        assertThat(extractedEmail).isEqualTo(user.getUsername());
        assertThat(extractedType).isEqualTo("refreshToken");
    }

    @Test
    void givenExpiredAccessToken_WhenExtractingData_ThenThrowExpiredJwtException() {
        User user = User
                .builder()
                .email("test@example.com")
                .role(Role.PARENT)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        when(jwtProperties.getAccessTokenExpiration()).thenReturn(0L);
        when(jwtProperties.getSecretKey()).thenReturn("koxelJPNOmdGTDaHkqL1D+vAhA9PXiddCi00q1UDEL0TEoB3RxlKQ1p0bPX9esoJjNWqL6yoPwjjBhbqPOpRXTZl4ceDypJaEsjVFJgp+LE=");

        String accessToken = jwtService.generateAccessToken(user);

        assertThatThrownBy(() -> jwtService.extractRole(accessToken)).isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void givenExpiredRefreshToken_WhenExtractingData_ThenThrowExpiredJwtException() {
        User user = User
                .builder()
                .email("test@example.com")
                .role(Role.PARENT)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        when(jwtProperties.getRefreshTokenExpiration()).thenReturn(0L);
        when(jwtProperties.getSecretKey()).thenReturn("koxelJPNOmdGTDaHkqL1D+vAhA9PXiddCi00q1UDEL0TEoB3RxlKQ1p0bPX9esoJjNWqL6yoPwjjBhbqPOpRXTZl4ceDypJaEsjVFJgp+LE=");

        String refreshToken = jwtService.generateRefreshToken(user);

        assertThatThrownBy(() -> jwtService.extractRole(refreshToken)).isInstanceOf(ExpiredJwtException.class);
    }
}