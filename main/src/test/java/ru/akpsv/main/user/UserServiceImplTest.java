package ru.akpsv.main.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository stubUserRepository;


    @Test
    void deleteById_UserNotExist_ThrowNoSuchElementException() {
        //Подготовка
        UserServiceImpl userService = new UserServiceImpl(stubUserRepository);
        Mockito.when(stubUserRepository.findById(Mockito.anyLong())).thenReturn( Optional.empty());
        String expectedExceptionMassege = "User with id=100 was not found";
        //Действия
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, ()->
                userService.deleteById(100L));
        //Проверка
        assertThat(exception.getMessage(), equalTo(expectedExceptionMassege));
    }

}