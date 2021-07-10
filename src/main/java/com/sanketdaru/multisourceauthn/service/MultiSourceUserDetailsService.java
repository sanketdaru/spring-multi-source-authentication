package com.sanketdaru.multisourceauthn.service;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import com.sanketdaru.multisourceauthn.config.LdapConfig;

@Service
public class MultiSourceUserDetailsService implements UserDetailsService {

	private static final Logger LOGGER = LogManager.getLogger(MultiSourceUserDetailsService.class);

	private UserDetailsService inMemoryUserDetailsService;

	private UserDetailsService ldapUserDetailsService;

	@Autowired
	LdapContextSource ldapContextSource;

	@Autowired
	LdapConfig ldapConfig;

	@PostConstruct
	public void init() {
		this.inMemoryUserDetailsService = initInMemoryUserDetailsService();
		this.ldapUserDetailsService = initLdapUserDetailsService();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (username.equalsIgnoreCase("admin@local")) {
			return inMemoryUserDetailsService.loadUserByUsername(username);
		}

		return ldapUserDetailsService.loadUserByUsername(username);
	}

	private UserDetailsService initInMemoryUserDetailsService() {
		Properties users = null;
		try {
			LOGGER.debug("Looking up from in-memory userDetailsService");
			users = PropertiesLoaderUtils.loadAllProperties("passwd.dat");
		} catch (IOException e) {
			LOGGER.error("Error while loading user details from in-memory userDetailsService.", e);
		}
		return new InMemoryUserDetailsManager(users);
	}

	private UserDetailsService initLdapUserDetailsService() {
		LdapUserSearch userSearch = new FilterBasedLdapUserSearch("", buildUserSearchFilter(), ldapContextSource);
		return new LdapUserDetailsService(userSearch);
	}

	private String buildUserSearchFilter() {
		StringBuilder builder = new StringBuilder();
		builder.append("(& ");
		builder.append("(objectclass=organizationalPerson)");
		builder.append("(sAMAccountName={0})");
		builder.append("(memberOf=");
		builder.append(ldapConfig.getAuthorizedUsersSearchBase());
		builder.append(")");
		builder.append(")");

		return builder.toString();
	}

	public void updateUserSearchFilter() {
		LdapUserSearch userSearch = new FilterBasedLdapUserSearch("", buildUserSearchFilter(), ldapContextSource);
		this.ldapUserDetailsService = new LdapUserDetailsService(userSearch);
	}
	
	public UserDetailsService getInMemoryUserDetailsService() {
		return inMemoryUserDetailsService;
	}

	public UserDetailsService getLdapUserDetailsService() {
		return ldapUserDetailsService;
	}

}
