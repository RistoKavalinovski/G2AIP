package com.sugarcrm.www.sugarcrm.rest.v4.impl;

import java.util.Collection;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.sugarcrm.www.sugarcrm.rest.v4.api.ISugarBean;

/**
 * SugarBean response for the v4 API
 * 
 * @author mmarum
 *
 */
public class SugarBean implements ISugarBean {

	@SerializedName("name_value_list")
	protected Map<String, Map<String, String>> values;

	@SerializedName("module_name")
	protected String moduleName;

	@SerializedName("id")
	protected String id;

	// Needed for Gson
	public SugarBean() {

	}

	public SugarBean(Map<String, Map<String, String>> name_value_list,
			String module_name) {
		values = name_value_list;
		moduleName = module_name;
	}

	public String getId() {
		return id;
	}

	public String get(String fieldName) {
		return values.get(fieldName).get("value");
	}

	public Collection<String> getFieldNames() {
		return values.keySet();
	}

	public String getModuleName() {
		return moduleName;
	}

}