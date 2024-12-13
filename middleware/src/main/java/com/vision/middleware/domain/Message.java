package com.vision.middleware.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * Represents a message entity in the system.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "messages")
@Entity
@Builder
public class Message {
    /**
     * Unique identifier for the message.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private long messageID;

    /**
     * Identifier for the user who sent the message.
     */
    private long sendingUserId;

    /**
     * Identifier for the user who will receive the message.
     */
    private long receivingUserId;

    /**
     * The content of the message.
     */
    private String messageBody;

    /**
     * The date and time when the message was sent.
     */
    private Date dateSent;
}
