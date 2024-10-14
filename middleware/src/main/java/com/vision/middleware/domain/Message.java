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

    @ManyToOne
    @JoinColumn(name = "sending_user_id")
    private ApplicationUser sendingUser;

    @ManyToOne
    @JoinColumn(name = "receiving_user_id")
    private ApplicationUser receivingUser;

    private String messageBody;

    private Date dateSent;
}
