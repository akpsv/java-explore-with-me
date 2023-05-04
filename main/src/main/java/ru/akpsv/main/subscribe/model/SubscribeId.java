package ru.akpsv.main.subscribe.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@Embeddable
@EqualsAndHashCode
public class SubscribeId implements Serializable {
    @Column(name = "subscriber_id")
    protected Long subscriberId;
    @Column(name = "publisher_id")
    protected Long publisherId;

    SubscribeId(){}

    public SubscribeId(Long subscriberId, Long publisherId) {
        this.subscriberId = subscriberId;
        this.publisherId = publisherId;
    }
}
