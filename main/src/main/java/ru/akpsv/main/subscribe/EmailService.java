package ru.akpsv.main.subscribe;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.akpsv.main.event.model.Event;
import ru.akpsv.main.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendEmails(User user, List<Event> events) {
        List<String> groupOfEventTitles = events.stream()
                .map(Event::getTitle)
                .collect(Collectors.toList());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("testewm@yandex.ru");
        message.setTo(user.getEmail());
        message.setSubject("События опубликованные пользователем c именем: " + user.getName());
        message.setText(groupOfEventTitles.toString());
        mailSender.send(message);
    }

    public void sendEmails(List<String> emails, Event event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("testewm@yandex.ru");
        message.setSubject("Опубликовано событие: ");

        try {
            emails.stream()
                    .forEach(email -> {
                        message.setTo(email);
                        message.setText(event.getTitle());
                        mailSender.send(message);
                    });
        } catch (MailException e) {
            log.debug(e.getMessage());
        }

    }
}
