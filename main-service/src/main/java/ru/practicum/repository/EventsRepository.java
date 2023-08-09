package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Event;

import java.sql.Timestamp;
import java.util.List;

public interface EventsRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByInitiator_id(Long initiatorId, Pageable pageable);

    @Query("select e from Event e where " +
            ":users IS NULL OR e.initiator.id IN (:users) " +
            "AND :states IS NULL OR  e.state IN (:states) " +
            "AND :categories IS NULL OR e.category.id IN (:categories) " +
            "AND (cast(:rangeStart as timestamp) IS NULL OR e.eventDate > :rangeStart) " +
            "AND (cast(:rangeEnd as timestamp) IS NULL OR e.eventDate < :rangeEnd) ")
    List<Event> getEventsByParameters(List<Long> users,
                                      List<String> states,
                                      List<Long> categories,
                                      Timestamp rangeStart,
                                      Timestamp rangeEnd,
                                      Pageable page);

    @Query("select e from Event e where " +
            ":users IS NULL OR e.initiator.id IN (:users) " +
            "AND :states IS NULL OR  e.state IN (:states) " +
            "AND :categories IS NULL OR e.category.id IN (:categories) ")
    List<Event> getEventsByParameters(List<Long> users,
                                      List<String> states,
                                      List<Long> categories,
                                      Pageable page);

    @Query("select e from Event e where " +
            ":text IS NULL OR e.annotation LIKE :text " +
            "OR :text IS NULL OR e.description LIKE :text " +
            "AND :categories IS NULL OR e.category.id IN (:categories) " +
            "AND (cast(:paid as boolean) IS NULL OR e.paid = :paid) " +
            "AND e.state LIKE :published " +
            "AND (cast(:rangeStart as timestamp) IS NULL OR e.eventDate > :rangeStart) " +
            "AND (cast(:rangeEnd as timestamp) IS NULL OR e.eventDate < :rangeEnd) ")
    List<Event> getEventsByParametersForPublic(String text,
                                               List<Long> categories,
                                               Boolean paid,
                                               String published,
                                               Timestamp rangeStart,
                                               Timestamp rangeEnd,
                                               Pageable page);
}
