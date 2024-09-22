package com.vision.middleware.domain;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "messages")
@Entity
public class Message {
    @Id
    @GeneratedValue(GenerationType = GenerationType.AUTO)
    @Column(name = "message_id")
    private long messageID;

    // todo: figure out if this is a good idea or not?
    private ApplicationUser sendingUser;
    private ApplicationUser recievingUser;

    private String messageBody;

    private Date dateSent;
}
