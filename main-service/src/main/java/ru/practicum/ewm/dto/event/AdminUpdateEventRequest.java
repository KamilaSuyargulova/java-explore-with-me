package ru.practicum.ewm.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.ewm.model.StateActionAdmin;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdminUpdateEventRequest extends UpdateEventRequest {
    private StateActionAdmin stateAction;
}