package com.sanketdaru.multisourceauthn.domain;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class AppLocalCredentials {

	@NotEmpty
	private String existingPassword;

	@NotEmpty
	@Pattern(regexp = "^(?=\\P{Ll}*\\p{Ll})(?=\\P{Lu}*\\p{Lu})(?=\\P{N}*\\p{N})(?=[\\p{L}\\p{N}]*[^\\p{L}\\p{N}])[\\s\\S]{10,}$")
	// REF: https://stackoverflow.com/a/48346033
	private String password;

	@NotEmpty
	private String confirmPassword;

	public String getExistingPassword() {
		return existingPassword;
	}

	public void setExistingPassword(String existingPassword) {
		this.existingPassword = existingPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

}
