package com.vision.middleware.repo.searchspec;

import com.vision.middleware.domain.Post;
import com.vision.middleware.domain.ApplicationUser;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.Date;

public class PostSearchSpecification {

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