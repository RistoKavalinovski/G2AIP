package com.g2dev.connect;

import java.util.ArrayList;
import java.util.List;

public class DbObject {

	private String name;
	private List<DbField> fields = new ArrayList<DbField>();
	private Schema schema;

	public DbObject(Schema schema) {
		this.setSchema(schema);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DbField> getFields() {
		return fields;
	}

	public void setFields(List<DbField> dbFields) {
		this.fields = dbFields;
	}

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

}
