package com.vision.middleware.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "reply")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reply {
  @Id
  @Column(name = "reply_id", unique = true, updatable = true)
  private int replyId;
  private int postId;
  private LocalDate datePosted;
  private String text;
  private int userId;
  private Integer parentReplyId;
}
