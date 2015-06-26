package com.g2dev.integrator;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.Name_value;

public class SugarIntegratorCSV implements CSV_Integrator {

	private String moduleName;
	private CSV_Mapper mapper;
	final static Logger logger = Logger.getLogger(SugarIntegratorCSV.class);

	public SugarIntegratorCSV(String moduleName, CSV_Mapper mapper) {
		this.moduleName = moduleName;
		this.mapper = mapper;
	}

	public void process(List<String[]> batch) {
		List<List<Name_value>> targetBatch = new ArrayList<List<Name_value>>();
		for (String[] fieldValues : batch) {
			List<Name_value> targetRow = new ArrayList<Name_value>();
			for (String fieldName : mapper.getMap().keySet()) {
				try {
					targetRow.add(new Name_value(fieldName, fieldValues[mapper
							.getMap().get(fieldName)]));
				} catch (Exception e) {
					logger.error("Name value error. fieldName: " + fieldName, e);
				}
			}
			targetBatch.add(targetRow);
		}
		try {
			SugarSession
					.getInstance()
					.getSugar()
					.set_entries(SugarSession.getInstance().getSessionId(),
							getModuleName(),
							toMatrix(targetBatch, mapper.getMap().size()));
			logger.info("Batch with size " + batch.size()
					+ " loaded succesfuly");
		} catch (RemoteException e) {
			logger.error("Failed to push batch to server.", e);
			e.printStackTrace();
		}
	}

	private static Name_value[][] toMatrix(List<List<Name_value>> targetBatch,
			int noColumns) {
		Name_value[][] matrix = new Name_value[targetBatch.size()][noColumns];
		for (int i = 0; i < targetBatch.size(); i++) {
			List<Name_value> list = targetBatch.get(i);
			for (int j = 0; j < list.size(); j++) {
				matrix[i][j] = list.get(j);
			}
		}
		return matrix;
	}

	public static void main(String[] args) {
		List<List<Name_value>> targetBatch = new ArrayList<List<Name_value>>();
		for (int i = 0; i < 5; i++) {
			List<Name_value> list = new ArrayList<Name_value>();
			for (int j = 0; j < 5; j++) {
				list.add(new Name_value(i + "", j + ""));
			}
			targetBatch.add(list);
		}
		Name_value[][] matrix = toMatrix(targetBatch, 5);
		for (Name_value[] name_values : matrix) {
			for (Name_value name_value : name_values) {
				System.out.println(name_value.getName() + " "
						+ name_value.getValue());
			}
		}
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public CSV_Mapper getMapper() {
		return mapper;
	}

	public void setMapper(CSV_Mapper mapper) {
		this.mapper = mapper;
	}

}
