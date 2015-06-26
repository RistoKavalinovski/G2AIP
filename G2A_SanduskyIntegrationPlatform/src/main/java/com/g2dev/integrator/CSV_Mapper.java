package com.g2dev.integrator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class CSV_Mapper {

	private Map<String, Integer> map;
	private Map<String, Type> typeMap;

	final static Logger logger = Logger.getLogger(CSV_Mapper.class);

	public enum Type {
		id, name, datetime, assigned_user_name, varchar
	}

	public Map<String, Integer> getMap() {
		return map;
	}

	public void setMap(Map<String, Integer> map) {
		this.map = map;
	}

	public void loadFromFile(File file) {
		BufferedReader in = null;
		map = new HashMap<String, Integer>();
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF8"));
			String str;

			try {
				while ((str = in.readLine()) != null) {
					String[] split = str.split("\t");
					if (split.length == 2) {
						try {
							map.put(split[0], Integer.valueOf(split[1]));
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

	}

	public Map<String, Type> getTypeMap() {
		return typeMap;
	}

	public void setTypeMap(Map<String, Type> typeMap) {
		this.typeMap = typeMap;
	}

	public static void main(String[] args) {
		CSV_Mapper mapper = new CSV_Mapper();
		mapper.loadFromFile(new File(StaticProperties.ACCOUNTS_MAP_FILE_PATH));
		Map<String, Integer> map2 = mapper.getMap();
		for (String key : map2.keySet()) {
			System.out.println("k>" + key + " v>" + map2.get(key));
		}
	}

}
