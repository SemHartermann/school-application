package co.inventorsoft.academy.schoolapplication.service;

import co.inventorsoft.academy.schoolapplication.dto.user.UserRequestDto;
import co.inventorsoft.academy.schoolapplication.dto.user.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    @Transactional(readOnly = true)
    UserResponseDto getUserByEmail(String email);

    @Transactional
    UserResponseDto addUser(UserRequestDto userDto);

    @Transactional(readOnly = true)
    Page<UserResponseDto> getAllUsers(Pageable pageable);

    @Transactional(readOnly = true)
    UserResponseDto getUserById(Long id);

    @Transactional
    UserResponseDto updateUserById(Long id, UserRequestDto userRequestDto);

    @Transactional
    void deleteUserById(Long id);

    @Transactional
    UserResponseDto saveRegisteredUser(UserRequestDto userRequestDto);

    String findVerificationTokenByEmail(String email);
}
