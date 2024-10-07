package com.vision.middleware.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "messages")
@Entity
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private final long messageID;

    private ApplicationUser sendingUser;
    private ApplicationUser receivingUser;

    private String messageBody;

    private Date dateSent;
}
