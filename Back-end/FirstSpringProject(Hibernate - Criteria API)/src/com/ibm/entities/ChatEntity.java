package com.ibm.entities;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ibm.other.Chat;

import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the chat_room database table.
 * 
 */
@Entity
@Table(name="chat_room")
@NamedQuery(name="ChatEntity.findAll", query="SELECT c FROM ChatEntity c")
public class ChatEntity extends Chat implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Column(name="create_tms")
	private Timestamp createTms;

	@Column(name="creator_id")
	private int creatorId;

	private String name;

	private String summary;

	@Column(name="type_id")
	private int typeId;

	@Column(name="update_tms")
	private Timestamp updateTms;

	//bi-directional many-to-one association to Invitation
	@OneToMany(mappedBy="chatEntity")
	@JsonIgnore
	private List<InvitationEntity> invitations;

	//bi-directional many-to-one association to Membership
	@OneToMany(mappedBy="chatEntity")
	@JsonIgnore
	private List<MembershipEntity> memberships;

	//bi-directional many-to-one association to Message
	@OneToMany(mappedBy="chatEntity")
	@JsonIgnore
	private List<MessageEntity> messages;

	public ChatEntity() {
	}
	
	public ChatEntity(int id, Timestamp createTms, int creatorId, String name, String summary, int typeId,
			Timestamp updateTms, List<InvitationEntity> invitations, List<MembershipEntity> memberships,
			List<MessageEntity> messages) {
		super();
		this.id = id;
		this.createTms = createTms;
		this.creatorId = creatorId;
		this.name = name;
		this.summary = summary;
		this.typeId = typeId;
		this.updateTms = updateTms;
		this.invitations = invitations;
		this.memberships = memberships;
		this.messages = messages;
	}

	public ChatEntity(Timestamp createTms, int creatorId, String name, String summary, int typeId,
			Timestamp updateTms) {
		super();
		this.createTms = createTms;
		this.creatorId = creatorId;
		this.name = name;
		this.summary = summary;
		this.typeId = typeId;
		this.updateTms = updateTms;
	}
	
	public Chat toChat() {
		return this;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Timestamp getCreateTms() {
		return this.createTms;
	}

	public void setCreateTms(Timestamp createTms) {
		this.createTms = createTms;
	}

	public int getCreatorId() {
		return this.creatorId;
	}

	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public int getTypeId() {
		return this.typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public Timestamp getUpdateTms() {
		return this.updateTms;
	}

	public void setUpdateTms(Timestamp updateTms) {
		this.updateTms = updateTms;
	}

	public List<InvitationEntity> getInvitations() {
		return this.invitations;
	}

	public void setInvitations(List<InvitationEntity> invitations) {
		this.invitations = invitations;
	}

	public InvitationEntity addInvitation(InvitationEntity invitation) {
		getInvitations().add(invitation);
		invitation.setChatEntity(this);

		return invitation;
	}

	public InvitationEntity removeInvitation(InvitationEntity invitation) {
		getInvitations().remove(invitation);
		invitation.setChatEntity(null);

		return invitation;
	}

	public List<MembershipEntity> getMemberships() {
		return this.memberships;
	}

	public void setMemberships(List<MembershipEntity> memberships) {
		this.memberships = memberships;
	}

	public MembershipEntity addMembership(MembershipEntity membership) {
		getMemberships().add(membership);
		membership.setChatEntity(this);

		return membership;
	}

	public MembershipEntity removeMembership(MembershipEntity membership) {
		getMemberships().remove(membership);
		membership.setChatEntity(null);

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
		message.setChatEntity(this);

		return message;
	}

	public MessageEntity removeMessage(MessageEntity message) {
		getMessages().remove(message);
		message.setChatEntity(null);

		return message;
	}

}