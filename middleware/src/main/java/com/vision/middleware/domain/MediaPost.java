package com.vision.middleware.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
@DiscriminatorValue("MEDIA")
public class MediaPost extends Post{
    private String mediaFileName;
}
