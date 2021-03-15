package com.example.springsecurityintegrationtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.springsecurityintegrationtest.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
}
