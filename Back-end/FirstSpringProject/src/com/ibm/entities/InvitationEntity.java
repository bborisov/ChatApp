package com.ibm.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ibm.entities.wrappers.Invitation;

/**
 * The persistent class for the invitation database table.
 * 
 */
@Entity
@Table(name = "invitation")
@NamedQuery(name = "InvitationEntity.findAll", query = "SELECT i FROM InvitationEntity i")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class InvitationEntity extends Invitation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "status_id")
	private int statusId;

	// bi-directional many-to-one association to ChatEntity
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "chat_room_id", insertable = false, updatable = false)
	private ChatEntity chatEntity;

	@Column(name = "chat_room_id")
	private int chatId;

	// bi-directional many-to-one association to User
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private UserEntity user1;

	@Column(name = "user_id")
	private int userId;

	// bi-directional many-to-one association to User
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name = "invitor_id", insertable = false, updatable = false)
	private UserEntity user2;

	@Column(name = "invitor_id")
	private int invitorId;

	public InvitationEntity() {
	}

	public InvitationEntity(int id, int statusId, ChatEntity chatEntity, int chatId, UserEntity user1, int userId,
			UserEntity user2, int invitorId) {
		super();
		this.id = id;
		this.statusId = statusId;
		this.chatEntity = chatEntity;
		this.chatId = chatId;
		this.user1 = user1;
		this.userId = userId;
		this.user2 = user2;
		this.invitorId = invitorId;
	}

	public InvitationEntity(int statusId, int chatId, int userId, int invitorId) {
		super();
		this.statusId = statusId;
		this.chatId = chatId;
		this.userId = userId;
		this.invitorId = invitorId;
	}

	public Invitation toInvitation() {
		return this;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public ChatEntity getChatEntity() {
		return chatEntity;
	}

	public void setChatEntity(ChatEntity chatEntity) {
		this.chatEntity = chatEntity;
	}

	public int getChatId() {
		return chatId;
	}

	public void setChatId(int chatId) {
		this.chatId = chatId;
	}

	public UserEntity getUser1() {
		return user1;
	}

	public void setUser1(UserEntity user1) {
		this.user1 = user1;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public UserEntity getUser2() {
		return user2;
	}

	public void setUser2(UserEntity user2) {
		this.user2 = user2;
	}

	public int getInvitorId() {
		return invitorId;
	}

	public void setInvitorId(int invitorId) {
		this.invitorId = invitorId;
	}

}