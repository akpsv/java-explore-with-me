package ru.akpsv.main.subscribe.model;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@Table(name = "subscribes")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Subscribe {
    @EmbeddedId
    private SubscribeId id;
}
