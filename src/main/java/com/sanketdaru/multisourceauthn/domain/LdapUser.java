package com.sanketdaru.multisourceauthn.domain;

import java.io.Serializable;

public class LdapUser implements Serializable {

	private static final long serialVersionUID = 1457450946309007506L;

	private String firstName;
	private String lastName;
	private String principalName;
	private String logonName;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPrincipalName() {
		return principalName;
	}

	public void setPrincipalName(String principalName) {
		this.principalName = principalName;
	}

	public String getLogonName() {
		return logonName;
	}

	public void setLogonName(String logonName) {
		this.logonName = logonName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LdapUser [firstName=").append(firstName).append(", lastName=").append(lastName)
				.append(", principalName=").append(principalName).append(", logonName=").append(logonName).append("]");
		return builder.toString();
	}

}
