package com.g2dev.connect;

import java.util.ArrayList;
import java.util.List;

public class Schema {

	private List<DbObject> objects = new ArrayList<DbObject>();

	public List<DbObject> getObjects() {
		return objects;
	}

	public void setObjects(List<DbObject> dbObjects) {
		this.objects = dbObjects;
	}

}
