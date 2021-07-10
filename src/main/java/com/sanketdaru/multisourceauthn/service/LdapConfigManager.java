package com.sanketdaru.multisourceauthn.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Service;

import com.sanketdaru.multisourceauthn.domain.LdapParams;

@Service
public class LdapConfigManager {
	
	@Autowired
	LdapContextSource ldapContextSource;
	
	@Autowired
	LdapParamsManager ldapParamsManager;
	
	@Autowired
	MultiSourceUserDetailsService msuds;
	
	@Autowired
	MultiSourceAuthenticationProvider msap;
	
	public void refresh() {
		LdapParams ldapParams = ldapParamsManager.fetchLdapParams();
		buildLdapContextSource(ldapParams, this.ldapContextSource);
		msuds.updateUserSearchFilter();
		msap.updateUserSearchFilter();
	}
	
	public LdapContextSource getLdapContextSource(LdapParams ldapParams) {
		return buildLdapContextSource(ldapParams, null);
	}
	
	public LdapTemplate ldapTemplate(LdapParams ldapParams) {
		return new LdapTemplate(buildLdapContextSource(ldapParams, null));
	}

	private LdapContextSource buildLdapContextSource(LdapParams ldapParams, LdapContextSource ldapContextSource) {
		if (null == ldapContextSource) {
			ldapContextSource = new LdapContextSource();
		}

		ldapContextSource.setUrl(
				ldapParams.getLdapProtocol() + "://" + ldapParams.getLdapHost() + ":" + ldapParams.getLdapPort());
		ldapContextSource.setBase(ldapParams.getLdapSearchBase());
		ldapContextSource.setUserDn(ldapParams.getDomainPrefix() + "\\" + ldapParams.getLdapAdminUser());
		ldapContextSource.setPassword(ldapParams.getLdapAdminPass());
		ldapContextSource.setReferral("ignore");

		final Map<String, Object> envProps = new HashMap<>();
		envProps.put("java.naming.ldap.attributes.binary", "objectGUID");
		ldapContextSource.setBaseEnvironmentProperties(envProps);

		ldapContextSource.afterPropertiesSet();

		return ldapContextSource;
	}

}
