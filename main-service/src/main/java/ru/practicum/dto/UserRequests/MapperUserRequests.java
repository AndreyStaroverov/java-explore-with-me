package ru.practicum.dto.UserRequests;

import ru.practicum.dto.events.ParticipationRequestDto;
import ru.practicum.model.UserRequest;

import java.util.ArrayList;
import java.util.Collection;

public final class MapperUserRequests {

    private MapperUserRequests() {
    }

    public static Collection<UserRequestDto> collectionUserRequestToDto(Collection<UserRequest> userRequests) {
        Collection<UserRequestDto> userRequestDtos = new ArrayList<>();
        for (UserRequest userRequest : userRequests) {
            userRequestDtos.add(new UserRequestDto(
                    userRequest.getCreated(),
                    userRequest.getEvent().getId(),
                    userRequest.getId(),
                    userRequest.getRequester().getId(),
                    userRequest.getStatus()
            ));
        }
        return userRequestDtos;
    }

    public static ParticipationRequestDto toPartReqDto(UserRequest userRequest) {
        return ParticipationRequestDto.builder()
                .id(userRequest.getId())
                .created(userRequest.getCreated())
                .event(userRequest.getEvent().getId())
                .requester(userRequest.getRequester().getId())
                .status(userRequest.getStatus())
                .build();
    }

    public static Collection<ParticipationRequestDto> toPartReqDtoColl(Collection<UserRequest> userRequests) {
        Collection<ParticipationRequestDto> userRequestDtos = new ArrayList<>();
        for (UserRequest userRequest : userRequests) {
            userRequestDtos.add(ParticipationRequestDto.builder()
                    .id(userRequest.getId())
                    .created(userRequest.getCreated())
                    .event(userRequest.getEvent().getId())
                    .requester(userRequest.getRequester().getId())
                    .status(userRequest.getStatus())
                    .build());
        }
        return userRequestDtos;
    }
}
