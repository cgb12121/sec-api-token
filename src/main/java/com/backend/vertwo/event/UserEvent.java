package com.backend.vertwo.event;

import com.backend.vertwo.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent {
    private User user;
    private EventType type;
    private Map<?, ?> data;
}
