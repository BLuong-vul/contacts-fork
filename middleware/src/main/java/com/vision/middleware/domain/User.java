package com.vision.middleware.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
  @Id
  @Column(name = "userid", unique = true, updatable = true)
  private int userId;
  private String fullname;
  private String username;
  private String password;
  private String email;
  private String phonenumber;
  private String address;
  private String city;
  private String state;
  private String zipcode;
  private String country;
  private int followercount;
}
