package com.g2dev.job.custom.ogden.migration.workflow;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.g2dev.job.custom.ogden.migration.OgdenSugarSession;
import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.Field;
import com.sugarcrm.www.sugarcrm.Name_value;
import com.sugarcrm.www.sugarcrm.New_module_fields;
import com.sugarcrm.www.sugarcrm.rest.SugarRestSession;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarApiException;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarClient;
import com.sugarcrm.www.sugarcrm.rest.v4.impl.SugarBean;

public abstract class UpdateRelationshipsProcess {

	public static String ogdenQuery = null;
	private static Map<String, Map<String, String>> moduleMap = new HashMap<String, Map<String, String>>();

	// /**
	// *
	// * @param moduleName
	// * @return oldIdIdMap map of oldId key Id key
	// */
	// public static Map<String, String> loadModule1(String moduleName) {
	// SugarSession session = SugarSession.getInstance();
	// if (session.loginForSandusky()) {
	// String[] select_fields = { "id", "oldid_c" };
	// try {
	// Get_entry_list_result get_entry_list = session.getSugar()
	// .get_entry_list(session.getSessionId(), moduleName,
	// null, null, 0, select_fields,
	// Integer.MAX_VALUE, 0);
	// Map<String, String> oldIdIdMap = new HashMap<String, String>();
	// Entry_value[] entry_list = get_entry_list.getEntry_list();
	// for (Entry_value entry_value : entry_list) {
	// Name_value[] name_value_list = entry_value
	// .getName_value_list();
	// String id = "";
	// String oldId = "";
	// for (Name_value name_value : name_value_list) {
	// if (name_value.getName().equalsIgnoreCase("id")) {
	// id = name_value.getValue();
	// } else if (name_value.getName().equalsIgnoreCase(
	// "oldid_c")) {
	// oldId = name_value.getValue();
	// }
	// }
	// oldIdIdMap.put(oldId, id);
	// }
	// return oldIdIdMap;
	// } catch (RemoteException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// return null;
	// }

	private static String findOldIdName(String moduleName) {
		String fieldName = "";
		SugarSession session = SugarSession.getInstance();
		if (session.loginForSandusky()) {
			try {
				/*Module_fields get_module_fields = session.getSugar()
						.get_module_fields(session.getSessionId(), moduleName);*/
				New_module_fields get_module_fields = session.getSugar()
						.get_module_fields(session.getSessionId(), moduleName,null);
				Field[] module_fields = get_module_fields.getModule_fields();
				for (Field field : module_fields) {
					if (field.getName().contains("oldid")) {
						return field.getName();
					}
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return fieldName;
	}

	/**
	 * 
	 * @param moduleName
	 * @return oldIdIdMap map of oldId key Id key
	 */
	@SuppressWarnings("deprecation")
	public static Map<String, String> loadModule(String moduleName, String query) {
		SugarRestSession session = SugarRestSession.getInstance();
		SugarClient client = session.getSanduskyClient();
		if (client != null) {
			String oldIdName = "oldid_c"/* findOldIdName(moduleName) */;
			String[] select_fields = { "id",
			/* moduleName.toLowerCase() + "." + */oldIdName };
			try {
				int batchSize = 2000;
				int offset = 0;
				int numberOfRecords = client.getEntriesCount(
						session.getSession(), moduleName, query, 0);
				Map<String, String> oldIdIdMap = new HashMap<String, String>();
				long startTime = System.currentTimeMillis();
				while (numberOfRecords > (offset + batchSize) || offset == 0) {

					List<SugarBean> entryList = client.getEntryList(
							session.getSession(), moduleName, query, null,
							offset, select_fields, batchSize, 0);
					for (SugarBean entry_value : entryList) {
						String id = entry_value.get("id").replaceAll("\"", "");
						String oldId = entry_value.get(oldIdName).replaceAll(
								"\"", "");

						System.out.println(oldId + " | " + id);
						oldIdIdMap.put(oldId, id);
					}
					offset += batchSize;
					SugarSession.getInstance().printETA(numberOfRecords,
							offset, startTime);
					// startTime = System.currentTimeMillis();
				}
				return oldIdIdMap;
			} catch (SugarApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	// /**
	// *
	// * @param moduleName
	// * @return oldIdIdMap map of oldId key Id key
	// */
	// public static Map<String, String> loadModule(String moduleName, String
	// query) {
	// SugarSession session = SugarSession.getInstance();
	// if (session.loginForSandusky()) {
	// String oldIdName = "oldid_c"/*findOldIdName(moduleName)*/;
	// String[] select_fields = { "id",
	// /* moduleName.toLowerCase() + "." + */oldIdName };
	// try {
	// int batchSize = 2000;
	// int offset = 0;
	// int numberOfRecords = session
	// .getSugar()
	// .get_entries_count(session.getSessionId(), moduleName,
	// query, 0).getResult_count();
	// Map<String, String> oldIdIdMap = new HashMap<String, String>();
	// long startTime = System.currentTimeMillis();
	// while (numberOfRecords > (offset + batchSize) || offset == 0) {
	//
	// Get_entry_list_result get_entry_list = session.getSugar()
	// .get_entry_list(session.getSessionId(), moduleName,
	// query, null, offset, select_fields,
	// batchSize, 0);
	// Entry_value[] entry_list = get_entry_list.getEntry_list();
	// for (Entry_value entry_value : entry_list) {
	// Name_value[] name_value_list = entry_value
	// .getName_value_list();
	// String id = "";
	// String oldId = "";
	// for (Name_value name_value : name_value_list) {
	// if (name_value.getName().equalsIgnoreCase("id")) {
	// id = name_value.getValue();
	// } else if (name_value.getName().equalsIgnoreCase(
	// oldIdName)) {
	// oldId = name_value.getValue();
	// }
	// }
	// System.out.println(oldId + " | " + id);
	// oldIdIdMap.put(oldId, id);
	// }
	// offset += batchSize;
	// session.printETA(numberOfRecords, offset, startTime);
	// // startTime = System.currentTimeMillis();
	// }
	// return oldIdIdMap;
	// } catch (RemoteException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// return null;
	// }

	public static void main(String[] args) {

		// Map<String, String> oldIdMap = loadModule("Accounts");
		// for (String oldId : oldIdMap.keySet()) {
		// System.out.println(oldId + " | " + oldIdMap.get(oldId));
		// }

		// SugarSession.getInstance().printModuleFields("Users");

		// u01UpdateAccounts();
		u02UpdateUsers();

	}

	private static void u01UpdateAccounts() {
		List<String> relatedModules = Arrays.asList("Users", "Campaigns");
		Map<String, String> relateFieldModule = new HashMap<String, String>();
		relateFieldModule.put("assigned_user_id", "Users");
		relateFieldModule.put("modified_user_id", "Users");
		relateFieldModule.put("created_by", "Users");
		relateFieldModule.put("parent_id", "Accounts");
		relateFieldModule.put("campaign_id", "Campaigns");
		// String query = "market_c='Ogden'";
		String query = null;
		ogdenQuery = "accounts.date_modified>20130000";
		updateModule("Accounts", relateFieldModule, relatedModules, query);

	}

	private static void u02UpdateUsers() {
		ogdenQuery = null;
		List<String> relatedModules = Arrays.asList("Users");
		Map<String, String> relateFieldModule = new HashMap<String, String>();
		relateFieldModule.put("reports_to_id", "Users");
		// relateFieldModule.put("reports_to_name", "Users");
		// String query = "market_c='Ogden'";
		String query = null;
		updateModule("Users", relateFieldModule, relatedModules, query);

	}

	public static void updateModule(String moduleName,
			Map<String, String> relateFieldModule, List<String> relatedModules) {
		updateModule(moduleName, relateFieldModule, relatedModules, null);

	}

	public static void updateModule(String moduleName,
			Map<String, String> relateFieldModule, List<String> relatedModules,
			String query) {/*
							 * List<String> moduleNames = new
							 * ArrayList<String>(relatedModules);
							 * moduleNames.add(moduleName); Map<String,
							 * Map<String, String>> moduleMap =
							 * loadModuleMap(moduleNames, query); SugarSession
							 * session = SugarSession.getInstance(); if
							 * (session.loginForSandusky()) { OgdenSugarSession
							 * ogdenSession = OgdenSugarSession.getInstance();
							 * if (ogdenSession.loginForOgden()) { List<String>
							 * fieldsSet = new ArrayList<String>(
							 * relateFieldModule.keySet()); fieldsSet.add("id");
							 * String[] selectFields = fieldsSet.toArray(new
							 * String[fieldsSet .size()]); try { int batchSize =
							 * 500; int offset = 0; int numberOfEntries =
							 * ogdenSession .getSugar()
							 * .get_entries_count(ogdenSession.getSessionId(),
							 * moduleName, ogdenQuery, 0) .getResult_count();
							 * 
							 * long startTime = System.currentTimeMillis();
							 * while (numberOfEntries > (offset + batchSize) ||
							 * offset == 0) {
							 * com.g2dev.job.custom.ogden.migration
							 * .sugar.Get_entry_list_result get_entry_list =
							 * ogdenSession .getSugar().get_entry_list(
							 * ogdenSession.getSessionId(), moduleName,
							 * ogdenQuery, null, offset, selectFields,
							 * batchSize, 0); if (get_entry_list.getError() ==
							 * null || get_entry_list.getError().getNumber() ==
							 * null || get_entry_list.getError().getNumber()
							 * .equals("0")) {
							 * com.g2dev.job.custom.ogden.migration
							 * .sugar.Entry_value[] entry_list = get_entry_list
							 * .getEntry_list(); Name_value[][] updateRecords =
							 * new Name_value[entry_list.length][]; for (int i =
							 * 0; i < entry_list.length; i++) {
							 * com.g2dev.job.custom
							 * .ogden.migration.sugar.Entry_value entry_value =
							 * entry_list[i];
							 * com.g2dev.job.custom.ogden.migration
							 * .sugar.Name_value[] name_value_list = entry_value
							 * .getName_value_list(); Name_value[] updateNvList
							 * = new Name_value[name_value_list.length]; for
							 * (int j = 0; j < name_value_list.length; j++) {
							 * com
							 * .g2dev.job.custom.ogden.migration.sugar.Name_value
							 * name_value = name_value_list[j]; Map<String,
							 * String> oldIdIdMap = null; if
							 * (name_value.getName().equalsIgnoreCase( "id")) {
							 * oldIdIdMap = moduleMap.get(moduleName); } else {
							 * oldIdIdMap = moduleMap .get(relateFieldModule
							 * .get(name_value .getName()));
							 * 
							 * // if (updateNvList[j].getValue() == // null //
							 * || updateNvList[j].getValue() // .isEmpty()) { //
							 * updateNvList[j].setValue(name_value //
							 * .getValue()); // } } if (oldIdIdMap != null) {
							 * updateNvList[j] = new Name_value(
							 * name_value.getName(), oldIdIdMap.get(name_value
							 * .getValue())); } } updateRecords[i] =
							 * clearNullList(updateNvList); }
							 * updateModule(moduleName,
							 * clearRecords(updateRecords));
							 * 
							 * } offset += batchSize;
							 * session.printETA(numberOfEntries, offset,
							 * startTime); } } catch (RemoteException e) { //
							 * TODO Auto-generated catch block
							 * e.printStackTrace(); }
							 * 
							 * } }
							 */
	}

	private static Name_value[][] clearRecords(Name_value[][] updateRecords) {
		List<Name_value[]> validRecords = new ArrayList<Name_value[]>();
		for (Name_value[] name_values : updateRecords) {
			if (name_values != null) {
				validRecords.add(name_values);
			}
		}
		if (!validRecords.isEmpty()) {
			Name_value[][] validMatrix = new Name_value[validRecords.size()][];
			for (int i = 0; i < validRecords.size(); i++) {
				validMatrix[i] = validRecords.get(i);
			}
			return validMatrix;
		}
		return null;
	}

	private static Name_value[] clearNullList(Name_value[] updateNvList) {
		List<Name_value> validUpdates = new ArrayList<Name_value>();
		boolean containsId = false;
		for (Name_value name_value : updateNvList) {
			if (!name_value.getName().equals(Name_value.nullNameString)
					&& !name_value.getValue().isEmpty()) {
				validUpdates.add(name_value);
				if (name_value.getName().equalsIgnoreCase("id")) {
					containsId = true;
				}
			}
		}
		if (containsId && !validUpdates.isEmpty()) {
			return validUpdates.toArray(new Name_value[validUpdates.size()]);
		}
		return null;
	}

	private static void updateModule(String moduleName,
			Name_value[][] updateRecords) {
		if (updateRecords == null) {
			return;
		}
		for (Name_value[] name_values : updateRecords) {
			System.out.println();
			for (Name_value name_value : name_values) {
				System.out.println(name_value.getName() + " "
						+ name_value.getValue());
			}
			System.out.println();
		}
		SugarSession session = SugarSession.getInstance();
		if (session.loginForSandusky()) {
			try {
				session.getSugar().set_entries(session.getSessionId(),
						moduleName, updateRecords);

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static Map<String, Map<String, String>> loadModuleMap(
			List<String> moduleNames) {
		return loadModuleMap(moduleNames, null);
	}

	public static Map<String, Map<String, String>> loadModuleMap(
			List<String> moduleNames, String query) {
		for (String moduleName : moduleNames) {
			if (!moduleMap.containsKey(moduleName)) {
				moduleMap.put(moduleName, loadModule(moduleName, query));
			}
		}
		return moduleMap;
	}

}
