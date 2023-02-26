package ru.akpsv.main.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.akpsv.TestHelper;
import ru.akpsv.main.error.ErrorHandler;
import ru.akpsv.main.user.dto.NewUserRequest;
import ru.akpsv.main.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private UserService stubUserService;
    @InjectMocks
    private UserController userController;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(ErrorHandler.class)
                .build();
    }

    @Test
    void createUser_NewUserRequest_StatusCode201() throws Exception {
        //Подготовка
        String userEmail = "user@email.ru";
        NewUserRequest newUserRequest = TestHelper.createNewUserRequest(userEmail);
        UserDto userDto = TestHelper.createUserDto(1L, userEmail);
        Mockito.when(stubUserService.create(Mockito.any())).thenReturn(Optional.of(userDto));

        //Действия
        //Проверка
        mvc.perform(post("/admin/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isCreated());
    }

    @Test
    void createUser_hibernateConstraintViolationException_StatusCode409() throws Exception {
        //Подготовка
        String userEmail = "user@email.ru";
        NewUserRequest newUserRequest = TestHelper.createNewUserRequest(userEmail);
        Mockito.when(stubUserService.create(Mockito.any())).thenThrow(ConstraintViolationException.class);

        //Действия
        //Проверка
        mvc.perform(post("/admin/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(409));
    }

    @Test
    void createUser_javaxConstraintViolationException_StatusCode400() throws Exception {
        //Подготовка
        String userEmail = "user@email.ru";
        NewUserRequest newUserRequest = TestHelper.createNewUserRequest(userEmail);
        Mockito.when(stubUserService.create(Mockito.any())).thenThrow(javax.validation.ConstraintViolationException.class);

        //Действия
        //Проверка
        mvc.perform(post("/admin/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(409));
    }

    @Test
    void deleteUserById_UserDelete_HttpStatus204() throws Exception {
        //Подготовка
        Mockito.doNothing().when(stubUserService).deleteById(Mockito.anyLong());
        //Действия
        //Проверка
        mvc.perform(delete("/admin/users/{userId}", 1L))
                .andExpect(status().is(204));
    }

    @Test
    void testDeleteUserById() {
    }
}