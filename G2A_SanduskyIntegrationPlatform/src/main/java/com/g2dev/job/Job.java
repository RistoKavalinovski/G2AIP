package com.g2dev.job;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.g2dev.connect.Connector;
import com.g2dev.connect.ext.CSVConnector;
import com.g2dev.connect.ext.SugarConnector;
import com.g2dev.input.ext.InputCsv;
import com.g2dev.input.ext.Parser;
import com.g2dev.map.IntegrationMap;

public class Job {

	private List<Connector> connectors = new ArrayList<Connector>();
	private IntegrationMap map = new IntegrationMap();

	public void addConnector(Connector connector) {
		if (!getConnectors().contains(connector)) {
			getConnectors().add(connector);
		}
	}

	public void removeCOnnector(Connector connector) {
		if (getConnectors().contains(connector)) {
			getConnectors().remove(connector);
		}
	}

	public List<Connector> getConnectors() {
		return connectors;
	}

	public void setConnectors(List<Connector> connectors) {
		this.connectors = connectors;
	}

	public IntegrationMap getMap() {
		return map;
	}

	public void setMap(IntegrationMap map) {
		this.map = map;
	}

	public void saveToFile(File file) {
		Map<String, String> mapMetadata = new HashMap<String, String>();
		mapMetadata
				.put("toObject",
						getMap().getFromToMap()
								.get(getMap().getFromToMap().keySet()
										.iterator().next()).getObject()
								.getName());
		mapMetadata.put("fromObject", getMap().getFromToMap().keySet()
				.iterator().next().getObject().getName());
		JSONObject mapMetadataObj = new JSONObject(mapMetadata);
		JSONObject mapObj = new JSONObject(getMap().getStringFromToMap());
		Map<String, JSONObject> jsons = new HashMap<String, JSONObject>();
		jsons.put("map", mapObj);
		jsons.put("mapMetadata", mapMetadataObj);
		String[] connectorFields = { "apiURL", "username", "password",
				"secToken", "csvFilePath", "firstRowColumnName", };
		JSONObject connectorJson = new JSONObject(getConnectors().get(1),
				connectorFields);
		jsons.put("connector", connectorJson);
		JSONObject forFile = new JSONObject(jsons);

		System.out.println(forFile);
	}

	public static void main(String[] args) {
		Job job = new Job();
		CSVConnector connector = new CSVConnector(new InputCsv());
		connector
				.setCsvFilePath("D:\\Sandysky data\\Sandusky_Extracts\\MSGEXP01_Sandusky.txt");
		job.addConnector(connector);
		SugarConnector sugarConnector = new SugarConnector();
		sugarConnector.getSelectedModules().add("Accounts");
		job.addConnector(sugarConnector);
		IntegrationMap map = new IntegrationMap();

		map.getFromToMap().put(
				connector.getSchema().getObjects().get(0).getFields().get(0),
				sugarConnector.getSchema().getObjects().get(0).getFields()
						.get(0));
		map.getFromToMap().put(
				connector.getSchema().getObjects().get(0).getFields().get(1),
				sugarConnector.getSchema().getObjects().get(0).getFields()
						.get(1));
		job.setMap(map);
		job.saveToFile(new File("testSaveJOB.g2j"));
	}

	public static Job loadFromFile(File file) {
		return null;
	}

}
