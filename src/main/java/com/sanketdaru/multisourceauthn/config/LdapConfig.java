package com.sanketdaru.multisourceauthn.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
@PropertySource("classpath:ldap-config.properties")
@ConfigurationProperties(prefix = "my-ldap")
public class LdapConfig {
	private String ldapProtocol;
	private String ldapHost;
	private Integer ldapPort;
	private String ldapAuth;
	private String domainPrefix;
	private String ldapAdminUser;
	private String ldapAdminPass;
	private String ldapSearchBase;
	private String authorizedUsersSearchBase;

	@Bean
	public LdapContextSource ldapContextSource() {
		LdapContextSource ldapContextSource = new LdapContextSource();
		ldapContextSource.setUrl(ldapProtocol + "://" + ldapHost + ":" + ldapPort);
		ldapContextSource.setBase(ldapSearchBase);
		ldapContextSource.setUserDn(domainPrefix + "\\" + ldapAdminUser);
		ldapContextSource.setPassword(ldapAdminPass);
		ldapContextSource.setReferral("ignore");

		final Map<String, Object> envProps = new HashMap<>();
		envProps.put("java.naming.ldap.attributes.binary", "objectGUID");
		ldapContextSource.setBaseEnvironmentProperties(envProps);

		return ldapContextSource;
	}

	public String getLdapProtocol() {
		return ldapProtocol;
	}

	public void setLdapProtocol(String ldapProtocol) {
		this.ldapProtocol = ldapProtocol;
	}

	public String getLdapHost() {
		return ldapHost;
	}

	public void setLdapHost(String ldapHost) {
		this.ldapHost = ldapHost;
	}

	public Integer getLdapPort() {
		return ldapPort;
	}

	public void setLdapPort(Integer ldapPort) {
		this.ldapPort = ldapPort;
	}

	public String getLdapAuth() {
		return ldapAuth;
	}

	public void setLdapAuth(String ldapAuth) {
		this.ldapAuth = ldapAuth;
	}

	public String getDomainPrefix() {
		return domainPrefix;
	}

	public void setDomainPrefix(String domainPrefix) {
		this.domainPrefix = domainPrefix;
	}

	public String getLdapAdminUser() {
		return ldapAdminUser;
	}

	public void setLdapAdminUser(String ldapAdminUser) {
		this.ldapAdminUser = ldapAdminUser;
	}

	public String getLdapAdminPass() {
		return ldapAdminPass;
	}

	public void setLdapAdminPass(String ldapAdminPass) {
		this.ldapAdminPass = ldapAdminPass;
	}

	public String getLdapSearchBase() {
		return ldapSearchBase;
	}

	public void setLdapSearchBase(String ldapSearchBase) {
		this.ldapSearchBase = ldapSearchBase;
	}

	public String getAuthorizedUsersSearchBase() {
		return authorizedUsersSearchBase;
	}

	public void setAuthorizedUsersSearchBase(String authorizedUsersSearchBase) {
		this.authorizedUsersSearchBase = authorizedUsersSearchBase;
	}
}
