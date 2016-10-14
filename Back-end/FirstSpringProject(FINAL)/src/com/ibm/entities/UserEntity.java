package com.ibm.entities;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ibm.entities.wrappers.User;

import java.util.List;

/**
 * The persistent class for the user database table.
 * 
 */
@Entity
@Table(name = "user")
@NamedQuery(name = "UserEntity.findAll", query = "SELECT u FROM UserEntity u")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class UserEntity extends User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private String email;

	private String name;

	@Column(name = "status_id")
	private int statusId;

	// bi-directional many-to-one association to Invitation
	@OneToMany(mappedBy = "user1")
	@JsonIgnore
	private List<InvitationEntity> invitations1;

	// bi-directional many-to-one association to Invitation
	@OneToMany(mappedBy = "user2")
	@JsonIgnore
	private List<InvitationEntity> invitations2;

	// bi-directional many-to-one association to Membership
	@OneToMany(mappedBy = "user")
	@JsonIgnore
	private List<MembershipEntity> memberships;

	// bi-directional many-to-one association to Message
	@OneToMany(mappedBy = "user")
	@JsonIgnore
	private List<MessageEntity> messages;

	public UserEntity() {
	}

	public UserEntity(int id, String email, String name, int statusId, List<InvitationEntity> invitations1,
			List<InvitationEntity> invitations2, List<MembershipEntity> memberships, List<MessageEntity> messages) {
		super();
		this.id = id;
		this.email = email;
		this.name = name;
		this.statusId = statusId;
		this.invitations1 = invitations1;
		this.invitations2 = invitations2;
		this.memberships = memberships;
		this.messages = messages;
	}

	public UserEntity(String email, String name, int statusId) {
		super();
		this.email = email;
		this.name = name;
		this.statusId = statusId;
	}

	public User toUser() {
		return this;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatusId() {
		return this.statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public List<InvitationEntity> getInvitations1() {
		return this.invitations1;
	}

	public void setInvitations1(List<InvitationEntity> invitations1) {
		this.invitations1 = invitations1;
	}

	public InvitationEntity addInvitations1(InvitationEntity invitations1) {
		getInvitations1().add(invitations1);
		invitations1.setUser1(this);

		return invitations1;
	}

	public InvitationEntity removeInvitations1(InvitationEntity invitations1) {
		getInvitations1().remove(invitations1);
		invitations1.setUser1(null);

		return invitations1;
	}

	public List<InvitationEntity> getInvitations2() {
		return this.invitations2;
	}

	public void setInvitations2(List<InvitationEntity> invitations2) {
		this.invitations2 = invitations2;
	}

	public InvitationEntity addInvitations2(InvitationEntity invitations2) {
		getInvitations2().add(invitations2);
		invitations2.setUser2(this);

		return invitations2;
	}

	public InvitationEntity removeInvitations2(InvitationEntity invitations2) {
		getInvitations2().remove(invitations2);
		invitations2.setUser2(null);

		return invitations2;
	}

	public List<MembershipEntity> getMemberships() {
		return this.memberships;
	}

	public void setMemberships(List<MembershipEntity> memberships) {
		this.memberships = memberships;
	}

	public MembershipEntity addMembership(MembershipEntity membership) {
		getMemberships().add(membership);
		membership.setUser(this);

		return membership;
	}

	public MembershipEntity removeMembership(MembershipEntity membership) {
		getMemberships().remove(membership);
		membership.setUser(null);

		return membership;
	}

	public List<MessageEntity> getMessages() {
		return this.messages;
	}

	public void setMessages(List<MessageEntity> messages) {
		this.messages = messages;
	}

	public MessageEntity addMessage(MessageEntity message) {
		getMessages().add(message);
		message.setUser(this);

		return message;
	}

	public MessageEntity removeMessage(MessageEntity message) {
		getMessages().remove(message);
		message.setUser(null);

		return message;
	}

}