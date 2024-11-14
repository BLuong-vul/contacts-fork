package com.vision.middleware.dto;

import com.vision.middleware.domain.relations.UserVote;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VoteDTO {
    private long votableId;
    private UserVote.VoteType voteType;
}
