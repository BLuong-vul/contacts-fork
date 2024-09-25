package com.vision.middleware.domain;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class ImagePost extends Post{

    @Lob
    @Basic(fetch = FetchType.LAZY) // lazily load, because images can be large.
    private byte[] image;

}
