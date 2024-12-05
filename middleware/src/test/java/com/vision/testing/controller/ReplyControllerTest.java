package com.vision.testing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.middleware.controller.ReplyController;
import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.Reply;
import com.vision.middleware.domain.relations.UserVote;
import com.vision.middleware.dto.ReplyDTO;
import com.vision.middleware.dto.ReplyRequest;
import com.vision.middleware.dto.UserDTO;
import com.vision.middleware.service.PostService;
import com.vision.middleware.service.ReplyService;
import com.vision.middleware.service.UserService;
import com.vision.middleware.service.VotingService;
import com.vision.middleware.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ReplyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReplyService replyService;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @Mock
    private VotingService votingService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private ReplyController replyController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(replyController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetRepliesToPostNoAuth() throws Exception {
        long postId = 1L;
        List<ReplyDTO> replyDTOs = Collections.singletonList(
                ReplyDTO.builder()
                        .id(1L)
                        .text("Test Reply")
                        .build()
        );

        when(replyService.getCommentTreeForPost(postId, null)).thenReturn(replyDTOs);

        mockMvc.perform(get("/replies/post/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(replyDTOs)));
    }

    @Test
    public void testCreateReplyWithParentReply() throws Exception {
        long postId = 1L;
        long userId = 1L;
        long parentReplyId = 2L;
        String token = "Bearer validToken";
        ReplyRequest request = ReplyRequest.builder().text("Test Reply").toReplyId(parentReplyId).build();

        ApplicationUser user = makeTestUser(userId, "testuser");
        Post post = Post.builder().id(postId).build();
        Reply parentReply = Reply.builder().id(parentReplyId).build();
        Reply newReply = Reply.builder().id(3L).text("Test Reply").build();
        ReplyDTO expectedDTO = ReplyDTO.builder()
                .id(3L)
                .text("Test Reply")
                .author(
                        UserDTO.builder()
                                .userId(userId)
                                .username("testuser")
                                .build()
                )
                .build();

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(userId);
        when(userService.loadUserById(userId)).thenReturn(user);
        when(postService.loadPostById(postId)).thenReturn(post);
        when(replyService.findReplyById(parentReplyId)).thenReturn(parentReply);
        when(replyService.createReply(post, user, request.getText(), parentReply)).thenReturn(newReply);

        mockMvc.perform(post("/replies/post/{postId}", postId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedDTO)));
    }

    @Test
    public void testCreateReplyWithoutParentReply() throws Exception {
        long postId = 1L;
        long userId = 1L;
        String token = "Bearer validToken";
        ReplyRequest request = ReplyRequest.builder().text("Test Reply").toReplyId(0).build();

        ApplicationUser user = makeTestUser(userId, "testuser");
        Post post = Post.builder().id(postId).build();
        Reply newReply = Reply.builder().id(3L).text("Test Reply").build();
        ReplyDTO expectedDTO = ReplyDTO.builder()
                .id(3L)
                .text("Test Reply")
                .author(
                        UserDTO.builder()
                                .userId(userId)
                                .username("testuser")
                                .build()
                )
                .build();

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(userId);
        when(userService.loadUserById(userId)).thenReturn(user);
        when(postService.loadPostById(postId)).thenReturn(post);
        when(replyService.createReply(post, user, request.getText(), null)).thenReturn(newReply);

        mockMvc.perform(post("/replies/post/{postId}", postId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedDTO)));
    }

    @Test
    public void testVoteOnReply() throws Exception {
        long replyId = 1L;
        long userId = 1L;
        String token = "Bearer validToken";
        UserVote.VoteType voteType = UserVote.VoteType.LIKE;

        ApplicationUser user = makeTestUser(userId, "testuser");
        Reply reply = Reply.builder().id(replyId).build();

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(userId);
        when(userService.loadUserById(userId)).thenReturn(user);
        when(replyService.findReplyById(replyId)).thenReturn(reply);

        mockMvc.perform(post("/replies/vote/{replyId}", replyId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteType)))
                .andExpect(status().isOk())
                .andExpect(content().string("Vote created."));
    }

    private ApplicationUser makeTestUser(long userId, String username) {
        return ApplicationUser.builder()
                .id(userId)
                .username(username)
                .password("testpassword")
                .fullName("hello world")
                .email("a@b.com")
                .phoneNumber("1234567890")
                .build();
    }
}