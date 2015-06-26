package com.sugarcrm.www.sugarcrm.rest.v4.impl;

import java.util.Map;

import com.sugarcrm.www.sugarcrm.rest.v4.api.User;

/**
 * Users module response in v4 API
 * 
 * @author mmarum
 *
 */
public class UsersResponse extends SugarBean implements User {

	public UsersResponse() {
		super();
	}

	public UsersResponse(Map<String, Map<String, String>> values_map,
			String moduleName) {
		super(values_map, moduleName);
	}

	public String getUserId() {
		return values.get("user_id").get("value");
	}

	public String getUserName() {
		return values.get("user_name").get("value");
	}

	public String getUserLanguage() {
		return values.get("user_language").get("value");
	}

}