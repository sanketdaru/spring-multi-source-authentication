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

import com.sanketdaru.multisourceauthn.service.LdapGroupsService;

@Controller
public class LdapGroupsController {
	private static final Logger LOGGER = LogManager.getLogger(LdapGroupsController.class);

	protected final HttpServletRequest request;

	@Autowired
	public LdapGroupsController(HttpServletRequest request) {
		this.request = request;
	}

	@Autowired
	LdapGroupsService ldapGroupsService;

	@GetMapping("/ous-and-groups")
	public String getAdOusAndGroupsPage(Model model) {
		LOGGER.debug("Building model for ous-and-groups...");

		addGroupsAttribute(model);

		return "ous-and-groups";
	}

	@ModelAttribute
	public void addGroupsAttribute(Model model) {
		model.addAttribute("ldapGroups", ldapGroupsService.fetchAdGroups());
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
