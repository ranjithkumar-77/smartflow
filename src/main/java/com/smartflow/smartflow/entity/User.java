package com.smartflow.smartflow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
   @Column(nullable = false)
private String name;

@NotBlank
@Column(nullable = false, unique = true)
@Email
private String email;

@NotBlank
@Column(nullable = false)
private String password;

@NotBlank
@Column(nullable = false)
private String phone;

@NotBlank
@Column(nullable = false)
private String role;
}