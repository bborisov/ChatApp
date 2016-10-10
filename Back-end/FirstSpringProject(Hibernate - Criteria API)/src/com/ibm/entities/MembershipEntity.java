package com.ibm.entities;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ibm.other.Membership;

/**
 * The persistent class for the membership database table.
 * 
 */
@Entity
@Table(name="membership")
@NamedQuery(name="MembershipEntity.findAll", query="SELECT m FROM MembershipEntity m")
public class MembershipEntity extends Membership implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Column(name="role_id")
	private int roleId;

	@Column(name="status_id")
	private int statusId;

	//bi-directional many-to-one association to ChatEntity
	@ManyToOne(fetch=FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name="chat_room_id", insertable = false, updatable = false)
	private ChatEntity chatEntity;
	
	@Column(name="chat_room_id")
	private int chatId;

	//bi-directional many-to-one association to User
	@ManyToOne(fetch=FetchType.LAZY)
	@JsonIgnore
	@JoinColumn(name="user_id", insertable = false, updatable = false)
	private UserEntity user;
	
	@Column(name="user_id")
	private int userId;

	public MembershipEntity() {
	}

	public MembershipEntity(int id, int roleId, int statusId, ChatEntity chatEntity, Integer chatId, UserEntity user,
			Integer userId) {
		super();
		this.id = id;
		this.roleId = roleId;
		this.statusId = statusId;
		this.chatEntity = chatEntity;
		this.chatId = chatId;
		this.user = user;
		this.userId = userId;
	}
	
	public MembershipEntity(int roleId, int statusId, Integer chatId,
			Integer userId) {
		super();
		this.roleId = roleId;
		this.statusId = statusId;
		this.chatId = chatId;
		this.userId = userId;
	}
	
	public Membership toMembership() {
		return this;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
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

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}