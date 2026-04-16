package ru.practicum.ewm.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.ewm.model.StateActionUser;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserUpdateEventRequest extends UpdateEventRequest {
    private StateActionUser stateAction;
}