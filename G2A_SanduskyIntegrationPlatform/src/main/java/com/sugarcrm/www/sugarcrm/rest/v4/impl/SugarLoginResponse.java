package com.sugarcrm.www.sugarcrm.rest.v4.impl;

import com.sugarcrm.www.sugarcrm.rest.v4.api.ISugarRESTSession;
import com.sugarcrm.www.sugarcrm.rest.v4.api.User;

/**
 * Sugar login API response in v4
 * 
 * @author mmarum
 *
 */
public class SugarLoginResponse extends SugarBean implements ISugarRESTSession {

	public String getSessionID() {
		return id;
	}

	public User getUser() {
		User user = new UsersResponse(values, moduleName);
		return user;
	}

}