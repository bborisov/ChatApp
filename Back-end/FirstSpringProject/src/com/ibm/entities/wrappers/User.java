package com.ibm.entities.wrappers;

public abstract class User {

	public abstract int getId();

	public abstract void setId(int id);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getEmail();

	public abstract void setEmail(String email);

	public abstract String getPassword();

	public abstract void setPassword(String password);

	public abstract String getSalt();

	public abstract void setSalt(String salt);

	public abstract int getStatusId();

	public abstract void setStatusId(int statusId);

}
