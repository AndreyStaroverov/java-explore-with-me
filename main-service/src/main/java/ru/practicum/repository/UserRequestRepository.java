package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.UserRequest;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface UserRequestRepository extends JpaRepository<UserRequest, Long> {

    List<UserRequest> findByRequester_id(Long requesterId);

    @Query("select u from UserRequest u where u.event.id = :eventId AND u.event.initiator.id = :initiatorId")
    List<UserRequest> findByInitiator_idAndEvent_id(Long initiatorId, Long eventId);
}
