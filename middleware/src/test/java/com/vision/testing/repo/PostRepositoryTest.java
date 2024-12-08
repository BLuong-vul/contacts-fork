package com.vision.testing.repo;

import com.vision.middleware.Application;
import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.baseentities.VotableEntity;
import com.vision.middleware.repo.PostRepository;
import com.vision.middleware.repo.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(classes = Application.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PostRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("username")
            .withPassword("password");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private static ApplicationUser testUser;

    @BeforeEach
    void setUp() {
        // Create a test user
        if (testUser == null) {
            testUser = ApplicationUser.builder()
                    .username("testuser")
                    .password("testpassword")
                    .fullName("hello world")
                    .email("test@example.com")
                    .phoneNumber("1234567890")
                    .build();
            testUser = userRepository.save(testUser);
        }
    }

    @AfterEach
    void tearDown() {
        postRepository.deleteAll();
    }

    @Test
    void testSaveAndFindPost() {
        // Create a test post
        Post post = new Post();
        post.setText("Test Post Content");
        post.setPostedBy(testUser);
        post.setDatePosted(new Date());

        // Save the post
        Post savedPost = postRepository.save(post);

        // Find the post by ID
        Optional<Post> foundPost = postRepository.findById(savedPost.getId());

        // Assertions
        assertThat(foundPost).isPresent();
        assertThat(foundPost.get().getText()).isEqualTo("Test Post Content");
        assertThat(foundPost.get().getPostedBy().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void testFindByPostedBy() {
        // Create multiple posts by the test user
        for (int i = 0; i < 10; i++) {
            Post post = Post.builder()
                    .text("Test Post Content " + i)
                    .postedBy(testUser)
                    .datePosted(new Date())
                    .build();
            postRepository.save(post);
        }

        // Retrieve posts with pagination
        Page<Post> userPosts = postRepository.findByPostedBy(testUser, PageRequest.of(0, 5));

        // Assertions
        assertThat(userPosts).hasSize(5);
        assertThat(userPosts.getTotalElements()).isEqualTo(10);
        assertThat(userPosts.getContent()).allMatch(post -> post.getPostedBy().getId() == testUser.getId());
    }

    @Test
    void testSearchPosts() {
        // Create posts with different contents
        Post post1 = createPost("Hello World Java Test", testUser);
        Post post2 = createPost("Another Java Test Post", testUser);
        Post post3 = createPost("Unrelated Content", testUser);

        // Search posts containing "Java"
        List<Post> searchResults = postRepository.searchPosts("Java", testUser);

        // Assertions
        assertThat(searchResults).hasSize(2);
        assertThat(searchResults.stream().map(VotableEntity::getId)).contains(post1.getId(), post2.getId());
        assertThat(searchResults.stream().map(VotableEntity::getId)).doesNotContain(post3.getId());
    }

    @Test
    void testSearchPosts_NoUser() {
        // Create posts with different contents
        Post post1 = createPost("Hello World Java Test", testUser);
        Post post2 = createPost("Another Java Test Post", testUser);
        Post post3 = createPost("Unrelated Content", testUser);

        // Search posts containing "Java"
        List<Post> searchResults = postRepository.searchPosts("Java", null);

        // Assertions
        assertThat(searchResults).hasSize(2);
        assertThat(searchResults.stream().map(VotableEntity::getId)).contains(post1.getId(), post2.getId());
        assertThat(searchResults.stream().map(VotableEntity::getId)).doesNotContain(post3.getId());
    }

    @Test
    void testSearchPostsByDateRange() {
        // Create posts with different dates
        Date now = new Date();
        Date oneWeekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000L);
        Date twoWeeksAgo = new Date(now.getTime() - 14 * 24 * 60 * 60 * 1000L);

        Post post1 = createPost("Recent Post", testUser, now);
        Post post2 = createPost("One Week Ago Post", testUser, oneWeekAgo);
        Post post3 = createPost("Two Weeks Ago Post", testUser, twoWeeksAgo);

        // Search posts within the last week
        List<Post> searchResults = postRepository.searchPostsByDateAndQuery(
                null, testUser, oneWeekAgo, now
        );

        // Assertions
        assertThat(searchResults).hasSize(2);
        assertThat(searchResults.stream().map(VotableEntity::getId)).contains(post1.getId(), post2.getId());
        assertThat(searchResults.stream().map(VotableEntity::getId)).doesNotContain(post3.getId());
    }

    @Test
    void testSearchPostsByDateRange_NoUser() {
        // Create posts with different dates
        Date now = new Date();
        Date oneWeekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000L);
        Date twoWeeksAgo = new Date(now.getTime() - 14 * 24 * 60 * 60 * 1000L);

        Post post1 = createPost("Recent Post", testUser, now);
        Post post2 = createPost("One Week Ago Post", testUser, oneWeekAgo);
        Post post3 = createPost("Two Weeks Ago Post", testUser, twoWeeksAgo);

        // Search posts within the last week
        List<Post> searchResults = postRepository.searchPostsByDateAndQuery(
                null, null, oneWeekAgo, now
        );

        // Assertions
        assertThat(searchResults).hasSize(2);
        assertThat(searchResults.stream().map(VotableEntity::getId)).contains(post1.getId(), post2.getId());
        assertThat(searchResults.stream().map(VotableEntity::getId)).doesNotContain(post3.getId());
    }

    @Test
    void testSearchPostsByDateRange_NoDate() {
        // Create posts with different dates
        Date now = new Date();
        Date oneWeekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000L);
        Date twoWeeksAgo = new Date(now.getTime() - 14 * 24 * 60 * 60 * 1000L);

        Post post1 = createPost("Recent Post", testUser, now);
        Post post2 = createPost("One Week Ago Post", testUser, oneWeekAgo);
        Post post3 = createPost("Two Weeks Ago Post", testUser, twoWeeksAgo);

        // Search posts within the last week
        List<Post> searchResults = postRepository.searchPostsByDateAndQuery(
                null, null, null, null
        );

        // Assertions
        assertThat(searchResults).hasSize(3);
    }

    // Helper method to create a post with specific content and user
    private Post createPost(String content, ApplicationUser user) {
        return postRepository.save(Post.builder()
                .text(content)
                .postedBy(user)
                .datePosted(new Date())
                .build());
    }

    // Overloaded helper method to create a post with specific date
    private Post createPost(String content, ApplicationUser user, Date createdAt) {
        return postRepository.save(Post.builder()
                .text(content)
                .postedBy(user)
                .datePosted(createdAt)
                .build());
    }
}