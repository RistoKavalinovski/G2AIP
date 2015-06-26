package com.sugarcrm.www.sugarcrm.rest.v4.api;

import java.util.List;

import com.sugarcrm.www.sugarcrm.rest.v4.impl.SugarApi;
import com.sugarcrm.www.sugarcrm.rest.v4.impl.SugarBean;

/**
 * Sugar Client
 * 
 * @author mmarum
 *
 */
public class SugarClient {

	private SugarApi sugar = null;

	public SugarClient(String sugarUrl) {
		// Only support 1 version of the API right now
		sugar = new SugarApi(sugarUrl);
	}

	public ISugarRESTSession getSugarSession(String userId, String password)
			throws SugarApiException {
		return sugar.getSugarSession(new SugarCredentials(userId, password));
	}

	public ISugarRESTSession getSugarSession(SugarCredentials credentials)
			throws SugarApiException {
		return sugar.getSugarSession(credentials);
	}

	public ISugarBean getBean(ISugarRESTSession session, String moduleName,
			String guid) throws SugarApiException {
		return sugar.getBean(session, moduleName, guid);
	}

	public List<SugarBean> getEntryList(ISugarRESTSession session,
			String moduleName, String query, String orderBy, int offset,
			String[] selectFields, int maxResults, int deleted)
			throws SugarApiException {
		return sugar.getEntryList(session, moduleName, query, orderBy, offset,
				selectFields, maxResults, deleted);
	}

	public int getEntriesCount(ISugarRESTSession session, String moduleName,
			String query, int deleted) throws SugarApiException {
		return sugar.getEntriesCount(session, moduleName, query, deleted);
	}

}