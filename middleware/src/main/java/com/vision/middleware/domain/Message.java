package com.vision.middleware.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "messages")
@Entity
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private long messageID;

    // todo: set up relation between sender and receiver and the ApplicationUser class
    private ApplicationUser sendingUser;
    private ApplicationUser receivingUser;

    private String messageBody;

    private Date dateSent;
}
