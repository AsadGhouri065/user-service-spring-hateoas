package com.example.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.restapi.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
