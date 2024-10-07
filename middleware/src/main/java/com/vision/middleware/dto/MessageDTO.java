package com.vision.middleware.dto;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MessageDTO {
    private String body;
    private long recipientId;
    private long senderId;
    private Date dateSent;
}
