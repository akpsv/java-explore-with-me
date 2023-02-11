package ru.akpsv.main.compilation;

import ru.akpsv.main.event.model.Event;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id")
    private long id;
    @Column(name = "title")
    private String title;
    @Column(name = "pinned")
    private boolean pinned;
    //TODO: доделать в соовтетствии с заданием возможно многие ко многим
    private List<Event> events;
}
