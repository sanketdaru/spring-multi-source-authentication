package com.sanketdaru.multisourceauthn.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.sanketdaru.multisourceauthn.domain.AppLoginCredentials;

@Controller
public class BaseController {
	private static final Logger LOGGER = LogManager.getLogger(BaseController.class);
	
	protected final HttpServletRequest request;
	
	@Autowired
	public BaseController(HttpServletRequest request) {
		this.request = request;
	}
	
	@GetMapping("/")
	public String getRoot() {
		return "redirect:/home";
	}
	
	@GetMapping("/login")
	public String getLogin(@ModelAttribute(name = "appLoginCredentials") AppLoginCredentials appLoginCredentials, 
			Model model) {
		LOGGER.debug("Building model for login...");
		return "login";
	}
	
	@GetMapping("/home")
	public String getHome(Model model) {
		LOGGER.debug("Building model for home...");
		
		return "home";
	}
	
	@ModelAttribute
	public void usernameAttribute(Model model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
	    model.addAttribute("username", username);
	    if(username.endsWith("@local")) {
	    	model.addAttribute("canChangePassword", true);
	    }
	}
}
