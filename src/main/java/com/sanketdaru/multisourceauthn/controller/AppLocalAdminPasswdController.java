package com.sanketdaru.multisourceauthn.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.sanketdaru.multisourceauthn.domain.AppLocalCredentials;
import com.sanketdaru.multisourceauthn.service.MultiSourceUserDetailsService;

@Controller
public class AppLocalAdminPasswdController {
	private static final Logger LOGGER = LogManager.getLogger(AppLocalAdminPasswdController.class);	
	
	protected final HttpServletRequest request;
	
	@Autowired
	public AppLocalAdminPasswdController(HttpServletRequest request) {
		this.request = request;
	}
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@GetMapping("/set-password")
	public String getPasswordUpdate(
			@ModelAttribute(name = "appLocalCredentials") AppLocalCredentials appLocalCredentials,
			Model model) {
		LOGGER.debug("Building model for set-password...");
		return "set-password";
	}

	@PostMapping("/set-password")
	public String postPasswordUpdate(
			@ModelAttribute(name = "appLocalCredentials") @Valid AppLocalCredentials appLocalCredentials,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			if (!appLocalCredentials.getPassword()
					.equals(appLocalCredentials.getConfirmPassword())) {
				bindingResult.rejectValue("confirmPassword", "", "Password confirmation failed.");
			}
			return "set-password";
		}

		if (!appLocalCredentials.getPassword().equals(appLocalCredentials.getConfirmPassword())) {
			bindingResult.rejectValue("confirmPassword", "", "Password confirmation failed.");
			return "set-password";
		}

		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		if (userDetailsService instanceof MultiSourceUserDetailsService) {
			MultiSourceUserDetailsService msuds = (MultiSourceUserDetailsService) userDetailsService;
			InMemoryUserDetailsManager uds = (InMemoryUserDetailsManager) msuds.getInMemoryUserDetailsService();
			String encNewPassword = null;
			UserDetails userDetails = uds.loadUserByUsername(username);
			
			if (passwordEncoder.matches(appLocalCredentials.getExistingPassword(),
					userDetails.getPassword())) {
				encNewPassword = passwordEncoder.encode(appLocalCredentials.getPassword());
				uds.changePassword(appLocalCredentials.getExistingPassword(), encNewPassword);
			} else {
				LOGGER.error("Authentication failed while changing password.");
				bindingResult.rejectValue("existingPassword", "", "Existing password verification failed!");
				return "set-password";
			}

			File file = null;
			try {
				StringBuilder builder = new StringBuilder();
				Properties users = PropertiesLoaderUtils.loadProperties(loadPasswdResource());
				Enumeration<?> names = users.propertyNames();
				while (names.hasMoreElements()) {
					String name = (String) names.nextElement();
					builder.append(name).append("=");

					if (name.equalsIgnoreCase(username)) {
						String[] tokens = StringUtils.commaDelimitedListToStringArray(users.getProperty(name));
						builder.append(encNewPassword).append(",").append(tokens[1]);
					} else {
						builder.append(users.getProperty(name));
					}

					builder.append(System.lineSeparator());
				}
				
				file = loadPasswdResource().getFile();
				try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
					bw.write(builder.toString());
					bw.flush();
				}
			} catch (IOException e) {
				LOGGER.error("Error while working with passwd.dat file.", e);
				return "set-password";
			}
		}

		return "redirect:/home";
	}
	
	private Resource loadPasswdResource() throws IOException {
		Resource resource = resourceLoader.getResource("classpath:passwd.dat");
		LOGGER.debug("PasswdResource: {}", resource.getFile().getAbsolutePath());
		return resource;
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
