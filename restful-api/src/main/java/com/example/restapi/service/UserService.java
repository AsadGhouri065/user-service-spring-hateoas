package com.example.restapi.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.restapi.model.User;
import com.example.restapi.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Transactional
	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	@Transactional
	public User createUser(User user) {
		return userRepository.save(user);
	}

	@Transactional
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}

	public User updateUser(User user) {
		return userRepository.save(user);
	}

}
