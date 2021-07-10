package com.sanketdaru.multisourceauthn.service;

import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.stereotype.Service;

import com.sanketdaru.multisourceauthn.domain.LdapUser;

@Service
public class LdapUsersService extends LdapService {

	@Autowired
	LdapContextSource ldapContextSource;
	
	public List<LdapUser> fetchAdUsers(String userSearchBase, boolean entireSubTree) {
		LdapQuery query = LdapQueryBuilder.query()
				.base(userSearchBase)
				.searchScope(SearchScope.SUBTREE)
				.attributes("sn", "givenName", "userPrincipalName", "sAMAccountName")
				.where("objectclass")
					.is("organizationalPerson")
					.and("isCriticalSystemObject").not().isPresent();

		return fetchAdUsers(query);
	}
	
	public List<LdapUser> fetchAdUsers() {
		LdapQuery query = LdapQueryBuilder.query()
				.searchScope(SearchScope.ONELEVEL)
				.attributes("sn", "givenName", "userPrincipalName", "sAMAccountName")
				.where("objectclass")
					.is("organizationalPerson")
					.and("isCriticalSystemObject").not().isPresent();
		
		return fetchAdUsers(query);
	}
	
	private List<LdapUser> fetchAdUsers(LdapQuery query) {
		LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource);
		ldapTemplate.setIgnorePartialResultException(true);
		
		List<LdapUser> adUsers = ldapTemplate.search(query, new AttributesMapper<LdapUser>() {
			public LdapUser mapFromAttributes(Attributes attrs) throws NamingException {
				return buildADUser(attrs);
			}
		});

		return adUsers;
	}

	private LdapUser buildADUser(Attributes attrs) throws NamingException {
		LdapUser adUser = new LdapUser();
		adUser.setFirstName(fetchAttrValue(attrs, "givenName"));
		adUser.setLastName(fetchAttrValue(attrs, "sn"));
		adUser.setPrincipalName(fetchAttrValue(attrs, "userPrincipalName"));
		adUser.setLogonName(fetchAttrValue(attrs, "sAMAccountName"));

		return adUser;
	}
}
