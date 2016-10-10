package com.ibm.other;

import java.sql.Timestamp;

public class Chat {
	
	private int id;
	private String name;
	private String summary;
	private int typeId;
	private int creatorId;
	private Timestamp createTms;
	private Timestamp updateTms;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public int getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
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
	
}
