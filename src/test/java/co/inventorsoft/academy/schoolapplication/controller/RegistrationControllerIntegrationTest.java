package co.inventorsoft.academy.schoolapplication.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.inventorsoft.academy.schoolapplication.dto.UserEmailDto;
import co.inventorsoft.academy.schoolapplication.dto.user.RegistrationDto;
import co.inventorsoft.academy.schoolapplication.entity.Parent;
import co.inventorsoft.academy.schoolapplication.entity.Student;
import co.inventorsoft.academy.schoolapplication.entity.Teacher;
import co.inventorsoft.academy.schoolapplication.entity.user.Role;
import co.inventorsoft.academy.schoolapplication.repository.ParentRepository;
import co.inventorsoft.academy.schoolapplication.repository.StudentRepository;
import co.inventorsoft.academy.schoolapplication.repository.TeacherRepository;
import co.inventorsoft.academy.schoolapplication.repository.UserRepository;
import co.inventorsoft.academy.schoolapplication.service.UserService;
import co.inventorsoft.academy.schoolapplication.service.VerificationTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class RegistrationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ParentRepository parentRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    VerificationTokenService verificationTokenService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        parentRepository.deleteAllInBatch();
        teacherRepository.deleteAllInBatch();
        studentRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        Parent parent = new Parent();
        parent.setEmail("kirfurs31@gmail.com");
        parent.setFirstName("Kyrylo");
        parent.setLastName("F");

        Student student = new Student();
        student.setDeleted(false);
        student.setPhone("+380332221");
        student.setEmail("vova2003@ukr.net");
        student.setFirstName("Volodymyr");
        student.setLastName("Havryliuk");

        Teacher teacher = new Teacher();
        teacher.setEmail("teacher@ukr.net");
        teacher.setFirstName("Teacher");
        teacher.setLastName("Test");

        parentRepository.save(parent);
        studentRepository.save(student);
        teacherRepository.save(teacher);
    }

    @Test
    void whenRegisterParent_thenEmailSentAndUserSaved() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto(Role.PARENT, "kirfurs31@gmail.com", "12345678gG");

        mockMvc.perform(post("/api/public/registration/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("kirfurs31@gmail.com"))
                .andExpect(jsonPath("$.role").value("PARENT"))
                .andExpect(jsonPath("$.accountStatus").value("DISABLED"));

        mockMvc.perform(post("/api/public/registration/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email is already in use")));
    }

    @Test
    void whenRegisterParentWithNonExistentEmail_thenError() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto(Role.PARENT, "nonexistentemail@test.com", "12345678gG");

        mockMvc.perform(post("/api/public/registration/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenRegisterStudent_thenEmailSentAndUserSaved() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto(Role.STUDENT, "vova2003@ukr.net", "12344321Ga");

        mockMvc.perform(post("/api/public/registration/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("vova2003@ukr.net"))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.accountStatus").value("DISABLED"));

        mockMvc.perform(post("/api/public/registration/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email is already in use")));
    }

    @Test
    void whenRegisterStudentWithNonExistentEmail_thenError() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto(Role.STUDENT, "maksym2002@gmail.com", "54321gMMa");

        mockMvc.perform(post("/api/public/registration/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenRegisterTeacher_thenEmailSentAndUserSaved() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto(Role.TEACHER, "teacher@ukr.net", "543fsd21gMa");

        mockMvc.perform(post("/api/public/registration/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("teacher@ukr.net"))
                .andExpect(jsonPath("$.role").value("TEACHER"))
                .andExpect(jsonPath("$.accountStatus").value("DISABLED"));

        mockMvc.perform(post("/api/public/registration/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email is already in use")));
    }

    @Test
    void whenRegisterTeacherWithNonExistentEmail_thenError() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto(Role.TEACHER, "not-teacher@gmail.com", "5432321gma");

        mockMvc.perform(post("/api/public/registration/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenEmailIsMissing_thenBadRequest() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto(Role.PARENT, null, "12345678gG");

        mockMvc.perform(post("/api/public/registration/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Validation failed for: email - Email is mandatory")));
    }

    @Test
    void whenEmailIsInvalid_thenBadRequest() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto(Role.PARENT, "kirfursgmail.com", "12345678gG");

        mockMvc.perform(post("/api/public/registration/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Validation failed for: email - Email should be valid")));
    }

    @Test
    void whenPasswordIsInvalid_thenBadRequest() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto(Role.PARENT, "kirfurs31@gmail.com", "1234");

        mockMvc.perform(post("/api/public/registration/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(
                        "Validation failed for: password - Password must contain at least 8 characters, one uppercase, one lowercase, and one number; ")));
    }

    @Test
    void whenPasswordIsMissing_thenBadRequest() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto(Role.PARENT, "kirfurs31@gmail.com", null);

        mockMvc.perform(post("/api/public/registration/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Validation failed for: password - Password is mandatory")));
    }

    @Test
    void whenNoRegistrationStrategyForRole_thenBadRequest() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto(Role.SUPER_ADMIN, "kirfurs31@gmail.com", "12345678gG");

        mockMvc.perform(post("/api/public/registration/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(
                        "No registration strategy found for role: SUPER_ADMIN")));
    }

    @Test
    void whenResendEmailToNonExistentUser_thenNotFound() throws Exception {
        UserEmailDto emailDto = new UserEmailDto("nonexistentemail@test.com");

        mockMvc.perform(post("/api/public/registration/resend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User with given email does not exist")));
    }

    @Test
    void whenResendEmail_thenTokenRefreshed() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto(Role.PARENT, "kirfurs31@gmail.com", "12345678gG");

        mockMvc.perform(post("/api/public/registration/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(jsonPath("$.email").value("kirfurs31@gmail.com"))
                .andExpect(jsonPath("$.role").value("PARENT"))
                .andExpect(jsonPath("$.accountStatus").value("DISABLED"));


        mockMvc.perform(post("/api/public/registration/resend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserEmailDto(registrationDto.getEmail()))))
                .andExpect(status().isOk());
    }

    @Test
    void whenConfirmWithValidToken_thenSuccess() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto(Role.PARENT, "kirfurs31@gmail.com", "12345678gG");

        mockMvc.perform(post("/api/public/registration/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("kirfurs31@gmail.com"))
                .andExpect(jsonPath("$.role").value("PARENT"))
                .andExpect(jsonPath("$.accountStatus").value("DISABLED"));

        String validToken = userService.findVerificationTokenByEmail(registrationDto.getEmail());

        mockMvc.perform(get("/api/public/registration/confirmation")
                        .param("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(containsString("Your account has been confirmed")));
    }

    @Test
    void whenConfirmWithInvalidToken_thenBadRequest() throws Exception {
        String invalidToken = "invalid-token";

        mockMvc.perform(get("/api/public/registration/confirmation")
                        .param("token", invalidToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("The provided token is invalid.")));
    }
}
