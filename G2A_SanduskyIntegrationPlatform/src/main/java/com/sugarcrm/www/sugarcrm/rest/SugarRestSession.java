package com.sugarcrm.www.sugarcrm.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sugarcrm.www.sugarcrm.rest.v4.api.ISugarRESTSession;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarApiException;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarClient;
import com.sugarcrm.www.sugarcrm.rest.v4.impl.SugarBean;

public class SugarRestSession {

	private static SugarRestSession instance;

	public static SugarRestSession getInstance() {
		if (instance == null) {
			instance = new SugarRestSession();
		}
		return instance;
	}

	private String endPoint = "";
	private String username = "";
	private String password = "";
	private ISugarRESTSession restSession;

	public String getEndPoint() {
		return endPoint;
	}

	// @Deprecated
	public SugarClient getSanduskyClient() {
		setEndPoint("https://sanduskynews.sugarondemand.com");
		setUsername("G2Dev.Sandusky");
		setPassword("G2@nalyt1c$2015");
		return getClient();
	}

	public ISugarRESTSession getSession() {
		return restSession;
	}

	public static void main(String[] args) {
		try {
			List<SugarBean> entryList = SugarRestSession
					.getInstance()
					.getSanduskyClient()
					.getEntryList(
							getInstance().getSession(),
							"Users",
							null,
							null,
							0,
							new ArrayList<String>(Arrays.asList("id",
									"reports_to_id", "reports_to_name"))
									.toArray(new String[2]), 51, 0);

			for (SugarBean sugarBean : entryList) {
				System.out.println();
				System.out.println(sugarBean.get("id") + " | "
						+ sugarBean.get("reports_to_id") + " | "
						+ sugarBean.get("reports_to_name"));
			}
		} catch (SugarApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public SugarClient getClient() {
		SugarClient client = new SugarClient(
				"https://sanduskynews.sugarondemand.com");
		try {
			restSession = client.getSugarSession(getUsername(), getPassword());
		} catch (SugarApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return client;
	}

	public String getSessionId() {
		if (restSession != null) {
			return restSession.getSessionID();
		}
		return "";
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
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

}
