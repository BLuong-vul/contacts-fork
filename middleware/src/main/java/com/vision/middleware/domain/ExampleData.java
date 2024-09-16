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

@Entity
@Table(name = "sometable")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExampleData {
  @Id
  @UuidGenerator
  @Column(name = "id", unique = true, updatable = true)
  private String id;
  private String name;
  private String ssn;
  private String credit_card_number;
}
