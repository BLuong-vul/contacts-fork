package com.vision.middleware.domain;

import com.vision.middleware.domain.baseentities.VotableEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a reply in the application, which is a response to a post.
 * Each reply is associated with a post and an author, and can have parent and child replies.
 */
@Entity
@Table(name = "reply")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "id_generator", sequenceName = "reply_sequence", allocationSize = 1)
public class Reply extends VotableEntity {
    /**
     * The post to which this reply belongs.
     * A reply can only be associated with one post.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * The author of this reply.
     * A reply must have an associated author when created.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private ApplicationUser author;

    /**
     * The date when this reply was posted.
     * This field is automatically set to the current date and time when the reply is persisted.
     */
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date datePosted;

    /**
     * The text content of this reply.
     * This field has a maximum length of 5000 characters.
     */
    @Column(nullable = false, length = 5000)
    private String text;

    /**
     * The parent reply to which this reply is a response.
     * This can be null if the reply is a top-level reply to a post.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_reply_id_junction")
    private Reply parentReply;

    /**
     * The child replies that are responses to this reply.
     * This set is ordered by vote score in descending order, and then by date posted in descending order.
     */
    @NonNull
    @OneToMany(mappedBy = "parentReply", cascade = CascadeType.ALL)
    @OrderBy("voteScore DESC, datePosted DESC")
    @Builder.Default
    private Set<Reply> childReplies = new HashSet<>();

    /**
     * Indicates whether this reply has been soft-deleted.
     * When true, the reply's text is replaced with "[deleted]".
     */
    @Column(nullable = false)
    private boolean deleted = false;

    /**
     * Sets the datePosted field to the current date and time before the(reply is persisted.
     * Calls the superclass's onPrePersist method to ensure proper initialization.
     */
    @PrePersist
    protected void onPrePersist() {
        super.onPrePersist();
        this.datePosted = new Date();
    }

    /**
     * Adds a child reply to this reply.
     * Sets the parent reply of the given reply to this reply.
     *
     * @param reply the child reply to be added
     */
    public void addChildReply(Reply reply) {
        childReplies.add(reply);
        reply.setParentReply(this);
    }

    /**
     * Removes a child reply from this reply.
     * Sets the parent reply of the given reply to null.
     *
     * @param reply the child reply to be removed
     */
    public void removeChildReply(Reply reply) {
        childReplies.remove(reply);
        reply.setParentReply(null);
    }

    /**
     * Marks this reply as deleted by setting the deleted flag to true and replacing the text with "[deleted]".
     */
    public void softDelete() {
        this.deleted = true;
        this.text = "[deleted]";
    }
}
