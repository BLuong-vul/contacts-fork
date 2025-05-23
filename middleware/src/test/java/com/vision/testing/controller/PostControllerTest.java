package com.vision.testing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vision.middleware.controller.PostController;
import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.MediaPost;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.relations.UserVote;
import com.vision.middleware.dto.PostDTO;
import com.vision.middleware.dto.UserDTO;
import com.vision.middleware.dto.VoteDTO;
import com.vision.middleware.repo.UserRepository;
import com.vision.middleware.service.PostService;
import com.vision.middleware.utils.JwtUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.print.attribute.standard.Media;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PostControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PostService postService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostController postController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreatePost_Success() throws Exception {
        // Arrange
        String token = "validToken";
        PostDTO requestDTO = createSamplePostDTO();
        ApplicationUser user = new ApplicationUser();
        Post post = Post.builder()
                .id(1L)
                .title("Test Post")
                .text("Test Content")
                .postedBy(user)
                .build();
        user.setId(1L);
        post.setPostedBy(user);

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(1L);
        when(postService.createPost(any(PostDTO.class), any(Long.class))).thenReturn(post);

        // Act & Assert
        mockMvc.perform(post("/post/new")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(post.getId()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.text").value(post.getText()));

        verify(postService).createPost(any(PostDTO.class), any(Long.class));
    }

    @Test
    void testCreateMediaPost_Success() throws Exception {
        // Arrange
        String token = "validToken";
        PostDTO requestDTO = createSamplePostDTO();
        ApplicationUser user = new ApplicationUser();
        Post post = MediaPost.builder()
                .id(1L)
                .title("Test Post")
                .text("Test Content")
                .postedBy(user)
                .mediaFileName("hello world")
                .build();
        user.setId(1L);
        post.setPostedBy(user);

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(1L);
        when(postService.createPost(any(PostDTO.class), any(Long.class))).thenReturn(post);

        // Act & Assert
        mockMvc.perform(post("/post/new")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(post.getId()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.text").value(post.getText()));

        verify(postService).createPost(any(PostDTO.class), any(Long.class));
    }
    @Test
    void testGetPostPage_Success() throws Exception {
        // Arrange
        int page = 0;
        int size = 10;
        List<Post> posts = createSamplePosts();
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(page, size), posts.size());

        when(postService.getAllPosts(page, size, "date", null, null))
                .thenReturn(postPage);

        // Act & Assert
        mockMvc.perform(get("/post/all")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort-by", "date"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.[0].postId").value(posts.get(0).getId()));

        verify(postService).getAllPosts(page, size, "date", null, null);
    }

    @Test
    void testGetPostPageByUsername_Success() throws Exception {
        // Arrange
        String username = "testuser";
        int page = 0;
        int size = 10;
        List<Post> posts = createSamplePosts();
        Page<Post> postPage = new PageImpl<>(posts, PageRequest.of(page, size), posts.size());

        when(postService.getAllPostsByUsername(username, page, size, "date", null, null))
                .thenReturn(postPage);

        // Act & Assert
        mockMvc.perform(get("/post/by-user")
                        .param("username", username)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort-by", "date"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.[0].postId").value(posts.get(0).getId()));

        verify(postService).getAllPostsByUsername(username, page, size, "date", null, null);
    }

    @Test
    void testGetPostById_Success() throws Exception {
        // Arrange
        long postId = 1L;
        Post post = createSamplePosts().get(0);
        PostDTO postDTO = createSamplePostDTO();
        postDTO.setPostId(postId);

        when(postService.loadPostById(postId))
                .thenReturn(post);

        // Act & Assert
        mockMvc.perform(get("/post/by-id")
                        .param("id", String.valueOf(postId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.postId").value(postDTO.getPostId()))
                .andExpect(jsonPath("$.title").value(postDTO.getTitle()))
                .andExpect(jsonPath("$.text").value(postDTO.getText()));

        verify(postService).loadPostById(postId);
    }

    @Test
    void testVoteOnPost_Success() throws Exception {
        // Arrange
        String token = "validToken";
        long userId = 1L;
        VoteDTO voteDTO = createSampleVoteDTO();

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(userId);

        // Act & Assert
        mockMvc.perform(post("/post/vote")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteDTO)))
                .andExpect(status().isOk());

        verify(postService).userVoteOnPost(voteDTO.getVotableId(), userId, voteDTO.getVoteType());
    }

    @Test
    void testSearchPosts_Success() throws Exception {
        // Arrange
        String query = "test query";
        List<Post> posts = createSamplePosts();

        when(postService.searchPosts(query)).thenReturn(posts);

        // Act & Assert
        mockMvc.perform(get("/post/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(postService).searchPosts(query);
    }

    @Test
    void testSearchPosts_InvalidQuery() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/post/search")
                        .param("query", ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.emptyOrNullString()));
    }

    @Test
    void testSearchPostsByUser_Success() throws Exception {
        // Arrange
        String query = "test query";
        long userId = 1L;
        ApplicationUser user = new ApplicationUser();
        List<Post> posts = createSamplePosts();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postService.searchPostsByUser(query, user)).thenReturn(posts);

        // Act & Assert
        mockMvc.perform(get("/post/search-by-user")
                        .param("query", query)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());

        verify(postService).searchPostsByUser(query, user);
    }

    @Test
    void testSearchPostsByUser_EmptyQuery() throws Exception {
        // Arrange
        String query = "";
        long userId = 1L;
        ApplicationUser user = new ApplicationUser();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        mockMvc.perform(get("/post/search-by-user")
                        .param("query", query)
                        .param("userId", String.valueOf(userId)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.emptyOrNullString()));
    }

    @Test
    void testSearchPostsByUserAndDate_Success() throws Exception {
        // Arrange
        String query = "test query";
        long userId = 1L;
        Date startDate = new Date(System.currentTimeMillis() - 86400000); // 1 day ago
        Date endDate = new Date();
        ApplicationUser user = ApplicationUser.builder()
                .id(1L)
                .username("testuser")
                .password("testpassword")
                .fullName("hello world")
                .email("a@b.com")
                .phoneNumber("1234567890")
                .build();
        List<Post> posts = createSamplePosts();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postService.searchPostsByDateRange(query, user, startDate, endDate)).thenReturn(posts);

        // Format dates to ISO 8601 format in UTC
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String formattedStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).format(formatter);
        String formattedEndDate = endDate.toInstant().atZone(ZoneId.systemDefault()).format(formatter);

        // Act & Assert
        mockMvc.perform(get("/post/search-by-user-date")
                        .param("query", query)
                        .param("userId", String.valueOf(userId))
                        .param("startDate", formattedStartDate)
                        .param("endDate", formattedEndDate))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testSearchPostsByUserAnd_StartDateAfterEndDate() throws Exception {
        // Arrange
        String query = "test query";
        long userId = 1L;
        Date startDate = new Date();
        Date endDate = new Date(System.currentTimeMillis() - 86400000); // 1 day ago
        ApplicationUser user = new ApplicationUser();
        List<Post> posts = createSamplePosts();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Format dates to ISO 8601 format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String formattedStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).format(formatter);
        String formattedEndDate = endDate.toInstant().atZone(ZoneId.systemDefault()).format(formatter);

        // Act & Assert
        mockMvc.perform(get("/post/search-by-user-date")
                        .param("query", query)
                        .param("userId", String.valueOf(userId))
                        .param("startDate", formattedStartDate)
                        .param("endDate", formattedEndDate))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.emptyOrNullString()));
    }

    @Test
    void testCheckUserVote_VoteExists() throws Exception {
        // Arrange
        String token = "validToken";
        long userId = 1L;
        long votableId = 1L;

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(userId);
        when(postService.getUserVote(votableId, userId)).thenReturn(Optional.of(UserVote.VoteType.LIKE));

        // Act & Assert
        mockMvc.perform(get("/post/get-vote")
                        .header("Authorization", token)
                        .param("votableId", String.valueOf(votableId)))
                .andExpect(status().isOk())
                .andExpect(content().string("\"LIKE\""));

        verify(postService).getUserVote(votableId, userId);
    }

    @Test
    void testCheckUserVote_NoVoteExists() throws Exception {
        // Arrange
        String token = "validToken";
        long userId = 1L;
        long votableId = 1L;

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(userId);
        when(postService.getUserVote(votableId, userId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/post/get-vote")
                        .header("Authorization", token)
                        .param("votableId", String.valueOf(votableId)))
                .andExpect(status().isNoContent());

        verify(postService).getUserVote(votableId, userId);
    }

    @Test
    void testUnvoteOnPost_Success() throws Exception {
        // Arrange
        String token = "validToken";
        long userId = 1L;
        long votableId = 1L;

        when(jwtUtil.checkJwtAuthAndGetUserId(token)).thenReturn(userId);

        // Act & Assert
        mockMvc.perform(delete("/post/unvote")
                        .header("Authorization", token)
                        .param("votableId", String.valueOf(votableId)))
                .andExpect(status().isNoContent());

        verify(postService).removeUserVote(votableId, userId);
    }

    // Helper methods to create sample objects for testing
    private PostDTO createSamplePostDTO() {
        UserDTO user = UserDTO.builder()
                .userId(1L)
                .username("testuser")
                .fullName("hello world")
                .build();

        return PostDTO.builder()
                .title("Test Post")
                .text("Test Content")
                .postedBy(user)
                .build();
    }

    private List<Post> createSamplePosts() {
        ApplicationUser user = ApplicationUser.builder()
                .id(1L)
                .username("testuser")
                .password("testpassword")
                .fullName("hello world")
                .email("a@b.com")
                .phoneNumber("1234567890")
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("Test Post")
                .text("Test Content")
                .postedBy(user)
                .build();

        return List.of(post);
    }

    private List<Post> createSampleImagePosts() {
        ApplicationUser user = ApplicationUser.builder()
                .id(1L)
                .username("testuser")
                .password("testpassword")
                .fullName("hello world")
                .email("a@b.com")
                .phoneNumber("1234567890")
                .build();

        Post post = MediaPost.builder()
                .id(1L)
                .title("Test Post")
                .text("Test Content")
                .postedBy(user)
                .mediaFileName("test media")
                .build();

        return List.of(post);
    }

    private VoteDTO createSampleVoteDTO() {
        return VoteDTO.builder()
                .votableId(1L)
                .voteType(UserVote.VoteType.LIKE)
                .build();
    }
}