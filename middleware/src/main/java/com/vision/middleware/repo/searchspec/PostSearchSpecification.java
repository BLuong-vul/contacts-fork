package com.vision.middleware.repo.searchspec;

import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.ApplicationUser;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * Factory class for creating JPA Specifications for searching and filtering Post entities.
 */
public class PostSearchSpecification {

    /**
     * Creates a Specification to filter Posts by a date before the specified date.
     *
     * @param beforeDate the date before which Posts should be filtered
     * @return a Specification to filter Posts by the specified date
     */
    public static Specification<Post> filterByBeforeDate(Date beforeDate) {
        return (root, query, builder) -> 
            builder.lessThanOrEqualTo(root.get("datePosted"), beforeDate);
    }

    /**
     * Creates a Specification to filter Posts by a date after the specified date.
     *
     * @param afterDate the date after which Posts should be filtered
     * @return a Specification to filter Posts by the specified date
     */
    public static Specification<Post> filterByAfterDate(Date afterDate) {
        return (root, query, builder) -> 
            builder.greaterThanOrEqualTo(root.get("datePosted"), afterDate);
    }

    /**
     * Creates a Specification to filter Posts by the specified ApplicationUser.
     *
     * @param user the ApplicationUser to filter Posts by
     * @return a Specification to filter Posts by the specified user
     */
    public static Specification<Post> filterByUser(ApplicationUser user) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("postedBy"), user);
    }

    /**
     * Creates a Specification to search for Posts based on a query string and optionally,
     * an ApplicationUser. Searching is performed on the Post's title and text fields.
     *
     * @param query the search query (case-insensitive, supports wildcards)
     * @param user the ApplicationUser to filter search results by, or null for no user filter
     * @return a Specification for searching Posts by the query and user
     */
    public static Specification<Post> searchPosts(String query, ApplicationUser user) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (!StringUtils.hasText(query)) {
                return criteriaBuilder.conjunction(); // Return all posts if no query
            }

            // Normalize the query for better matching
            String normalizedQuery = "%" + query.toLowerCase().trim() + "%";

            // Create predicates for different searchable fields
            Predicate titleMatch = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("title")),
                    normalizedQuery
            );

            Predicate textMatch = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("text")),
                    normalizedQuery
            );

            Predicate userMatch = user != null ?
                    criteriaBuilder.equal(root.get("postedBy"), user) :
                    criteriaBuilder.conjunction();

            // Combine predicates with OR for fuzzy-like searching
            return criteriaBuilder.and(
                    criteriaBuilder.or(titleMatch, textMatch),
                    userMatch
            );
        };
    }

    /**
     * Creates a Specification to filter Posts within a specified date range (inclusive).
     * If either the start or end date is null, the corresponding boundary is ignored.
     *
     * @param startDate the start of the date range, or null for no start boundary
     * @param endDate the end of the date range, or null for no end boundary
     * @return a Specification to filter Posts by the specified date range
     */
    public static Specification<Post> searchByDateRange(Date startDate, Date endDate) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return criteriaBuilder.conjunction();
            }

            Predicate datePredicate = criteriaBuilder.conjunction();

            if (startDate != null) {
                datePredicate = criteriaBuilder.and(
                        datePredicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("datePosted"), startDate)
                );
            }

            if (endDate != null) {
                datePredicate = criteriaBuilder.and(
                        datePredicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("datePosted"), endDate)
                );
            }

            return datePredicate;
        };
    }
}
