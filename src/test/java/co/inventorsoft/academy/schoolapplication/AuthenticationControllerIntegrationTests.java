package co.inventorsoft.academy.schoolapplication;

import co.inventorsoft.academy.schoolapplication.dto.AuthenticationRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.user.UserResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.user.AccountStatus;
import co.inventorsoft.academy.schoolapplication.entity.user.Role;
import co.inventorsoft.academy.schoolapplication.entity.user.User;
import co.inventorsoft.academy.schoolapplication.service.JwtService;
import co.inventorsoft.academy.schoolapplication.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@FieldDefaults(level = AccessLevel.PRIVATE)
class AuthenticationControllerIntegrationTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtService jwtService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @MockBean
    UserService userService;

    @Test
    void givenExistingUser_WhenLogining_ThenReturnAccessAndRefreshTokens() throws Exception {
        String email = "test@example.com";
        String password = "testPassword";
        Role role = Role.PARENT;

        UserResponseDto testUser = UserResponseDto
                .builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        when(userService.getUserByEmail(any())).thenReturn(testUser);

        AuthenticationRequestDto authenticationRequestDto = AuthenticationRequestDto
                .builder()
                .email(email)
                .password(password)
                .build();

        mockMvc.perform(post("/api/public/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authenticationRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void givenNonExistentUser_WhenLogining_ThenReturnErrorStatus() throws Exception {
        String email = "evil@example.com";
        String password = "evilPassword";

        AuthenticationRequestDto authenticationRequestDto = AuthenticationRequestDto
                .builder()
                .email(email)
                .password(password)
                .build();

        when(userService.getUserByEmail(any())).thenThrow(new UsernameNotFoundException("User not found with login: " + email));

        mockMvc.perform(post("/api/public/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authenticationRequestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenGeneratedRefreshToken_WhenRequestRefreshToken_ThenReturnAccessAndRefreshTokens() throws Exception {
        String email = "test@example.com";
        String password = "testPassword";
        Role role = Role.PARENT;

        UserResponseDto testUserResponseDto = UserResponseDto
                .builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        User testUser = User
                .builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        when(userService.getUserByEmail(any())).thenReturn(testUserResponseDto);

        String refreshToken = jwtService.generateRefreshToken(testUser);

        mockMvc.perform(post("/api/public/auth/refresh-token")
                        .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void givenNotValidRefreshToken_WhenRequestRefreshToken_ThenErrorStatus() throws Exception {
        String refreshToken = "IamEvilToken";

        mockMvc.perform(post("/api/public/auth/refresh-token")
                        .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isUnauthorized());
    }
}
