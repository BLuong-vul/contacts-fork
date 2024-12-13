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

/**
 * Repository interface for managing Post entities, providing basic CRUD operations
 * and custom query methods with filtering and pagination capabilities.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    /**
     * Retrieves a Post by its unique identifier.
     *
     * @param id the ID of the Post to retrieve
     * @return an Optional containing the Post if found, or an empty Optional otherwise
     */
    Optional<Post> findById(long id);

    /**
     * Fetches a paged list of Posts posted by a specific ApplicationUser.
     *
     * @param postedBy the ApplicationUser who posted the Posts
     * @param pageable pagination information (e.g., page number, size)
     * @return a Page of Posts posted by the specified user
     */
    Page<Post> findByPostedBy(ApplicationUser postedBy, Pageable pageable);

    /**
     * Retrieves a paged list of Posts, filtered by optional date range constraints.
     *
     * @param pageable     pagination information (e.g., page number, size)
     * @param beforeDate   filter Posts with dates before this value (inclusive), or null for no constraint
     * @param afterDate    filter Posts with dates after this value (inclusive), or null for no constraint
     * @return a Page of Posts matching the applied date filters
     */
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

    /**
     * Fetches a paged list of Posts posted by a specific ApplicationUser,
     * optionally filtered by date range constraints.
     *
     * @param user        the ApplicationUser who posted the Posts, or null for no user constraint
     * @param pageable    pagination information (e.g., page number, size)
     * @param beforeDate  filter Posts with dates before this value (inclusive), or null for no constraint
     * @param afterDate   filter Posts with dates after this value (inclusive), or null for no constraint
     * @return a Page of Posts matching the applied user and date filters
     */
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

    /**
     * Searches for Posts based on a query string, scoped to a specific ApplicationUser.
     *
     * @param query  the search query string
     * @param user   the ApplicationUser to scope the search to, or null for global search
     * @return a List of Posts matching the search query
     */
    default List<Post> searchPosts(String query, ApplicationUser user) {
        return findAll(PostSearchSpecification.searchPosts(query, user));
    }

    /**
     * Searches for Posts based on a query string, date range, and scoped to a specific ApplicationUser.
     *
     * @param query     the search query string
     * @param user      the ApplicationUser to scope the search to, or null for global search
     * @param startDate the start of the date range (inclusive), or null for no start constraint
     * @param endDate   the end of the date range (inclusive), or null for no end constraint
     * @return a List of Posts matching the search query and date range
     */
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
