package com.vision.middleware.domain.interfaces;

/**
 * Represents an entity that can be voted on, with capabilities to track like and dislike counts.
 */
public interface Votable {
    /**
     * Returns the unique identifier of the votable entity.
     *
     * @return the unique identifier of the entity
     */
    long getId();

    /**
     * Returns the current count of likes for the entity.
     *
     * @return the number of likes
     */
    long getLikeCount();

    /**
     * Sets the count of likes for the entity.
     *
     * @param l the new number of likes
     */
    void setLikeCount(long l);

    /**
     * Returns the current count of dislikes for the entity.
     *
     * @return the number of dislikes
     */
    long getDislikeCount();

    /**
     * Sets the count of dislikes for the entity.
     *
     * @param l the new number of dislikes
     */
    void setDislikeCount(long l);
}
