package com.sanketdaru.multisourceauthn.domain;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

public class LdapParams {

	private String ldapProtocol;

	@NotEmpty
	private String ldapHost;

	@Positive
	private Integer ldapPort;

	@NotEmpty
	private String domainPrefix;

	@NotEmpty
	private String ldapAdminUser;

	@NotEmpty
	private String ldapAdminPass;

	@NotEmpty
	private String ldapSearchBase;

	@NotEmpty
	private String authorizedUsersSearchBase;

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LdapParams [ldapProtocol=").append(ldapProtocol).append(", ldapHost=").append(ldapHost)
				.append(", ldapPort=").append(ldapPort).append(", domainPrefix=").append(domainPrefix)
				.append(", ldapAdminUser=").append(ldapAdminUser).append(", ldapAdminPass=").append("********")
				.append(", ldapSearchBase=").append(ldapSearchBase).append(", authorizedUsersSearchBase=")
				.append(authorizedUsersSearchBase).append("]");
		return builder.toString();
	}
}
