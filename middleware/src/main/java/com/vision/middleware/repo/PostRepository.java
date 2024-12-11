package com.vision.middleware.repo;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Post;
import com.vision.middleware.repo.searchspec.PostSearchSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    Optional<Post> findById(long id);
    Page<Post> findByPostedBy(ApplicationUser postedBy, Pageable pageable);

    default Page<Post> findAllPostsWithFilters(Pageable pageable, Date beforeDate, Date afterDate) {
        Specification<Post> spec = Specification.where(null);
        
        if (beforeDate != null) {
            spec = spec.and(PostSearchSpecification.filterByBeforeDate(beforeDate));
        }
        if (afterDate != null) {
            spec = spec.and(PostSearchSpecification.filterByAfterDate(afterDate));
        }

        return findAll(spec, pageable);
    }

    default Page<Post> findAllPostsByUserWithFilters(ApplicationUser user, Pageable pageable, Date beforeDate, Date afterDate) {
        Specification<Post> spec = Specification.where(null);

        if (user != null) {
            spec = spec.and(PostSearchSpecification.filterByUser(user));
        }

        if (beforeDate != null) {
            spec = spec.and(PostSearchSpecification.filterByBeforeDate(beforeDate));
        }
        if (afterDate != null) {
            spec = spec.and(PostSearchSpecification.filterByAfterDate(afterDate));
        }

        return findAll(spec, pageable);
    }




    // searching methods
    default List<Post> searchPosts(String query, ApplicationUser user) {
        return findAll(PostSearchSpecification.searchPosts(query, user));
    }

    default List<Post> searchPostsByDateAndQuery(
            String query,
            ApplicationUser user,
            Date startDate,
            Date endDate
    ) {
        return findAll(
                Specification.where(
                        PostSearchSpecification.searchPosts(query, user)
                                .and(PostSearchSpecification.searchByDateRange(startDate, endDate))
                )
        );
    }
}
