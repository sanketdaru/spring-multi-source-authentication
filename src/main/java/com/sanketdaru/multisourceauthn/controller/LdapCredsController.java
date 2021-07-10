package com.sanketdaru.multisourceauthn.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.PartialResultException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.sanketdaru.multisourceauthn.domain.LdapParams;
import com.sanketdaru.multisourceauthn.service.LdapService;
import com.sanketdaru.multisourceauthn.service.LdapConfigManager;
import com.sanketdaru.multisourceauthn.service.LdapParamsManager;

@Controller
public class LdapCredsController {
	private static final Logger LOGGER = LogManager.getLogger(LdapCredsController.class);	
	
	protected final HttpServletRequest request;
	
	@Autowired
	public LdapCredsController(HttpServletRequest request) {
		this.request = request;
	}
	
	@Autowired
	LdapParamsManager ldapParamsManager;
	
	@Autowired
	LdapConfigManager ldapConfigService;
	
	@Autowired
	LdapService ldapService;
	
	@GetMapping("/ldap-creds")
	public String getLdapCreds(Model model, LdapParams ldapParams, BindingResult result) {
		LOGGER.debug("Building model for ldap-creds...");
		
		addAdLdapCredentialsAttribute(model);
		
		return "ldap-creds";
	}
	
	@PostMapping("/ldap-creds")
	public String postLdapCreds(@Valid LdapParams ldapParams, BindingResult bindingResult, Model model) {
		LOGGER.debug("Received ldap-creds...");
		
		if (bindingResult.hasErrors()) {
			return "ldap-creds";
		}

		try {
			ldapService.validateLdapAdminUser(ldapParams);
		} catch (CommunicationException ce) {
			LOGGER.error("Error while reaching LDAP server.", ce);
			bindingResult.rejectValue("ldapHost", "", "Could not connect to LDAP. Please check the host.");
			bindingResult.rejectValue("ldapPort", "", "Could not connect to LDAP. Please check the port.");
			return "ldap-creds";
		} catch(AuthenticationException ae) {
			LOGGER.error("Error while binding with LDAP server.", ae);
			bindingResult.rejectValue("ldapAdminUser", "", "Could not authenticate with LDAP. Please check the username.");
			bindingResult.rejectValue("ldapAdminPass", "", "Could not authenticate with LDAP. Please check the password.");
			return "ldap-creds";
		} catch(PartialResultException pre) {
			LOGGER.error("Error while querying LDAP server.", pre);
			bindingResult.rejectValue("ldapGroupsSearchBase", "", "Could not query LDAP. Please ensure user search base is correct.");
			return "ldap-creds";
		} catch (Exception e) {
			LOGGER.error("Error while querying LDAP.", e);
			bindingResult.rejectValue("ldapAdminPass", "", "Connection to LDAP failed.");
			return "ldap-creds";
		}

		if(!ldapParamsManager.updateLdapParams(ldapParams)) {
			return "ldap-creds";
		}
		
		ldapConfigService.refresh();
		
		return "redirect:/ous-and-groups";
	}
	
	private void addAdLdapCredentialsAttribute(Model model) {
	    model.addAttribute("ldapParams", ldapParamsManager.fetchLdapParams());
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
