package com.sanketdaru.multisourceauthn.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.stereotype.Service;

import com.sanketdaru.multisourceauthn.domain.LdapParams;

@Primary
@Service
public class LdapService {
	private static final Logger LOGGER = LogManager.getLogger(LdapService.class);

	protected DateTimeFormatter dateTimeFormatter;
	
	@Autowired
	LdapConfigManager ldapConfigManager;
	
	public LdapService() {
		dateTimeFormatter = DateTimeFormatter.ofPattern ( "uuuuMMddHHmmss[,S][.S]X" );
	}
	
	protected String fetchAttrValue(Attributes attrs, String attrName) throws NamingException {
		if (null != attrs.get(attrName)) {
			return attrs.get(attrName).get().toString();
		}
		return null;
	}

	protected String decodeGuid(byte[] bytes) {
		if (bytes != null && bytes.length == 16) {
            long msb = bytes[3] & 0xFF;
            msb = msb << 8 | (bytes[2] & 0xFF);
            msb = msb << 8 | (bytes[1] & 0xFF);
            msb = msb << 8 | (bytes[0] & 0xFF);

            msb = msb << 8 | (bytes[5] & 0xFF);
            msb = msb << 8 | (bytes[4] & 0xFF);

            msb = msb << 8 | (bytes[7] & 0xFF);
            msb = msb << 8 | (bytes[6] & 0xFF);

            long lsb = bytes[8] & 0xFF;
            lsb = lsb << 8 | (bytes[9] & 0xFF);
            lsb = lsb << 8 | (bytes[10] & 0xFF);
            lsb = lsb << 8 | (bytes[11] & 0xFF);
            lsb = lsb << 8 | (bytes[12] & 0xFF);
            lsb = lsb << 8 | (bytes[13] & 0xFF);
            lsb = lsb << 8 | (bytes[14] & 0xFF);
            lsb = lsb << 8 | (bytes[15] & 0xFF);

            return new UUID(msb, lsb).toString();
        }
		
		return null;
	}
	
	public void validateLdapAdminUser(LdapParams ldapParams) {
		LdapQuery query = LdapQueryBuilder.query()
				.searchScope(SearchScope.SUBTREE)
				.attributes("distinguishedName")
				.where("objectclass").is("organizationalPerson")
				.and("isCriticalSystemObject").not().isPresent()
				.and("sAMAccountName").is(ldapParams.getLdapAdminUser());

		LdapTemplate ldapTemplate = ldapConfigManager.ldapTemplate(ldapParams);
		ldapTemplate.setIgnorePartialResultException(true);
		
		List<String> userDn = ldapTemplate.search(query, new AttributesMapper<String>() {
			public String mapFromAttributes(Attributes attrs) throws NamingException {
				return fetchAttrValue(attrs, "distinguishedName");
			}
		});

		if(null != userDn && userDn.size() == 1) {
			// No need to bind again. 
			// To fetch userDn a bind was already performed successfully using the supplied creds! 
			LOGGER.info("Admin user {} credentials verified successfully. Admin user DN={}", ldapParams.getLdapAdminUser(), userDn.get(0));
		}
	}
}
