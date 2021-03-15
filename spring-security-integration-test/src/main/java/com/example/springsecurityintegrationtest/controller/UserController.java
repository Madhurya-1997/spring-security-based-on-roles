package com.example.springsecurityintegrationtest.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springsecurityintegrationtest.common.UserConstants;
import com.example.springsecurityintegrationtest.model.User;
import com.example.springsecurityintegrationtest.repository.UserRepository;

@RestController
@ComponentScan("com.example.springsecurityintegrationtest")
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@GetMapping("/")
	public String home() {
		return ("<h1>Welcome</h1>");
	}
	
	@PostMapping("/add")
	public String addUser(@RequestBody User user) {
		user.setRoles(UserConstants.DEFAULT_ROLE);
		String encodedPwd = encoder.encode(user.getPassword());
		user.setPassword(encodedPwd);
		repository.save(user);
		return "User saved, added as a User";
	}

	@GetMapping("/access/{userId}/{userRole}") //only admin and mod can access this api
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
	public String giveAccessToUser(@PathVariable int userId, @PathVariable String userRole, Principal principal) {
		User user = repository.findById(userId).get();
		List<String> activeRoles = getRolesByLoggedInUser(principal);
		String newRoles = "";
		if(activeRoles.contains(userRole)) {
			newRoles = user.getRoles() + "," + userRole;
			user.setRoles(newRoles);
		}
		repository.save(user);
		return "You now have access to the group as " + userRole;
	}
	
	@GetMapping("/loadusers")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public List<User> loadUsers() {
		return repository.findAll();
	}
	
	@GetMapping("/test")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String testUserCase() {
		return "For users only !!";
	}
	
	private User getLoggedInUser(Principal principal) {
		return repository.findByUsername(principal.getName());
	}
	
	private List<String> getRolesByLoggedInUser(Principal principal) {
		User user = getLoggedInUser(principal);
		List<String> assignRoles = Arrays.stream(user.getRoles().split(",")).collect(Collectors.toList());
		
		if(assignRoles.contains("ROLE_ADMIN")) {
			return Arrays.stream(UserConstants.ADMIN_ACCESS).collect(Collectors.toList());
		} else if (assignRoles.contains("ROLE_MODERATOR")) {
			return Arrays.stream(UserConstants.MODERATOR_ACCESS).collect(Collectors.toList());
		} else {
			return Collections.emptyList();			
		}
	}

}
