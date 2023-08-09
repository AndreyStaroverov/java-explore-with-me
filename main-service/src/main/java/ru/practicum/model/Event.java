package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Positive;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "events", schema = "public")
public class Event {

    @Positive
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "annotation", nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "conf_req", nullable = false)
    private Long conf_req;
    @Column(name = "created", nullable = false)
    private Timestamp createdOn;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "event_date", nullable = false)
    private Timestamp event_date;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
    @Column(name = "paid", nullable = false)
    private Boolean paid;
    @Column(name = "part_limit", nullable = false)
    private Long part_limit;
    @Column(name = "published", nullable = false)
    private Timestamp publishedOn;
    @Column(name = "req_moderation", nullable = false)
    private Boolean requestModeration;
    @Column(name = "state", nullable = false)
    private String state;
    @Column(name = "title", nullable = false)
    private String title;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "compilations_events",
            joinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "compilation_id", referencedColumnName = "id"))
    private List<Compilation> compilations;
}
