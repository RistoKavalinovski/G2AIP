package com.g2dev.connect;

public class DbField {
	private String name;
	private String type;
	private int index;
	private DbObject object;

	public DbField(DbObject object) {
		this.setObject(object);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public DbObject getObject() {
		return object;
	}

	public void setObject(DbObject object) {
		this.object = object;
	}

}
