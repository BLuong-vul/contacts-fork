package com.vision.middleware.dto;

import com.vision.middleware.domain.relations.UserVote;
import lombok.Builder;
import lombok.Getter;

/**
 * Data Transfer Object for representing a vote.
 * It encapsulates the identifier of the votable item and the type of vote.
 */
@Builder
@Getter
public class VoteDTO {
    private long votableId;
    private UserVote.VoteType voteType;
}
