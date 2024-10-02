package com.vision.middleware.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    // note: we shouldn't need any more information than this: will be figured out when making the post.
    private String title;
    private String text;
}
