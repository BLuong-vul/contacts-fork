package com.vision.testing.domain.baseentities;

import com.vision.middleware.domain.baseentities.VotableEntity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class VotableEntityTest {

    @Table(name = "test_votable_entity")
    @SequenceGenerator(name = "id_generator", sequenceName = "test_votable_entity_seq", allocationSize = 1)
    @SuperBuilder
    @NoArgsConstructor
    public static class TestVotableEntity extends VotableEntity {
        // This class is used solely for testing purposes
    }

    private TestVotableEntity votableEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        votableEntity = new TestVotableEntity();
    }

    @Test
    void testInitialState() {
        assertEquals(0, votableEntity.getLikeCount());
        assertEquals(0, votableEntity.getDislikeCount());
        assertEquals(0, votableEntity.getVoteScore());
    }

    @Test
    void testUpdateDerivedFields() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        votableEntity = TestVotableEntity.builder()
                .id(1)
                .likeCount(5)
                .dislikeCount(3)
                .build();

        Method updateDerivedFieldsMethod = VotableEntity.class.getDeclaredMethod("updateDerivedFields");
        updateDerivedFieldsMethod.setAccessible(true);
        updateDerivedFieldsMethod.invoke(votableEntity);

        assertEquals(2, votableEntity.getVoteScore());
    }

    @Test
    void testOnPrePersist() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        votableEntity.setLikeCount(10);
        votableEntity.setDislikeCount(4);

        Method onPrePersistMethod = VotableEntity.class.getDeclaredMethod("onPrePersist");
        onPrePersistMethod.setAccessible(true);
        onPrePersistMethod.invoke(votableEntity);

        assertEquals(6, votableEntity.getVoteScore());
    }

    @Test
    void testOnPreUpdate() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        votableEntity.setLikeCount(7);
        votableEntity.setDislikeCount(2);

        Method onPreUpdateMethod = VotableEntity.class.getDeclaredMethod("onPreUpdate");
        onPreUpdateMethod.setAccessible(true);
        onPreUpdateMethod.invoke(votableEntity);

        assertEquals(5, votableEntity.getVoteScore());
    }

    @Test
    void testVoteScoreCalculation() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        votableEntity.setLikeCount(15);
        votableEntity.setDislikeCount(8);

        Method updateDerivedFieldsMethod = VotableEntity.class.getDeclaredMethod("updateDerivedFields");
        updateDerivedFieldsMethod.setAccessible(true);
        updateDerivedFieldsMethod.invoke(votableEntity);

        assertEquals(7, votableEntity.getVoteScore());
    }

    @Test
    void testNegativeVoteScore() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        votableEntity.setLikeCount(3);
        votableEntity.setDislikeCount(5);

        Method updateDerivedFieldsMethod = VotableEntity.class.getDeclaredMethod("updateDerivedFields");
        updateDerivedFieldsMethod.setAccessible(true);
        updateDerivedFieldsMethod.invoke(votableEntity);

        assertEquals(-2, votableEntity.getVoteScore());
    }

    @Test
    void testZeroVoteScore() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        votableEntity.setLikeCount(5);
        votableEntity.setDislikeCount(5);

        Method updateDerivedFieldsMethod = VotableEntity.class.getDeclaredMethod("updateDerivedFields");
        updateDerivedFieldsMethod.setAccessible(true);
        updateDerivedFieldsMethod.invoke(votableEntity);

        assertEquals(0, votableEntity.getVoteScore());
    }
}