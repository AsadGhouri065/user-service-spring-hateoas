package com.example.restapi.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.example.restapi.model.User;
import com.example.restapi.service.UserService;

@RestController
@RequestMapping(value = "/api/v1")
public class UserController {

	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Gets all the users in a list.
	 * 
	 * @return the users list
	 */
	@GetMapping(value = "/users")
	ResponseEntity<List<EntityModel<User>>> get() {

		List<EntityModel<User>> user = userService.getAllUsers().stream()
				.map(users -> EntityModel.of(users,
						linkTo(methodOn(UserController.class).one(users.getId())).withSelfRel(),
						linkTo(methodOn(UserController.class).get()).withRel("users")))
				.limit(5).collect(Collectors.toList());

		if (user.isEmpty()) {
			throw new EntityNotFoundException("No users found");
		} else {
			return new ResponseEntity<List<EntityModel<User>>>(user, HttpStatus.OK);
		}
	}

	/**
	 * Gets a single user if its present
	 * 
	 * @param userId specific user to find
	 * @return found user in the database with FOUND status
	 */
	@GetMapping(value = "/users/{id}")
	ResponseEntity<EntityModel<User>> one(@PathVariable Long id) {

		User user = userService.getUserById(id).orElseThrow(() -> new EntityNotFoundException("Record not found."));

		return new ResponseEntity<EntityModel<User>>(
				EntityModel.of(user, linkTo(methodOn(UserController.class).one(id)).withSelfRel(),
						linkTo(methodOn(UserController.class).get()).withRel("users")),
				HttpStatus.OK);
	}

	/**
	 * Creates a user in the database
	 * 
	 * @param user model to be stored in the db
	 * @return saved user with CREATED status
	 */
	@PostMapping(value = "/users")
	ResponseEntity<EntityModel<User>> createUser(@RequestBody User user) {

		User createdUser = userService.createUser(user);

		if (createdUser != null) {
			return new ResponseEntity<EntityModel<User>>(
					EntityModel.of(createdUser,
							linkTo(methodOn(UserController.class).one(createdUser.getId())).withSelfRel()),
					HttpStatus.CREATED);
		} else {
			return new ResponseEntity<EntityModel<User>>(HttpStatus.BAD_GATEWAY);
		}

	}

	/**
	 * finds the user if its present and updates it
	 * 
	 * @param id   for the user to be searched
	 * @param user data to be updated
	 * @return user with updated data
	 */
	@PutMapping(value = "/users/{id}")
	EntityModel<User> updateUser(@PathVariable(value = "id") Long id, @RequestBody User user) {

		Optional<User> isUserFound = userService.getUserById(id);

		if (isUserFound.isPresent()) {
			user.setId(isUserFound.get().getId()); // setting the id of found user into the new user data
			userService.createUser(user); // saving the new user data into the database
		}
		return EntityModel.of(user, linkTo(methodOn(UserController.class).one(user.getId())).withSelfRel());
	}

	/**
	 * deletes the given id user
	 * 
	 * @param id (user id to be deleted)
	 * @return user model that got deleted
	 */
	@DeleteMapping(value = "/users/{id}")
	ResponseEntity<User> deleteUser(@PathVariable(value = "id") Long id) {
		Optional<User> userFound = userService.getUserById(id);

		if (userFound.isPresent()) {
			userService.deleteUser(id);
			return new ResponseEntity<User>(userFound.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}
	}
}
