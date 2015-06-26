package com.g2dev.connect;

public class Connector {

	private String apiURL;
	private String username;
	private String password;
	private String secToken;
	private String csvFilePath;
	private boolean firstRowColumnName;
	private Schema schema;

	public String getApiURL() {
		return apiURL;
	}

	public void setApiURL(String apiURL) {
		this.apiURL = apiURL;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSecToken() {
		return secToken;
	}

	public void setSecToken(String secToken) {
		this.secToken = secToken;
	}

	public String getCsvFilePath() {
		return csvFilePath;
	}

	public void setCsvFilePath(String csvFilePath) {
		this.csvFilePath = csvFilePath;
	}

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public boolean isFirstRowColumnName() {
		return firstRowColumnName;
	}

	public void setFirstRowColumnName(boolean firstRowColumnName) {
		this.firstRowColumnName = firstRowColumnName;
	}

}
