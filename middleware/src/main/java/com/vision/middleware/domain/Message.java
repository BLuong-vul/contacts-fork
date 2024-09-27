package com.vision.middleware.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "messages")
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private final long messageID;

    // todo: figure out if this is a good idea or not?
    private ApplicationUser sendingUser;
    private ApplicationUser receivingUser;

    private String messageBody;

    private Date dateSent;
}
