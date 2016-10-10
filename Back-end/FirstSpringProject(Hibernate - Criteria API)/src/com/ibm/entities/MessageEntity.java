package com.ibm.entities;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ibm.other.Message;

import java.sql.Timestamp;


/**
 * The persistent class for the message database table.
 * 
 */
@Entity
@Table(name="message")
@NamedQuery(name="MessageEntity.findAll", query="SELECT m FROM MessageEntity m")
public class MessageEntity extends Message implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private String content;

	@Column(name="create_tms")
	private Timestamp createTms;

	@Column(name="update_tms")
	private Timestamp updateTms;

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
	@JoinColumn(name="sender_id", insertable = false, updatable = false)
	private UserEntity user;
	
	@Column(name="sender_id")
	private int senderId;

	public MessageEntity() {
	}

	public MessageEntity(int id, String content, Timestamp createTms, Timestamp updateTms, ChatEntity chatEntity,
			Integer chatId, UserEntity user, Integer senderId) {
		super();
		this.id = id;
		this.content = content;
		this.createTms = createTms;
		this.updateTms = updateTms;
		this.chatEntity = chatEntity;
		this.chatId = chatId;
		this.user = user;
		this.senderId = senderId;
	}

	public MessageEntity(String content, Timestamp createTms, Timestamp updateTms, Integer chatId, Integer senderId) {
		super();
		this.content = content;
		this.createTms = createTms;
		this.updateTms = updateTms;
		this.chatId = chatId;
		this.senderId = senderId;
	}
	
	public Message toMessage() {
		return this;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Timestamp getCreateTms() {
		return createTms;
	}

	public void setCreateTms(Timestamp createTms) {
		this.createTms = createTms;
	}

	public Timestamp getUpdateTms() {
		return updateTms;
	}

	public void setUpdateTms(Timestamp updateTms) {
		this.updateTms = updateTms;
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

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

}