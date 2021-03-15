package com.example.springsecurityintegrationtest.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.springsecurityintegrationtest.model.MyUserDetails;
import com.example.springsecurityintegrationtest.model.User;
import com.example.springsecurityintegrationtest.repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService{

	@Autowired
	UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		Optional<User> optionalUser = Optional.of(user);
		optionalUser.orElseThrow(() -> new UsernameNotFoundException("Not Found: " + username));
		return new MyUserDetails(user);
	}

}
