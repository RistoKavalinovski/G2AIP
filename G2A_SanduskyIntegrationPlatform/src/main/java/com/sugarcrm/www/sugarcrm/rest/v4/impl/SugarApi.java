package com.sugarcrm.www.sugarcrm.rest.v4.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarApiException;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarCredentials;
import com.sugarcrm.www.sugarcrm.rest.v4.api.ISugarRESTSession;

/**
 * Sugar API v4 specific stuff
 * 
 * @author mmarum
 *
 */
public class SugarApi {

	private String REST_ENDPOINT = null;

	private URLCodec codec = null;
	private Gson json = null;

	public SugarApi(String sugarUrl) {
		REST_ENDPOINT = sugarUrl + "/service/v4/rest.php";
		json = new GsonBuilder().create();
		codec = new URLCodec();
	}

	public class SugarLoginRequest {
		protected SugarCredentials user_auth;

		public void setUserAuth(SugarCredentials auth) {
			user_auth = auth;
		}
	}

	public class GetEntryRequest {

		public GetEntryRequest(String session, String moduleName, String id) {
			this.session = session;
			this.moduleName = moduleName;
			this.id = id;
		}

		protected String session;

		@SerializedName("module_name")
		protected String moduleName;
		@SerializedName("select_fields")
		protected String[] select_fields /* = {"id","oldId_c"} */;

		protected String id;

	}

	public class GetEntryListRequest {

		public GetEntryListRequest(String session, String moduleName,
				String query, String orderBy, int offset,
				String[] selectFields, int maxResults, int deleted) {
			this.session = session;
			this.moduleName = moduleName;
			setQuery(query);
			setOrderBy(orderBy);

			// this.orderBy = orderBy;
			// this.offset = offset + "";
			this.offset = new Integer(offset);
			if (selectFields != null && selectFields.length > 0) {
				this.selectFields = Arrays.asList(selectFields);
			}
			this.max_results = new Integer(maxResults);
			// this.deleted = deleted + "";
		}

		private void setOrderBy(String orderBy2) {
			if (orderBy2 != null) {
				orderBy = orderBy2;
			}

		}

		private void setQuery(String query2) {
			if (query2 != null) {
				this.query = query2;
			}
		}

		protected String session;

		@SerializedName("module_name")
		protected String moduleName;
		@SerializedName("query")
		protected String query = "";
		@SerializedName("order_by")
		protected String orderBy = "";
		@SerializedName("offset")
		protected Integer offset;
		@SerializedName("select_fields")
		protected List<String> selectFields = new ArrayList<String>(); // =

		@SerializedName("link_name_to_fields_array")
		protected List<String> link_name_to_fields_array = new ArrayList<String>();// "[{}]";
		// "[{}]";
		@SerializedName("max_results")
		protected Integer max_results;
		@SerializedName("deleted")
		protected Integer deleted = 0;

		protected Boolean favorites = false;

	}

	public class GetEntriesCountRequest {

		public GetEntriesCountRequest(String session, String moduleName,
				String query, int deleted) {
			this.session = session;
			this.moduleName = moduleName;
			this.query = query;
			this.deleted = new Integer(deleted);
		}

		protected String session;

		@SerializedName("module_name")
		protected String moduleName;
		@SerializedName("query")
		protected String query;
		@SerializedName("deleted")
		protected Integer deleted;

	}

	public class NameValue {
		protected String name;
		protected String value;
	}

	public String postToSugar(String urlStr) throws Exception {
		URL url = new URL(urlStr);
		System.out.println(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setAllowUserInteraction(false);
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");

		if (conn.getResponseCode() != 200) {
			throw new IOException(conn.getResponseMessage());
		}

		// Buffer the result into a string
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();

		conn.disconnect();
		if (System.getenv("sugardebug") != null) {
			System.out.println(sb.toString());
		}
		return sb.toString();
	}

	public ISugarRESTSession getSugarSession(SugarCredentials credentials)
			throws SugarApiException {

		SugarLoginRequest loginReq = new SugarLoginRequest();
		loginReq.setUserAuth(credentials);

		SugarLoginResponse jResp = null;
		try {
			String response = postToSugar(REST_ENDPOINT
					+ "?method=login&response_type=JSON&input_type=JSON&rest_data="
					+ codec.encode(json.toJson(loginReq)));
			jResp = json.fromJson(response, SugarLoginResponse.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SugarApiException("Sugar Login failed", e);
		}
		return jResp;
	}

	public List<SugarBean> getEntryList(ISugarRESTSession session,
			String moduleName, String query, String orderBy, int offset,
			String[] selectFields, int maxResults, int deleted)
			throws SugarApiException {
		String sessionId = session.getSessionID();
		GetEntryListRequest req = new GetEntryListRequest(sessionId,
				moduleName, query, orderBy, offset, selectFields, maxResults,
				deleted);
		String response = null;
		try {
			System.out.println(json.toJson(req));
			response = postToSugar(REST_ENDPOINT
					+ "?method=get_entry_list&response_type=JSON&input_type=JSON&rest_data="
					+ codec.encode(json.toJson(req)));
		} catch (EncoderException e) {
			e.printStackTrace();
			throw new SugarApiException("Could not fetch bean.", e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SugarApiException("Could not fetch bean.", e);
		}

		return Arrays.asList(parseJson(json, response).getEntryList());

	}

	public int getEntriesCount(ISugarRESTSession session, String moduleName,
			String query, int deleted) throws SugarApiException {
		String sessionId = session.getSessionID();
		GetEntriesCountRequest req = new GetEntriesCountRequest(sessionId,
				moduleName, query, deleted);
		String response = null;
		try {
			System.out.println(json.toJson(req));
			response = postToSugar(REST_ENDPOINT
					+ "?method=get_entries_count&response_type=JSON&input_type=JSON&rest_data="
					+ codec.encode(json.toJson(req)));
		} catch (EncoderException e) {
			e.printStackTrace();
			throw new SugarApiException("Could not fetch bean.", e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SugarApiException("Could not fetch bean.", e);
		}

		return parseCountJson(json, response);
		// GetEntryResponse entryResp = json.fromJson(response,
		// GetEntryResponse.class);
		// if (entryResp.getEntryList() == null) {
		// ErrorResponse error = json.fromJson(response, ErrorResponse.class);
		// SugarApiException ex = new SugarApiException(error.getName());
		// ex.setDescription(error.getDescription());
		// ex.setNumber(error.getNumber());
		// throw ex;
		// }
		// if (entryResp.getEntryList().length > 0) {
		// return Arrays.asList(entryResp.getEntryList());
		// } else {
		// return null;
		// }
	}

	private int parseCountJson(Gson json2, String response) {
		JsonElement parse = new JsonParser().parse(response);
		if (parse.isJsonObject()) {
			try {
				return Integer.parseInt(parse.getAsJsonObject()
						.get("result_count").toString().replaceAll("\"", ""));
			} catch (Exception e) {
				System.out.println();
			}
		}
		return -1;
	}

	private GetEntryResponse parseJson(Gson json2, String response) {
		JsonElement parse = new JsonParser().parse(response);
		if (parse.isJsonObject()) {
			JsonObject jsonObject = parse.getAsJsonObject();
			JsonElement entryList = jsonObject.get("entry_list");
			if (entryList.isJsonArray()) {
				JsonArray entryArray = entryList.getAsJsonArray();
				GetEntryResponse rsp = new GetEntryResponse();
				List<SugarBean> beans = new ArrayList<SugarBean>();
				for (int i = 0; i < entryArray.size(); i++) {
					JsonElement jsonElement = entryArray.get(i);
					JsonObject beanObject = jsonElement.getAsJsonObject();
					SugarBean sb = new SugarBean();
					sb.id = beanObject.get("id").toString();
					sb.moduleName = beanObject.get("module_name").toString();
					JsonObject fieldsArray = beanObject.get("name_value_list")
							.getAsJsonObject();
					Map<String, Map<String, String>> valuesMap = new HashMap<String, Map<String, String>>();
					Set<Entry<String, JsonElement>> entrySet = fieldsArray
							.entrySet();
					for (Entry<String, JsonElement> entry : entrySet) {
						JsonObject nameValueObject = entry.getValue()
								.getAsJsonObject();
						Map<String, String> nameValueMap = new HashMap<String, String>();
						// nameValueMap.put(
						// nameValueObject.get("name").toString(),
						// nameValueObject.get("value").toString());
						nameValueMap.put("name", nameValueObject.get("name")
								.toString());
						nameValueMap.put("value", nameValueObject.get("value")
								.toString());
						// if (entry.getKey().equals("id")
						// || entry.getKey().equals("oldid_c")) {
						// System.out.println();
						// }
						valuesMap.put(entry.getKey(), nameValueMap);
					}
					sb.values = valuesMap;
					beans.add(sb);
				}
				rsp.entryList = beans.toArray(new SugarBean[beans.size()]);
				return rsp;
			}
			System.out.println("");
		}

		// JsonObject.
		// TODO parse JSON string
		System.out.println(parse);
		return null;

	}

	public SugarBean getBean(ISugarRESTSession session, String moduleName,
			String guid) throws SugarApiException {
		String sessionId = session.getSessionID();
		GetEntryRequest req = new GetEntryRequest(sessionId, moduleName, guid);
		String response = null;
		try {
			System.out.println(json.toJson(req));
			response = postToSugar(REST_ENDPOINT
					+ "?method=get_entry&response_type=JSON&input_type=JSON&rest_data="
					+ codec.encode(json.toJson(req)));
		} catch (EncoderException e) {
			e.printStackTrace();
			throw new SugarApiException("Could not fetch bean.", e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SugarApiException("Could not fetch bean.", e);
		}

		GetEntryResponse entryResp = json.fromJson(response,
				GetEntryResponse.class);
		if (entryResp.getEntryList() == null) {
			ErrorResponse error = json.fromJson(response, ErrorResponse.class);
			SugarApiException ex = new SugarApiException(error.getName());
			ex.setDescription(error.getDescription());
			ex.setNumber(error.getNumber());
			throw ex;
		}
		if (entryResp.getEntryList().length > 0) {
			return entryResp.getEntryList()[0];
		} else {
			return null;
		}
	}

}