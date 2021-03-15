package com.example.springsecurityintegrationtest.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springsecurityintegrationtest.model.Post;
import com.example.springsecurityintegrationtest.model.PostStatus;
import com.example.springsecurityintegrationtest.repository.PostRepository;

@RestController
@RequestMapping("/post")
public class PostController {

	@Autowired
	PostRepository repository;
	
	@PostMapping("/create") // accessed by admin/moderator/user
	public String createPost(@RequestBody Post post, Principal principal) {
		post.setStatus(PostStatus.PENDING);
		post.setUsername(principal.getName());
		repository.save(post);
		return "Your post is created successfully, wait for ADMIN/MODERATOR approval";
	}
	
	@GetMapping("/approvepost/{postId}") // by admin/mod
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
	public String approvePost(@PathVariable int postId) {
		Post post = repository.findById(postId).get();
		post.setStatus(PostStatus.APPROVED);
		repository.save(post);
		return "Post has been approved";
	}
	
	@GetMapping("/approveallpost") // by admin/mod
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
	public String approveAllPendingPost() {
		repository.findAll().stream()
		.filter(post -> post.getStatus().equals(PostStatus.PENDING))
		.forEach(post -> {
			post.setStatus(PostStatus.APPROVED);
			repository.save(post);
		});
		
		return "All posts are approved";
	}
	
	@GetMapping("/rejectpost/{postId}") // by admin/mod
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
	public String rejectPost(@PathVariable int postId) {
		Post post = repository.findById(postId).get();
		post.setStatus(PostStatus.REJECTED);
		repository.save(post);
		return "Post has been rejected";
	}
	
	@GetMapping("/rejectallpost") // by admin/mod
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
	public String rejectAllPendingPost() {
		repository.findAll().stream()
		.filter(post -> post.getStatus().equals(PostStatus.PENDING))
		.forEach(post -> {
			post.setStatus(PostStatus.REJECTED);
			repository.save(post);
		});
		
		return "All posts are rejected";
	}
	
	@GetMapping("/viewposts")
	public List<Post> viewAllApprovedPosts() {
		return repository.findAll().stream()
		.filter(post -> post.getStatus().equals(PostStatus.APPROVED)).collect(Collectors.toList());
	}
}
