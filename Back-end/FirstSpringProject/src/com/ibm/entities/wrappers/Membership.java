package com.ibm.entities.wrappers;

import com.ibm.entities.ChatEntity;
import com.ibm.entities.UserEntity;

public abstract class Membership {

	public abstract int getId();

	public abstract void setId(int id);

	public abstract int getRoleId();

	public abstract void setRoleId(int roleId);

	public abstract int getStatusId();

	public abstract void setStatusId(int statusId);

	public abstract ChatEntity getChatEntity();

	public abstract void setChatEntity(ChatEntity chatEntity);

	public abstract int getChatId();

	public abstract void setChatId(int chatId);

	public abstract UserEntity getUser();

	public abstract void setUser(UserEntity user);

	public abstract int getUserId();

	public abstract void setUserId(int userId);

}
