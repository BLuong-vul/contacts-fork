package com.vision.testing.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.Reply;
import com.vision.middleware.domain.enums.VotableType;
import com.vision.middleware.domain.relations.UserVote;
import com.vision.middleware.exceptions.DuplicateVoteException;
import com.vision.middleware.repo.PostRepository;
import com.vision.middleware.repo.ReplyRepository;
import com.vision.middleware.repo.UserVoteRepository;
import com.vision.middleware.service.VotingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VotingServiceTest {

    @Mock
    private UserVoteRepository userVoteRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ReplyRepository replyRepository;

    @InjectMocks
    private VotingService votingService;

    private ApplicationUser testUser;
    private Post testPost;
    private Reply testReply;

    @BeforeEach
    void setUp() {
        testUser = new ApplicationUser();
        testUser.setId(1L);

        testPost = new Post();
        testPost.setId(1L);
        testPost.setLikeCount(0);
        testPost.setDislikeCount(0);

        testReply = new Reply();
        testReply.setId(2L);
        testReply.setLikeCount(0);
        testReply.setDislikeCount(0);
    }

    @Test
    void voteOnPost_FirstLike_ShouldIncreasePostLikeCount() {
        // Arrange
        when(userVoteRepository.findByUserAndVotableAndVotableType(testUser, testPost, VotableType.POST))
                .thenReturn(Optional.empty());

        // Act
        votingService.voteOnVotable(testUser, testPost, UserVote.VoteType.LIKE);

        // Assert
        verify(postRepository).save(testPost);
        assertThat(testPost.getLikeCount()).isEqualTo(1);
        verify(userVoteRepository).save(any(UserVote.class));
    }

    @Test
    void voteOnReply_FirstDislike_ShouldIncreaseReplyDislikeCount() {
        // Arrange
        when(userVoteRepository.findByUserAndVotableAndVotableType(testUser, testReply, VotableType.REPLY))
                .thenReturn(Optional.empty());

        // Act
        votingService.voteOnVotable(testUser, testReply, UserVote.VoteType.DISLIKE);

        // Assert
        verify(replyRepository).save(testReply);
        assertThat(testReply.getDislikeCount()).isEqualTo(1);
        verify(userVoteRepository).save(any(UserVote.class));
    }

    @Test
    void changeVote_FromLikeToDislike_ShouldUpdateCounts() {
        // Arrange
        UserVote existingVote = UserVote.builder()
                .user(testUser)
                .votable(testPost)
                .votableType(VotableType.POST)
                .voteType(UserVote.VoteType.LIKE)
                .build();
        testPost.setLikeCount(1L); // user liked post already


        when(userVoteRepository.findByUserAndVotableAndVotableType(testUser, testPost, VotableType.POST))
                .thenReturn(Optional.of(existingVote));

        // Act
        votingService.voteOnVotable(testUser, testPost, UserVote.VoteType.DISLIKE);

        // Assert
        verify(postRepository).save(testPost);
        assertThat(testPost.getLikeCount()).isEqualTo(0);
        assertThat(testPost.getDislikeCount()).isEqualTo(1);
        verify(userVoteRepository).save(existingVote);
    }

    @Test
    void changeVote_FromDislikeToLike_ShouldUpdateCounts() {
        // Arrange
        UserVote existingVote = UserVote.builder()
                .user(testUser)
                .votable(testPost)
                .votableType(VotableType.POST)
                .voteType(UserVote.VoteType.DISLIKE)
                .build();
        testPost.setDislikeCount(1L); // user disliked post already


        when(userVoteRepository.findByUserAndVotableAndVotableType(testUser, testPost, VotableType.POST))
                .thenReturn(Optional.of(existingVote));

        // Act
        votingService.voteOnVotable(testUser, testPost, UserVote.VoteType.LIKE);

        // Assert
        verify(postRepository).save(testPost);
        assertThat(testPost.getLikeCount()).isEqualTo(1);
        assertThat(testPost.getDislikeCount()).isEqualTo(0);
        verify(userVoteRepository).save(existingVote);
    }

    @Test
    void duplicateVote_ShouldThrowException() {
        // Arrange
        UserVote existingVote = UserVote.builder()
                .user(testUser)
                .votable(testPost)
                .votableType(VotableType.POST)
                .voteType(UserVote.VoteType.LIKE)
                .build();

        when(userVoteRepository.findByUserAndVotableAndVotableType(testUser, testPost, VotableType.POST))
                .thenReturn(Optional.of(existingVote));

        // Act & Assert
        assertThatThrownBy(() ->
                votingService.voteOnVotable(testUser, testPost, UserVote.VoteType.LIKE)
        ).isInstanceOf(DuplicateVoteException.class);
    }

    @Test
    void deleteVote_ShouldDecreaseVoteCount() {
        // Arrange
        UserVote existingVote = UserVote.builder()
                .user(testUser)
                .votable(testPost)
                .votableType(VotableType.POST)
                .voteType(UserVote.VoteType.LIKE)
                .build();
        testPost.setLikeCount(1);

        when(userVoteRepository.findByUserAndVotableAndVotableType(testUser, testPost, VotableType.POST))
                .thenReturn(Optional.of(existingVote));

        // Act
        votingService.deleteVote(testUser, testPost);

        // Assert
        verify(postRepository).save(testPost);
        assertThat(testPost.getLikeCount()).isEqualTo(0);
        verify(userVoteRepository).deleteByVotableIdAndUserId(testPost.getId(), testUser.getId());
    }

    @Test
    void getUserVoteOnVotable_ShouldReturnVoteType() {
        // Arrange
        UserVote existingVote = UserVote.builder()
                .user(testUser)
                .votable(testReply)
                .votableType(VotableType.REPLY)
                .voteType(UserVote.VoteType.DISLIKE)
                .build();

        when(userVoteRepository.findByUserAndVotableAndVotableType(testUser, testReply, VotableType.REPLY))
                .thenReturn(Optional.of(existingVote));

        // Act
        Optional<UserVote.VoteType> result = votingService.getUserVoteOnVotable(testUser, testReply);

        // Assert
        assertThat(result).contains(UserVote.VoteType.DISLIKE);
    }
}