package ru.akpsv.main.subscribe;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ru.akpsv.TestHelper;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.user.model.User;

import javax.mail.internet.MimeMessage;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    @Mock
    JavaMailSender mockMailSender;
    @InjectMocks
    EmailService emailService;

    @Test
    void sendEmails_UserAndEvent_CallEmailSend() {
        //Подготовка
        Event event = TestHelper.createEvent(1L, 1L, 1L);
        User user = TestHelper.createUser(1L, "user1@email.com");

        //Действия
        emailService.sendEmails(user, List.of(event));

        //Проверка
        Mockito.verify(mockMailSender, Mockito.times(1)).send(Mockito.any(SimpleMailMessage.class));

    }

    @Test
    void testSendEmails() {
    }
}
