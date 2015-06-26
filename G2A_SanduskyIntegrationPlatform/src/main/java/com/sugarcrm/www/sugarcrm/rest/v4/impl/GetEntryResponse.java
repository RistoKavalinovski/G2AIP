package com.sugarcrm.www.sugarcrm.rest.v4.impl;

import com.google.gson.annotations.SerializedName;

/**
 * GetEntry response for v4 API
 * 
 * @author mmarum
 *
 */
public class GetEntryResponse {

	@SerializedName("entry_list")
	protected SugarBean[] entryList;
	@SerializedName("relationship_list")
	protected Object relationshipList;

	public SugarBean[] getEntryList() {
		return entryList;
	}

	public Object getRelationshipList() {
		return relationshipList;
	}

}