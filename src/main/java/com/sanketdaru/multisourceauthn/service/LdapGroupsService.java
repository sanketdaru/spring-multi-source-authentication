package com.sanketdaru.multisourceauthn.service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.stereotype.Service;

import com.sanketdaru.multisourceauthn.domain.LdapGroup;
import com.sanketdaru.multisourceauthn.domain.LdapGroupType;

@Service
public class LdapGroupsService extends LdapService {
	private static final Logger LOGGER = LogManager.getLogger(LdapGroupsService.class);
	
	@Autowired
	LdapContextSource ldapContextSource;
	
	public List<LdapGroup> fetchAdGroups() {
		LdapQuery query = LdapQueryBuilder.query()
				.searchScope(SearchScope.SUBTREE)
				.attributes("name", "distinguishedName", "objectGUID", "whenCreated", "whenChanged", "objectClass")
				.where("objectClass").is("organizationalUnit")
				.or("objectClass").is("group")
				.and(LdapQueryBuilder.query().where("isCriticalSystemObject").not().isPresent());

		LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource);
		ldapTemplate.setIgnorePartialResultException(true);
		
		Map<String, LdapGroup> mapOfGroups = new HashMap<String, LdapGroup>();
		
		List<LdapGroup> adGroups = ldapTemplate.search(query, new AttributesMapper<LdapGroup>() {
			public LdapGroup mapFromAttributes(Attributes attrs) throws NamingException {
				LdapGroup group = buildADGroup(attrs);
				mapOfGroups.put(group.getDn(), group);
				return group;
			}
		});

		LOGGER.debug("Received {} groups.", adGroups.size());
		
		adGroups.forEach(group -> {
			LdapGroup parent = mapOfGroups.get(group.getDn().substring(group.getDn().indexOf(",") + 1, group.getDn().length()));
			if(null != parent) {
				group.setParentDn(parent.getDn());
				parent.getSubGroups().add(group);
			}
		});
		
		return adGroups;
	}

	protected LdapGroup buildADGroup(Attributes attrs) throws NamingException {
		LdapGroup adGroup = new LdapGroup();
		adGroup.setName(fetchAttrValue(attrs, "name"));
		adGroup.setDn(fetchAttrValue(attrs, "distinguishedName"));
		adGroup.setGuid(decodeGuid((byte[]) attrs.get("objectGUID").get()));
		
		Instant iCreatedAt = fetchAttrValue(attrs, "whenCreated") == null ? null : OffsetDateTime.parse(fetchAttrValue(attrs, "whenCreated") , dateTimeFormatter).toInstant();
		adGroup.setCreatedAt(iCreatedAt);
		
		Instant iUpdatedAt = fetchAttrValue(attrs, "whenChanged") == null ? null : OffsetDateTime.parse(fetchAttrValue(attrs, "whenChanged") , dateTimeFormatter).toInstant();
		adGroup.setUpdatedAt(iUpdatedAt);
		
		NamingEnumeration<?> enumeration = attrs.get("objectClass").getAll();
		while(enumeration.hasMoreElements()) {
			if(enumeration.nextElement().equals("organizationalUnit")) {
				adGroup.setType(LdapGroupType.organizationalUnit);
				break;
			}
		}
		
		if(null == adGroup.getType()) {
			adGroup.setType(LdapGroupType.securityGroup);
		}
		
		return adGroup;
	}
}
