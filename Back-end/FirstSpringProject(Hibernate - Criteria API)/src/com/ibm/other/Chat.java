package com.ibm.other;

import java.sql.Timestamp;

public abstract class Chat {
	
	public abstract int getId();

	public abstract void setId(int id);

	public abstract Timestamp getCreateTms();

	public abstract void setCreateTms(Timestamp createTms);

	public abstract int getCreatorId();

	public abstract void setCreatorId(int creatorId);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getSummary();

	public abstract void setSummary(String summary);
	
	public abstract int getTypeId();

	public abstract void setTypeId(int typeId);

	public abstract Timestamp getUpdateTms();

	public abstract void setUpdateTms(Timestamp updateTms);

}
