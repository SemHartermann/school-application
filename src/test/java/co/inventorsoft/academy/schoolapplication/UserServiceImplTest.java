package co.inventorsoft.academy.schoolapplication;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import co.inventorsoft.academy.schoolapplication.dto.user.UserRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.user.UserResponseDto;
import co.inventorsoft.academy.schoolapplication.entity.user.AccountStatus;
import co.inventorsoft.academy.schoolapplication.entity.user.Role;
import co.inventorsoft.academy.schoolapplication.entity.user.User;
import co.inventorsoft.academy.schoolapplication.mapper.UserMapper;
import co.inventorsoft.academy.schoolapplication.repository.UserRepository;
import co.inventorsoft.academy.schoolapplication.service.impl.UserServiceImpl;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserMapper userMapper;

    @Test
    void whenSaveRegisteredUser_thenSuccess() {
        UserRequestDto requestDto = new UserRequestDto(null, "email@example.com", "Password123", Role.STUDENT);
        User user = User.builder()
                .email("email@example.com")
                .password("encodedPassword")
                .role(Role.STUDENT)
                .accountStatus(AccountStatus.DISABLED)
                .build();
        UserResponseDto responseDto = new UserResponseDto(null, "email@example.com", Role.STUDENT, AccountStatus.DISABLED);

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.userToUserResponseDto(any(User.class))).thenReturn(responseDto);

        UserResponseDto result = userService.saveRegisteredUser(requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("Password123");
    }

    @Test
    void whenEmailAlreadyExistsInSaveRegisteredUser_thenThrowException() {
        UserRequestDto requestDto = new UserRequestDto(null, "email@example.com", "Password123", Role.STUDENT);

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.saveRegisteredUser(requestDto);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void whenAddUser_thenSuccess() {
        UserRequestDto requestDto = new UserRequestDto(null, "email@example.com", "Password123", Role.STUDENT);
        User user = new User();
        UserResponseDto responseDto = new UserResponseDto(1L, "email@example.com", Role.STUDENT, AccountStatus.DISABLED);

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(requestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.userToUserResponseDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.addUser(requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(userRepository).save(user);
    }

    @Test
    void whenEmailAlreadyExistsInAddUser_thenThrowException() {
        UserRequestDto requestDto = new UserRequestDto(null, "email@example.com", "Password123", Role.STUDENT);

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.addUser(requestDto));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void whenGetUserByEmail_thenSuccess() {
        String email = "email@example.com";
        User user = User.builder().email(email).build();
        UserResponseDto responseDto = new UserResponseDto(1L, email, Role.STUDENT, AccountStatus.DISABLED);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.userToUserResponseDto(user)).thenReturn(responseDto);

        UserResponseDto result = userService.getUserByEmail(email);

        assertThat(result).isEqualTo(responseDto);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void whenGetUserByEmail_UserNotFound_thenThrowException() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByEmail(email));

        verify(userRepository).findByEmail(email);
    }
    @Test
    void whenGetUserById_thenSuccess() {
        // Arrange
        Long id = 1L;
        User user = new User();
        UserResponseDto responseDto = new UserResponseDto(1L, "email@example.com", Role.STUDENT, AccountStatus.DISABLED);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.userToUserResponseDto(user)).thenReturn(responseDto);

        // Act
        UserResponseDto result = userService.getUserById(id);

        // Assert
        assertThat(result).isEqualTo(responseDto);
        verify(userRepository).findById(id);
        verify(userMapper).userToUserResponseDto(user);
    }

    @Test
    void whenGetUserById_UserNotFound_thenThrowException() {
        // Arrange
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserById(id));

        verify(userRepository).findById(id);
        verify(userMapper, never()).userToUserResponseDto(any(User.class));
    }

    @Test
    void whenUpdateUserById_thenSuccess() {
        // Arrange
        Long id = 1L;
        UserRequestDto requestDto = new UserRequestDto(id, "updatedEmail@example.com", "UpdatedPassword123", Role.TEACHER);

        User user = new User();
        user.setId(id);
        user.setEmail("oldEmail@example.com");
        user.setPassword("OldPassword123");
        user.setRole(Role.STUDENT);
        user.setAccountStatus(AccountStatus.DISABLED);

        UserResponseDto responseDto = new UserResponseDto(id, "updatedEmail@example.com", Role.TEACHER, AccountStatus.DISABLED);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUserFromDto(requestDto, user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.userToUserResponseDto(user)).thenReturn(responseDto);

        // Act
        UserResponseDto result = userService.updateUserById(id, requestDto);

        // Assert
        assertThat(result).isEqualTo(responseDto);
        verify(userRepository).findById(id);
        verify(userMapper).updateUserFromDto(requestDto, user);
        verify(userRepository).save(user);
        verify(userMapper).userToUserResponseDto(user);
    }

    @Test
    void whenUpdateUserById_UserNotFound_thenThrowException() {
        // Arrange
        Long id = 1L;
        UserRequestDto requestDto = new UserRequestDto(id, "updatedEmail@example.com", "UpdatedPassword123", Role.TEACHER);

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.updateUserById(id, requestDto));

        verify(userRepository).findById(id);
        verify(userMapper, never()).updateUserFromDto(any(UserRequestDto.class), any(User.class));
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).userToUserResponseDto(any(User.class));
    }


    @Test
    void whenDeleteUserById_thenRepositoryDeleteIsCalled() {
        // Arrange
        Long id = 1L;

        // Act
        userService.deleteUserById(id);

        // Assert
        verify(userRepository).deleteById(id);
    }

    @Test
    void whenGetAllUsers_thenSuccess() {
        // Arrange
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        List<User> users = Arrays.asList(
                new User("user1@example.com", "password1", Role.STUDENT, AccountStatus.ACTIVE, null, null),
                new User("user2@example.com", "password2", Role.TEACHER, AccountStatus.DISABLED, null, null)
        );
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.userToUserResponseDto(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    return new UserResponseDto(
                            user.getId(),
                            user.getEmail(),
                            user.getRole(),
                            user.getAccountStatus()
                    );
                });

        // Act
        Page<UserResponseDto> result = userService.getAllUsers(pageable);

        // Assert
        assertThat(result.getContent().size()).isEqualTo(users.size());
        for (int i = 0; i < users.size(); i++) {
            User expectedUser = users.get(i);
            UserResponseDto actualDto = result.getContent().get(i);
            assertThat(actualDto.getId()).isEqualTo(expectedUser.getId());
            assertThat(actualDto.getEmail()).isEqualTo(expectedUser.getEmail());
            assertThat(actualDto.getRole()).isEqualTo(expectedUser.getRole());
            assertThat(actualDto.getAccountStatus()).isEqualTo(expectedUser.getAccountStatus());
        }
        assertThat(result.getNumber()).isEqualTo(page); // Check the page number
        assertThat(result.getSize()).isEqualTo(size); // Check the page size
        assertThat(result.getTotalElements()).isEqualTo(users.size()); // Check the total elements
        verify(userRepository).findAll(pageable);
        verify(userMapper, times(users.size())).userToUserResponseDto(any(User.class));
    }
}