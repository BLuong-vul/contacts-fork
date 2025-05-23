package com.vision.middleware.dto;

import com.vision.middleware.domain.relations.UserVote;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.Reply;

import com.vision.middleware.dto.UserDTO;

/**
 * Data Transfer Object representing a reply in the system.
 * It includes details such as the reply's text, author, date of posting,
 * vote counts, vote score, user vote type, deletion status, and nested replies.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReplyDTO {
    private Long id;
    private String text;
    private UserDTO author;
    private Date datePosted;
    private long likeCount;
    private long dislikeCount;
    private long voteScore;
    private UserVote.VoteType userVoteType;
    private boolean isDeleted;
    private List<ReplyDTO> replies;
}
