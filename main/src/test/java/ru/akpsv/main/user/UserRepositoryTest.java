package ru.akpsv.main.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.akpsv.TestHelper;
import ru.akpsv.main.user.dto.NewUserRequest;
import ru.akpsv.main.user.dto.UserMapper;
import ru.akpsv.main.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    void save_UserWithoutId_ReturnsUserWitnId() {
        //Подготовка
        String userEmail = "user@email.ru";
        User userWithoutId = TestHelper.createUser(0L, userEmail);
        //Проверка
        Long actualUserId = userRepository.save(userWithoutId).getId();
        //Действия
        assertThat(actualUserId, not(0L));
    }

    @Test
    void getUsersByIds_ArrayOfIds_ReturnsCorrectQuantityOfUsers() {
        //Подготовка
        NewUserRequest newUserRequest1 = TestHelper.createNewUserRequest("user1@email.ru");
        NewUserRequest newUserRequest2 = TestHelper.createNewUserRequest("user2@email.ru");
        User savedUser1 = userRepository.save(UserMapper.toUser(newUserRequest1));
        User savedUser2 = userRepository.save(UserMapper.toUser(newUserRequest2));
        List<Long> ids = List.of(savedUser1.getId(), savedUser2.getId());
        Integer expectedNumberOfUsers = 2;
        //Действия
        List<User> usersByIds = userRepository.getUsersByIds(em, ids, 0, 10);
        Integer actualNumberOfUsers = usersByIds.size();
        //Проверка
        assertThat(actualNumberOfUsers, equalTo(expectedNumberOfUsers));
    }
}