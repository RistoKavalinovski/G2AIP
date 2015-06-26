package com.g2dev.map;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.g2dev.connect.DbField;
import com.g2dev.connect.DbObject;
import com.g2dev.connect.Schema;
import com.g2dev.integrator.StaticProperties;

public class IntegrationMap {
	private List<Schema> from = new ArrayList<Schema>();
	private Schema to;
	private Map<DbField, DbField> fromToMap = new HashMap<DbField, DbField>();
	final static Logger logger = Logger.getLogger(IntegrationMap.class);

	public List<Schema> getFrom() {
		return from;
	}

	public void setFrom(List<Schema> from) {
		this.from = from;
	}

	public Schema getTo() {
		return to;
	}

	public void setTo(Schema to) {
		this.to = to;
	}

	public Map<DbField, DbField> getFromToMap() {
		return fromToMap;
	}

	public void setFromToMap(Map<DbField, DbField> fromToMap) {
		this.fromToMap = fromToMap;
	}

	public List<String> getTargetFieldNames() {
		List<String> targetFieldNames = new ArrayList<String>();
		for (DbField fromField : getFromToMap().keySet()) {
			DbField toField = getFromToMap().get(fromField);
			targetFieldNames.add(toField.getName());
		}
		return targetFieldNames;
	}

	public Map<String, String> getStringFromToMap() {
		Map<String, String> stringFromToMap = new HashMap<String, String>();
		for (DbField fromField : getFromToMap().keySet()) {
			DbField toField = getFromToMap().get(fromField);
			stringFromToMap.put(fromField.getName(), toField.getName());
		}
		return stringFromToMap;
	}

	public Map<String, String> getStringFromIndexToMap() {
		Map<String, String> stringFromToMap = new HashMap<String, String>();
		for (DbField fromField : getFromToMap().keySet()) {
			DbField toField = getFromToMap().get(fromField);
			stringFromToMap.put(fromField.getIndex() + "", toField.getName());
		}
		return stringFromToMap;
	}

	public IntegrationMap loadFromAccountsFile(File file) {
		loadFromFile(file, "Accounts");
		return this;
	}

	public IntegrationMap loadFromFile(File file, String moduleName) {
		BufferedReader in = null;
		fromToMap = new HashMap<DbField, DbField>();
		Schema sugarSchema = new Schema();
		Schema csvSchema = new Schema();
		DbObject sugarObject = new DbObject(sugarSchema);
		sugarObject.setName(moduleName);
		sugarSchema.getObjects().add(sugarObject);
		DbObject csvObject = new DbObject(csvSchema);
		csvObject.setName("CSV_File");
		csvSchema.getObjects().add(csvObject);
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF8"));
			String str;

			try {
				while ((str = in.readLine()) != null) {
					String[] split = str.split("\t");
					if (split.length == 2) {
						try {
							DbField sugarField = new DbField(sugarObject);
							sugarField.setName(split[0]);
							sugarObject.getFields().add(sugarField);
							DbField csvField = new DbField(csvObject);
							csvField.setIndex(Integer.valueOf(split[1]));
							csvObject.getFields().add(csvField);
							fromToMap.put(csvField, sugarField);
							// map.put(split[0], Integer.valueOf(split[1]));
						} catch (Exception e) {
							logger.error("Map file error/ Line is: " + str, e);
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				}
				in.close();
			} catch (IOException e) {
				logger.error("read line error", e);
				e.printStackTrace();
				try {
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		} catch (UnsupportedEncodingException e) {
			logger.error("Unsoported encoding: UTF8", e);
			e.printStackTrace();

		} catch (FileNotFoundException e) {
			logger.error("Map file not found: " + file.getAbsolutePath(), e);
			e.printStackTrace();

		}
		return this;
	}

	public static void main(String[] args) {

		IntegrationMap mapper = new IntegrationMap();
		mapper.loadFromAccountsFile(new File(StaticProperties.ACCOUNTS_MAP_FILE_PATH));
		Map<DbField, DbField> fromToMap2 = mapper.getFromToMap();
		for (DbField csvField : fromToMap2.keySet()) {
			System.out.println("CSV: " + csvField.getIndex());
			System.out.println("Sugar: " + fromToMap2.get(csvField).getName());
		}

	}
}
