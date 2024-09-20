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
@Table(name = "text_post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {
  @Id
  @Column(name = "post_id", unique = true, updatable = true)
  private int postId;
  private int userId;
  private int likeCount;
  private int dislikeCount;
  private LocalDate datePosted;
  private String title;
  private String text;
}
