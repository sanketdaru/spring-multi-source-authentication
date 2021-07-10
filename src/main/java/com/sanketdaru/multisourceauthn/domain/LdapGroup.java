package com.sanketdaru.multisourceauthn.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class LdapGroup implements Serializable {

	private static final long serialVersionUID = -6449429696754077392L;

	private LdapGroupType type;
	private String name;
	private String dn;
	private String parentDn;
	private String guid;
	private Instant createdAt;
	private Instant updatedAt;
	private List<LdapGroup> subGroups;

	public LdapGroup() {
		this.subGroups = new ArrayList<LdapGroup>();
	}

	public LdapGroupType getType() {
		return type;
	}

	public void setType(LdapGroupType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getParentDn() {
		return parentDn;
	}

	public void setParentDn(String parentDn) {
		this.parentDn = parentDn;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<LdapGroup> getSubGroups() {
		return subGroups;
	}

	public void setSubGroups(List<LdapGroup> subGroups) {
		this.subGroups = subGroups;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LdapGroup [type=").append(type).append(", name=").append(name).append(", dn=").append(dn)
				.append(", parentDn=").append(parentDn).append(", guid=").append(guid).append(", createdAt=")
				.append(createdAt).append(", updatedAt=").append(updatedAt).append(", subGroups=").append(subGroups)
				.append("]");
		return builder.toString();
	}

}
