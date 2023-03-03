package ru.akpsv.main.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.akpsv.TestHelper;
import ru.akpsv.main.event.dto.EventMapper;
import ru.akpsv.main.user.dto.NewUserRequest;
import ru.akpsv.main.user.dto.UserDto;
import ru.akpsv.main.user.model.User;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository stubUserRepository;

    @InjectMocks
    UserServiceImpl userService;


    @Test
    void create_NewUserRequest_ReturnsUserDto() {
        //Подготовка
        NewUserRequest newUserRequest = TestHelper.createNewUserRequest("user@email.ru");
        User user = TestHelper.createUser(1L, "user@email.ru");
        Mockito.when(stubUserRepository.save(Mockito.any())).thenReturn(user);
        UserDto expectedUserDto = TestHelper.createUserDto(1L, "user@email.ru");
        //Действия
        UserDto actualUserDto = userService.create(newUserRequest);
        //Проверка
        assertThat(actualUserDto, samePropertyValuesAs(expectedUserDto));
    }

    @Test
    void deleteById_UserNotExist_ThrowNoSuchElementException() {
        //Подготовка
        UserServiceImpl userService = new UserServiceImpl(stubUserRepository);
        Mockito.when(stubUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        String expectedExceptionMassege = "User with id=100 was not found";
        //Действия
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                userService.deleteById(100L));
        //Проверка
        assertThat(exception.getMessage(), equalTo(expectedExceptionMassege));
    }
}