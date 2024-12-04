package com.vision.demo.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.MediaPost;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.relations.UserVote;
import com.vision.middleware.dto.PostDTO;
import com.vision.middleware.exceptions.IdNotFoundException;
import com.vision.middleware.repo.PostRepository;
import com.vision.middleware.service.PostService;
import com.vision.middleware.service.UserService;
import com.vision.middleware.service.VotingService;
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
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @Mock
    private VotingService votingService;

    @InjectMocks
    private PostService postService;

    private ApplicationUser testUser;
    private PostDTO testPostDTO;
    private Post testPost;

    @BeforeEach
    public void setUp() {
        testUser = new ApplicationUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testPostDTO = new PostDTO();
        testPostDTO.setTitle("Test Post");
        testPostDTO.setText("Test Content");

        testPost = Post.builder()
                .id(1L)
                .postedBy(testUser)
                .title("Test Post")
                .text("Test Content")
                .datePosted(new Date())
                .build();
    }

    @Test
    public void createPost_TextPost_Success() {
        // Arrange
        when(userService.loadUserById(testUser.getId())).thenReturn(testUser);
        when(postRepository.save(any(Post.class))).thenReturn(testPost);

        // Act
        Post createdPost = postService.createPost(testPostDTO, testUser.getId());

        // Assert
        assertThat(createdPost).isNotNull();
        assertThat(createdPost.getPostedBy()).isEqualTo(testUser);
        assertThat(createdPost.getTitle()).isEqualTo(testPostDTO.getTitle());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    public void createPost_MediaPost_Success() {
        // Arrange
        testPostDTO.setMediaFileName("test-media.jpg");
        when(userService.loadUserById(testUser.getId())).thenReturn(testUser);

        MediaPost mediaPost = MediaPost.builder()
                .postedBy(testUser)
                .title(testPostDTO.getTitle())
                .mediaFileName(testPostDTO.getMediaFileName())
                .build();
        when(postRepository.save(any(MediaPost.class))).thenReturn(mediaPost);

        // Act
        Post createdPost = postService.createPost(testPostDTO, testUser.getId());

        // Assert
        assertThat(createdPost).isInstanceOf(MediaPost.class);
        assertThat(((MediaPost)createdPost).getMediaFileName()).isEqualTo("test-media.jpg");
        verify(postRepository).save(any(MediaPost.class));
    }

    @Test
    public void userVoteOnPost_Success() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userService.loadUserById(testUser.getId())).thenReturn(testUser);

        // Act
        postService.userVoteOnPost(1L, testUser.getId(), UserVote.VoteType.LIKE);

        // Assert
        verify(votingService).voteOnVotable(testUser, testPost, UserVote.VoteType.LIKE);
    }

    @Test
    public void userVoteOnPost_PostNotFound_ThrowsException() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() ->
                postService.userVoteOnPost(1L, testUser.getId(), UserVote.VoteType.LIKE)
        ).isInstanceOf(IdNotFoundException.class);
    }

    @Test
    public void removeUserVote_Success() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userService.loadUserById(testUser.getId())).thenReturn(testUser);

        // Act
        postService.removeUserVote(1L, testUser.getId());

        // Assert
        verify(votingService).deleteVote(testUser, testPost);
    }

    @Test
    public void removeUserVote_NoExistingVote_ShouldNotThrowException() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userService.loadUserById(testUser.getId())).thenReturn(testUser);

        // Act & Assert (should not throw any exception)
        assertThatNoException().isThrownBy(() ->
                postService.removeUserVote(1L, testUser.getId())
        );

        // Verify voting service was called
        verify(votingService).deleteVote(testUser, testPost);
    }

    @Test
    public void removeUserVote_PostNotFound_ThrowsIdNotFoundException() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() -> postService.removeUserVote(1L, testUser.getId()))
                .isInstanceOf(IdNotFoundException.class)
                .hasMessageContaining("Post id 1 not found");
    }

    @Test
    public void getUserVote_Success() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userService.loadUserById(testUser.getId())).thenReturn(testUser);
        when(votingService.getUserVoteOnVotable(testUser, testPost))
                .thenReturn(Optional.of(UserVote.VoteType.LIKE));

        // Act
        Optional<UserVote.VoteType> vote = postService.getUserVote(1L, testUser.getId());

        // Assert
        assertThat(vote).isPresent();
        assertThat(vote.get()).isEqualTo(UserVote.VoteType.LIKE);
    }

    @Test
    public void getUserVote_NoExistingVote_ShouldReturnEmptyOptional() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userService.loadUserById(testUser.getId())).thenReturn(testUser);
        when(votingService.getUserVoteOnVotable(testUser, testPost))
                .thenReturn(Optional.empty());

        // Act
        Optional<UserVote.VoteType> vote = postService.getUserVote(1L, testUser.getId());

        // Assert
        assertThat(vote).isEmpty();
    }

    @Test
    public void getUserVote_PostNotFound_ThrowsIdNotFoundException() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() -> postService.getUserVote(1L, testUser.getId()))
                .isInstanceOf(IdNotFoundException.class)
                .hasMessageContaining("Post id 1 not found");
    }

    @Test
    public void getAllPosts_Success() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("datePosted"));
        List<Post> postList = Collections.singletonList(testPost);
        Page<Post> postPage = new PageImpl<>(postList, pageRequest, postList.size());

        when(postRepository.findAll(pageRequest)).thenReturn(postPage);

        // Act
        Page<Post> result = postService.getAllPosts(0, 10);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testPost);
    }

    @Test
    public void getAllPostsByUsername_Success() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("datePosted"));
        List<Post> postList = Collections.singletonList(testPost);
        Page<Post> postPage = new PageImpl<>(postList, pageRequest, postList.size());

        when(userService.loadUserByUsername("testuser")).thenReturn(testUser);
        when(postRepository.findByPostedBy(testUser, pageRequest)).thenReturn(postPage);

        // Act
        Page<Post> result = postService.getAllPostsByUsername("testuser", 0, 10);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testPost);
    }

    @Test
    public void loadPostById_Success() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));

        // Act
        Post retrievedPost = postService.loadPostById(1L);

        // Assert
        assertThat(retrievedPost).isEqualTo(testPost);
    }

    @Test
    public void loadPostById_NotFound_ThrowsException() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() ->
                postService.loadPostById(1L)
        ).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void searchPosts_Success() {
        // Arrange
        List<Post> postList = Collections.singletonList(testPost);
        when(postRepository.searchPosts("test", null)).thenReturn(postList);

        // Act
        List<Post> result = postService.searchPosts("test");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testPost);
    }

    @Test
    public void searchPostsByUser_Success() {
        // Arrange
        List<Post> postList = Collections.singletonList(testPost);
        when(postRepository.searchPosts("test", testUser)).thenReturn(postList);

        // Act
        List<Post> result = postService.searchPostsByUser("test", testUser);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testPost);
    }

    @Test
    public void searchPostsByDateRange_Success() {
        // Arrange
        Date startDate = new Date();
        Date endDate = new Date();
        List<Post> postList = Collections.singletonList(testPost);

        when(postRepository.searchPostsByDateAndQuery("test", testUser, startDate, endDate))
                .thenReturn(postList);

        // Act
        List<Post> result = postService.searchPostsByDateRange("test", testUser, startDate, endDate);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testPost);
    }
}