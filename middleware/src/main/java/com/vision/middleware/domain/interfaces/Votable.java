package com.vision.middleware.domain.interfaces;

public interface Votable {
    long getId();

    long getLikeCount();
    void setLikeCount(long l);
    long getDislikeCount();
    void setDislikeCount(long l);
}