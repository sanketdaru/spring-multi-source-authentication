package com.sanketdaru.multisourceauthn.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import com.sanketdaru.multisourceauthn.config.LdapConfig;
import com.sanketdaru.multisourceauthn.domain.LdapParams;

@Service
public class LdapParamsManager {

	private static final Logger LOGGER = LogManager.getLogger(LdapParamsManager.class);
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	LdapConfig ldapConfig;
	
	public LdapParams fetchLdapParams() {
		LdapParams ldapParams = new LdapParams();
		ldapParams.setLdapProtocol(ldapConfig.getLdapProtocol());
		ldapParams.setLdapHost(ldapConfig.getLdapHost());
		ldapParams.setLdapPort(ldapConfig.getLdapPort());
		ldapParams.setDomainPrefix(ldapConfig.getDomainPrefix());
		ldapParams.setLdapAdminUser(ldapConfig.getLdapAdminUser());
		ldapParams.setLdapAdminPass(ldapConfig.getLdapAdminPass());
		ldapParams.setLdapSearchBase(ldapConfig.getLdapSearchBase());
		ldapParams.setAuthorizedUsersSearchBase(ldapConfig.getAuthorizedUsersSearchBase());
		
		return ldapParams;
	}
	
	public boolean updateLdapParams(LdapParams ldapParams) {
		File file = null;
		try {
			StringBuilder builder = new StringBuilder();
			Properties ldapProperties = PropertiesLoaderUtils.loadProperties(fetchLdapConfigResource());
			Enumeration<?> names = ldapProperties.propertyNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				builder.append(name).append("=");

				switch (name) {
				case "my-ldap.ldapProtocol":
					builder.append(ldapProperties.getProperty(name));
					ldapConfig.setLdapProtocol(ldapProperties.getProperty(name));
					break;
				case "my-ldap.ldapHost":
					builder.append(ldapParams.getLdapHost());
					ldapConfig.setLdapHost(ldapParams.getLdapHost());
					break;
				case "my-ldap.ldapPort":
					builder.append(ldapParams.getLdapPort());
					ldapConfig.setLdapPort(ldapParams.getLdapPort());
					break;
				case "my-ldap.ldapAuth":
					builder.append(ldapProperties.getProperty(name));
					ldapConfig.setLdapAuth(ldapProperties.getProperty(name));
					break;
				case "my-ldap.domainPrefix":
					builder.append(ldapParams.getDomainPrefix());
					ldapConfig.setDomainPrefix(ldapParams.getDomainPrefix());
					break;
				case "my-ldap.ldapAdminUser":
					builder.append(ldapParams.getLdapAdminUser());
					ldapConfig.setLdapAdminUser(ldapParams.getLdapAdminUser());
					break;
				case "my-ldap.ldapAdminPass":
					builder.append(ldapParams.getLdapAdminPass());
					ldapConfig.setLdapAdminPass(ldapParams.getLdapAdminPass());
					break;
				case "my-ldap.ldapSearchBase":
					builder.append(ldapParams.getLdapSearchBase());
					ldapConfig.setLdapSearchBase(ldapParams.getLdapSearchBase());
					break;
				case "my-ldap.authorizedUsersSearchBase":
					builder.append(ldapParams.getAuthorizedUsersSearchBase());
					ldapConfig.setAuthorizedUsersSearchBase(ldapParams.getAuthorizedUsersSearchBase());
					break;
				default:
					builder.delete(builder.length() - name.length(), builder.length());
				}

				builder.append(System.lineSeparator());
			}

			file = fetchLdapConfigResource().getFile();
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
				bw.write(builder.toString());
				bw.flush();
			}
		} catch (IOException e) {
			LOGGER.error("Error while working with ldap-config.properties file.", e);
			return false;
		}
		return true;
	}
	
	private Resource fetchLdapConfigResource() throws IOException {
		Resource resource = resourceLoader.getResource("classpath:ldap-config.properties");
		LOGGER.debug("LdapConfigResource: {}", resource.getFile().getAbsolutePath());
		return resource;
	}
}
