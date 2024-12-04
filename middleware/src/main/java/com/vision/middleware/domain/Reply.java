package com.vision.middleware.domain;

import com.vision.middleware.domain.baseEntities.VotableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "reply")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "id_generator", sequenceName = "reply_sequence", allocationSize = 1)
public class Reply extends VotableEntity {
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

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date datePosted;

    @Column(nullable = false, length = 5000) // todo: how long do we want replies?
    private String text;

    // replies can have a parent: the reply that they are replying to.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_reply_id_junction")
    private Reply parentReply;

    // nested replies: a reply can be associated with many replies.
    @NonNull
    @OneToMany(mappedBy = "parentReply", cascade = CascadeType.ALL)
    @OrderBy("voteScore DESC, datePosted DESC")
    @Builder.Default
    private Set<Reply> childReplies = new HashSet<>();

    @Column(nullable = false)
    private boolean deleted = false;

    @PrePersist
    protected void onPrePersist() {
        super.onPrePersist();
        this.datePosted = new Date();
    }

    public void addChildReply(Reply reply) {
        childReplies.add(reply);
        reply.setParentReply(this);
    }

    public void removeChildReply(Reply reply) {
        childReplies.remove(reply);
        reply.setParentReply(null);
    }

    public void softDelete() {
        this.deleted = true;
        this.text = "[deleted]";
    }
}
