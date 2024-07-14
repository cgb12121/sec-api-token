package com.backend.vertwo.event;

import com.backend.vertwo.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEvenListener {

    private final EmailService emailService;

    @EventListener
    public void onUserEvent(UserEvent event) {
        switch (event.getType()){

            case REGISTRATION ->emailService
                    .sendNewAccountEmail(
                            event.getUser().getFirstName(),
                            event.getUser().getEmail(),
                            (String) event.getData( ).get("key")
                    );

            case RESET_PASSWORD -> emailService
                    .sendResetPasswordEmail(
                            event.getUser().getFirstName(),
                            event.getUser().getEmail(),
                            (String) event.getData( ).get("key")
                    );

            default -> System.out.println("Something went wrong");
        }
    }
}
