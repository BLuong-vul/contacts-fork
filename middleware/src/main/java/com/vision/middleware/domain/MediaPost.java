package com.vision.middleware.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Represents a media post, which is a specific type of post that includes a media file.
 */
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@DiscriminatorValue("MEDIA")
public class MediaPost extends Post {
    /**
     * The name of the media file associated with this media post.
     */
    private String mediaFileName;
}
