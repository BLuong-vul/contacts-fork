package com.vision.testing.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.Reply;
import com.vision.middleware.domain.relations.UserVote;
import com.vision.middleware.dto.ReplyDTO;
import com.vision.middleware.exceptions.IdNotFoundException;
import com.vision.middleware.repo.ReplyRepository;
import com.vision.middleware.service.PostService;
import com.vision.middleware.service.ReplyService;
import com.vision.middleware.service.UserService;
import com.vision.middleware.service.VotingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReplyServiceTest {

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @Mock
    private VotingService votingService;

    @InjectMocks
    private ReplyService replyService;

    private ApplicationUser testUser;
    private Post testPost;
    private Reply testReply;
    private Reply testChildReply;

    @BeforeEach
    void setUp() {
        testUser = ApplicationUser.builder()
                .id(1L)
                .username("testuser")
                .password("testpassword")
                .fullName("testname")
                .email("test@email.com")
                .phoneNumber("1234567890")
                .build();

        testPost = Post.builder()
                .id(1L)
                .build();

        testChildReply = Reply.builder()
                        .id(2L)
                        .post(testPost)
                        .author(testUser)
                        .text("Child test reply content")
                        .datePosted(new Date())
                        .childReplies(new HashSet<>())
                        .build();

        testReply = Reply.builder()
                .id(1L)
                .post(testPost)
                .author(testUser)
                .text("Test reply content")
                .datePosted(new Date())
                .childReplies(new HashSet<>(Collections.singletonList(testChildReply)))
                .build();
    }

    @Test
    void createReply_ValidInput_ReplyCreatedSuccessfully() {
        when(replyRepository.save(any(Reply.class))).thenReturn(testReply);

        Reply createdReply = replyService.createReply(testPost, testUser, "Test reply content", null);

        assertThat(createdReply).isNotNull();
        assertThat(createdReply.getPost()).isEqualTo(testPost);
        assertThat(createdReply.getAuthor()).isEqualTo(testUser);
        assertThat(createdReply.getText()).isEqualTo("Test reply content");

        verify(replyRepository).save(any(Reply.class));
    }

    @Test
    void createReply_EmptyText_ThrowsIllegalArgumentException() {
        assertThatThrownBy(() ->
                replyService.createReply(testPost, testUser, "", null)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Reply text cannot be empty");
    }

    @Test
    void createReply_TextTooLong_ThrowsIllegalArgumentException() {
        String longText = "x".repeat(5001);

        assertThatThrownBy(() ->
                replyService.createReply(testPost, testUser, longText, null)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("exceeds maximum length");
    }

    @Test
    void createReply_NestedReplyExceedingMaxDepth_ThrowsIllegalArgumentException() {
        // Create a deeply nested reply chain
        Reply deepestParent = testReply;
        for (int i = 0; i < 100; i++) {
            deepestParent = Reply.builder()
                    .parentReply(deepestParent)
                    .build();
        }

        Reply finalDeepestParent = deepestParent;
        assertThatThrownBy(() ->
                replyService.createReply(testPost, testUser, "Nested reply", finalDeepestParent)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Maximum nesting depth");
    }

    @Test
    void createChildReply_ValidInput_ReplyCreatedSuccessfully() {
        // given that parent reply has no replies:
        testReply.setChildReplies(new HashSet<>());

        testChildReply.setParentReply(testReply);
        when(replyRepository.save(any(Reply.class))).thenReturn(testChildReply);

        Reply createdReply = replyService.createReply(testPost, testUser, "Child test reply content", testReply);

        assertThat(createdReply).isNotNull();
        assertThat(createdReply.getPost()).isEqualTo(testPost);
        assertThat(createdReply.getAuthor()).isEqualTo(testUser);
        assertThat(createdReply.getText()).isEqualTo("Child test reply content");

        assertThat(createdReply.getParentReply().getId()).isEqualTo(testReply.getId());

        verify(replyRepository).save(any(Reply.class));
    }

    @Test
    void findReplyById_ExistingReply_ReturnsReply() {
        when(replyRepository.findById(1L)).thenReturn(Optional.of(testReply));

        Reply foundReply = replyService.findReplyById(1L);

        assertThat(foundReply).isEqualTo(testReply);
    }

    @Test
    void findReplyById_NonExistingReply_ThrowsIdNotFoundException() {
        when(replyRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                replyService.findReplyById(999L)
        ).isInstanceOf(IdNotFoundException.class)
                .hasMessageContaining("Reply with id 999 not found");
    }

    @Test
    void voteOnReply_ValidInput_VotingServiceCalled() {
        doNothing().when(votingService).voteOnVotable(testUser, testReply, UserVote.VoteType.LIKE);

        replyService.voteOnReply(testUser, testReply, UserVote.VoteType.LIKE);

        verify(votingService).voteOnVotable(testUser, testReply, UserVote.VoteType.LIKE);
    }

    @Test
    void deleteReply_WithoutChildren_DeletedCompletely() {
        testReply.setChildReplies(new HashSet<>());
        Reply parentReply = Reply.builder()
                .id(2L)
                .build();
        testReply.setParentReply(parentReply);

        replyService.deleteReply(testReply);

        verify(replyRepository).delete(testReply);
        verify(replyRepository).save(parentReply);
    }

    @Test
    void deleteReply_WithChildren_SoftDeleted() {
        Reply childReply = Reply.builder()
                .id(3L)
                .build();
        testReply.addChildReply(childReply);

        when(replyRepository.save(testReply)).thenReturn(testReply);

        replyService.deleteReply(testReply);

        assertThat(testReply.isDeleted()).isTrue();
        assertThat(testReply.getText()).isEqualTo("[deleted]");
        verify(replyRepository).save(testReply);
    }

    @Test
    void getCommentTreeForPost_ReturnsCorrectStructure() {
        List<Reply> topLevelReplies = new ArrayList<>();
        topLevelReplies.add(testReply);

        when(replyRepository.findTopLevelRepliesByPostId(1L)).thenReturn(topLevelReplies);
        when(votingService.getUserVoteOnVotable(testUser, testReply)).thenReturn(Optional.empty());

        List<ReplyDTO> commentTree = replyService.getCommentTreeForPost(1L, testUser);

        assertThat(commentTree).hasSize(1);
        ReplyDTO firstReply = commentTree.get(0);
        assertThat(firstReply.getId()).isEqualTo(testReply.getId());
        assertThat(firstReply.getText()).isEqualTo(testReply.getText());

        ReplyDTO childReply = commentTree.get(0).getReplies().get(0);
        assertThat(childReply.getId()).isEqualTo(testChildReply.getId());
        assertThat(childReply.getText()).isEqualTo(testChildReply.getText());
    }
}