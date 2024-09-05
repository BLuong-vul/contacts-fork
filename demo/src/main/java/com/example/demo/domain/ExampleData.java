package com.example.demo.domain;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
  private String someString;
}
