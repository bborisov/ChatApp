package com.ibm.controllers;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.dto.UserCreateDto;
import com.ibm.entities.wrappers.User;
import com.ibm.services.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	@MessageMapping(value = "/createUser")
	public void createUser(UserCreateDto userCreateDto, Principal principal) throws IOException {
		User user = null;
		try {
			user = userService.createUser(userCreateDto);
		} finally {
			messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/users/createUser",
					(user == null) ? false : user);
		}
	}

	@MessageMapping(value = "/login")
	public void login(String email, Principal principal) throws IOException {
		User user = null;
		try {
			user = userService.login(email);
		} finally {
			messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/users/login",
					(user == null) ? false : user);
		}
	}

	@MessageMapping(value = "/getAllUsers")
	public void getAllUsers(Principal principal) throws IOException {
		messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/getAllUsers", userService.getAllUsers());
	}

}
