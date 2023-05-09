package ru.akpsv.main.subscribe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "subscribes")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Subscribe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscribe_id")
    private Long subscribeId;
    @Column(name = "subscriber_id")
    private Long subscriberId;
    @Column(name = "publisher_id")
    private Long publisherId;
}
