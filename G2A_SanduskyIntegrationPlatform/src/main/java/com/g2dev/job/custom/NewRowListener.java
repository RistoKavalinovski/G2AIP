package com.g2dev.job.custom;

import java.util.List;

import com.sugarcrm.www.sugarcrm.Name_value;

public interface NewRowListener {

	public List<Name_value> newRowEvent(List<Name_value> currentRow);

	public void afterInsertBatch(List<List<Name_value>> targetBatch);
	
	public void beforeProcessBatch(List<String[]> sourceBatch);
	
	public List<List<Name_value>> trimRows(List<List<Name_value>> targetBatch);

}
