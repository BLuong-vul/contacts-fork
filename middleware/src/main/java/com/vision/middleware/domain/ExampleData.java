package com.vision.middleware.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

/**
 * Entity representing data in the 'sometable' table.
 */
@Entity
@Table(name = "sometable")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExampleData {
    /**
     * Unique identifier for the entity, generated as a UUID.
     */
    @Id
    @UuidGenerator
    @Column(name = "id", unique = true, updatable = false)
    private String id;

    /**
     * Name associated with the entity.
     */
    private String name;

    /**
     * Social Security Number associated with the entity.
     */
    private String ssn;

    /**
     * Credit card number associated with the entity.
     */
    @Column(name = "credit_card_number")
    private String creditCardNumber;
}
