package com.g2dev.output.ext;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.g2dev.connect.DbField;
import com.g2dev.job.cleanup.AssignTeamsToRecords;
import com.g2dev.job.custom.NewRowListener;
import com.g2dev.map.IntegrationMap;
import com.g2dev.output.Output;
import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.Name_value;
import com.sugarcrm.www.sugarcrm.New_set_entries_result;
import com.sugarcrm.www.sugarcrm.SugarsoapPortType;

public class SugarOutputProcessorCSV extends Output {

	private String moduleName;
	private IntegrationMap map;
	private NewRowListener newRowListener;
	private boolean removeDuplicates = true;
	private boolean checkDeletedDuplicates = true;
	final static Logger logger = Logger
			.getLogger(SugarOutputProcessorCSV.class);
	// private static final String FIELD_NAME_MARKET = "market_c";
	// private static final String FIELD_NAME_TEAM_ID = "team_id";
	// private static final Map<String, String> marketTeams = new
	// HashMap<String, String>();
	// private static final Map<String, String> marketTeamSets = new
	// HashMap<String, String>();
	// static {
	// marketTeams.put("Ogden", "eb3bf958-6003-e6e5-1218-5550aa4e91e8");
	// marketTeams.put("Tandem", "de180f25-11fd-11d1-9cc3-5550ab58d9ca");
	//
	// marketTeamSets.put("Ogden", "");
	// marketTeamSets.put("Tandem", "");
	// }

	private AssignTeamsToRecords assignTeamsToRecords = new AssignTeamsToRecords();

	public SugarOutputProcessorCSV(String moduleName, IntegrationMap map) {
		this(moduleName, map, null);
	}

	public SugarOutputProcessorCSV(String moduleName, IntegrationMap map,
			NewRowListener newRowListener) {
		this.moduleName = moduleName;
		this.setMap(map);
		this.setNewRowListener(newRowListener);
	}

	public void process(List<String[]> batch) {
		if (newRowListener != null) {
			newRowListener.beforeProcessBatch(batch);
		}
		List<List<Name_value>> targetBatch = new ArrayList<List<Name_value>>();
		for (String[] fieldValues : batch) {
			List<Name_value> targetRow = new ArrayList<Name_value>();
			for (DbField fromField : map.getFromToMap().keySet()) {
				DbField toField = map.getFromToMap().get(fromField);
				try {
					targetRow.add(new Name_value(toField.getName(),
							fieldValues[fromField.getIndex()]));
				} catch (Exception e) {
					logger.error("Name value error. fieldName: " + fromField, e);
				}
			}
			if (newRowListener != null) {
				List<Name_value> feedBack = newRowListener
						.newRowEvent(targetRow);
				if (feedBack != null) {
					targetRow.addAll(feedBack);
				}
			}
			// checkAndAddTeam(targetRow);
			targetBatch.add(targetRow);
		}
		try {
			SugarSession session = SugarSession.getInstance();
			session.loginForSandusky();
			SugarsoapPortType client = session.getSugar();
			// // >>>
			// Map<Integer, Name_value> hashMap = new HashMap<Integer,
			// Name_value>();
			// for (List<Name_value> list : targetBatch) {
			// for (Name_value name_value : list) {
			// if (hashMap.containsKey(name_value.hashCode())) {
			// Name_value name_value2 = hashMap.get(name_value
			// .hashCode());
			// System.out.println(name_value2.getName());
			// System.out.println(name_value2.hashCode());
			// } else {
			// hashMap.put(name_value.hashCode(), name_value);
			// }
			// ;
			// }
			// }
			// // <<

			if (removeDuplicates) {
				targetBatch = removeDuplicateIds(targetBatch);
			}
			New_set_entries_result result = null;
			if (newRowListener != null) {
				List<List<Name_value>> trimRows = newRowListener
						.trimRows(targetBatch);
				if (trimRows != null) {
					targetBatch = trimRows;
				}
			}
			if (!targetBatch.isEmpty()) {
				result = client.set_entries(session.getSessionId(),
						getModuleName(),
						toMatrix(targetBatch, targetBatch.get(0).size()));
			}

			if (result != null
			/*
			 * && (result.getError() == null || result.getError().getNumber() ==
			 * null || result .getError().getNumber().equals("0"))
			 */) {
				logger.info("Batch with size " + targetBatch.size()
						+ " loaded succesfuly");
				// newRowListener.afterInsertBatch(targetBatch);
				// System.out.println("IDS>>");
				// for (String id : result.getIds()) {
				// System.out.println(id);
				// }
				// System.out.println("<<IDS");
				try {
					// TODO this list should be only with ID-s
					for (int i = 0; i < targetBatch.size(); i++) {
						targetBatch.get(i).add(
								new Name_value("id", result.getIds()[i]));
					}
					assignTeamsToRecords.assignTeamToBatch(targetBatch,
							moduleName, true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (newRowListener != null) {
				newRowListener.afterInsertBatch(targetBatch);
			}
		} catch (Exception e) {
			logger.error("Failed to push batch to server.", e);
			e.printStackTrace();
			if (isForceInput()) {
				logger.warn("Force input. batch reload");
				process(batch);
			}
		}
	}

	// private void checkAndAddTeam(List<Name_value> targetRow) {
	// boolean teamIsSet = false;
	// String market = "";
	// for (Name_value name_value : targetRow) {
	// if (name_value.getName().equals(FIELD_NAME_TEAM_ID)
	// && name_value.getValue() != null
	// && !name_value.getValue().isEmpty()) {
	// teamIsSet = true;
	// } else if (name_value.getName().equals(FIELD_NAME_MARKET)) {
	// market = name_value.getValue();
	// }
	// }
	// if (!teamIsSet && market != null && !market.isEmpty()) {
	// targetRow.add(new Name_value(FIELD_NAME_TEAM_ID, marketTeams
	// .get(market)));
	// }
	//
	// }

	private List<List<Name_value>> removeDuplicateIds(
			List<List<Name_value>> targetBatch) {
		List<String> ids = new ArrayList<String>();
		for (List<Name_value> list : targetBatch) {
			for (Name_value name_value : list) {
				if (name_value.getName().equalsIgnoreCase("id")) {
					ids.add(name_value.getValue());
				}
			}
		}

		String query = "";
		for (String string : ids) {
			query += " " + getModuleName().toLowerCase() + ".id='" + string
					+ "' or ";
		}
		if (query.length() > 3) {
			query = query.substring(0, query.length() - 3);
		} else {
			return targetBatch;
		}
		System.out.println(query);
		String module_name = getModuleName();
		String[] select_fields = { "id" };
		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		List<List<Name_value>> batch = new ArrayList<List<Name_value>>();
		try {
			com.sugarcrm.www.sugarcrm.Get_entry_list_result_version2 get_entry_list = session
					.getSugar().get_entry_list(session.getSessionId(),
							module_name, query, null, 0, select_fields, null,
							ids.size(), 0, false);
			com.sugarcrm.www.sugarcrm.Entry_value[] entry_list = get_entry_list
					.getEntry_list();
			List<String> existingIds = new ArrayList<String>();
			for (com.sugarcrm.www.sugarcrm.Entry_value entry_value : entry_list) {
				existingIds.add(entry_value.getId());
			}
			if (checkDeletedDuplicates) {
				get_entry_list = session.getSugar().get_entry_list(
						session.getSessionId(), module_name, query, null, 0,
						select_fields, null, ids.size(), 1, false);
				entry_list = get_entry_list.getEntry_list();
				for (com.sugarcrm.www.sugarcrm.Entry_value entry_value : entry_list) {
					existingIds.add(entry_value.getId());
				}
			}
			if (existingIds.isEmpty()) {
				return targetBatch;
			} else {
				for (List<Name_value> list : targetBatch) {
					boolean rowExist = false;
					for (Name_value name_value : list) {
						if (name_value.getName().equalsIgnoreCase("id")
								&& existingIds.contains(name_value.getValue())) {
							rowExist = true;
							break;
						}
					}
					if (!rowExist) {
						batch.add(list);
					}
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return batch;
	}

	private static Name_value[][] toMatrix(List<List<Name_value>> targetBatch,
			int noColumns) {
		Name_value[][] matrix = new Name_value[targetBatch.size()][noColumns + 1];
		for (int i = 0; i < targetBatch.size(); i++) {
			List<Name_value> list = targetBatch.get(i);
			for (int j = 0; j < list.size(); j++) {
				// try {
				matrix[i][j] = list.get(j);
				// } catch (Exception e) {
				// e.printStackTrace();
				// for (Name_value name_value : list) {
				// System.out.println(" <'" + name_value.getName()
				// + " <> " + name_value.getValue() + "'> ");
				// }
				// System.exit(0);
				// }
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

	public IntegrationMap getMap() {
		return map;
	}

	public void setMap(IntegrationMap map) {
		this.map = map;
	}

	public NewRowListener getNewRowListener() {
		return newRowListener;
	}

	public void setNewRowListener(NewRowListener newRowListener) {
		this.newRowListener = newRowListener;
	}

	public boolean isForceInput() {
		return forceInput;
	}

	public void setForceInput(boolean forceInput) {
		this.forceInput = forceInput;
	}

	public boolean isRemoveDuplicates() {
		return removeDuplicates;
	}

	public void setRemoveDuplicates(boolean removeDuplicates) {
		this.removeDuplicates = removeDuplicates;
	}

	public boolean isCheckDeletedDuplicates() {
		return checkDeletedDuplicates;
	}

	public void setCheckDeletedDuplicates(boolean checkDeletedDuplicates) {
		this.checkDeletedDuplicates = checkDeletedDuplicates;
	}

}
