package com.vision.middleware.domain;

import com.vision.middleware.domain.interfaces.Votable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "reply")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reply implements Votable {
  @Id
  @Column(name = "reply_id", unique = true, updatable = true)
  private long id;

  // Replies can be grouped by a post: the only place for a reply
  // to appear would be a post.
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private Post post;

  // associated with an author: only retrieve when needed.
  // replies must have an associated user when created.
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private ApplicationUser author;

  private Date datePosted;
  private String text;
  private long likeCount;
  private long dislikeCount;

  // replies can have a parent: the reply that they are replying to.
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_reply_id_junction")
  private Reply parentReply;

  // nested replies: a reply can be associated with many replies.
  // at the moment, if a reply is deleted, all child replies will also be too.
  @OneToMany(mappedBy = "parentReply", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Reply> childReplies;
}
