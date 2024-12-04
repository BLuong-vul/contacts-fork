package com.vision.testing.repo;

import com.vision.middleware.Application;
import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.Reply;
import com.vision.middleware.repo.PostRepository;
import com.vision.middleware.repo.ReplyRepository;
import com.vision.middleware.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(classes = Application.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReplyRepositoryTest {


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
    private ReplyRepository replyRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private Post post;
    private ApplicationUser author;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setTitle("Test Post");

        author = ApplicationUser.builder()
                .id(1L)
                .username("testuser")
                .password("testpassword")
                .fullName("testname")
                .email("test@email.com")
                .phoneNumber("1234567890")
                .build();

        post.setPostedBy(author);

        // Save entities to the database
        author = userRepository.save(author);
        post = postRepository.save(post);
    }

    @Test
    void testFindTopLevelRepliesByPostId() {
        // Create and save replies
        Reply reply1 = Reply.builder()
                .post(post)
                .author(author)
                .text("Reply 1")
                .voteScore(10)
                .build();

        Reply reply2 = Reply.builder()
                .post(post)
                .author(author)
                .text("Reply 2")
                .voteScore(5)
                .build();

        replyRepository.save(reply1);
        replyRepository.save(reply2);

        // Fetch top-level replies by postId
        List<Reply> replies = replyRepository.findTopLevelRepliesByPostId(post.getId());

        // Assertions
        assertThat(replies).hasSize(2);
        assertThat(replies.get(0).getText()).isEqualTo("Reply 1");
        assertThat(replies.get(1).getText()).isEqualTo("Reply 2");
    }

    @Test
    void testFindById() {
        // Create and save a reply
        Reply reply = Reply.builder()
                .post(post)
                .author(author)
                .text("Reply")
                .voteScore(10)
                .build();

        reply = replyRepository.save(reply);

        // Fetch reply by id
        Optional<Reply> foundReply = replyRepository.findById(reply.getId());

        // Assertions
        assertThat(foundReply).isPresent();
        assertThat(foundReply.get().getText()).isEqualTo("Reply");
    }
}