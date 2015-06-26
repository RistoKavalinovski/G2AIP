package com.g2dev.job.cleanup;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.Entry_value;
import com.sugarcrm.www.sugarcrm.Name_value;
import com.sugarcrm.www.sugarcrm.SugarsoapPortType;

public class AssignTeamsToRecords {

	private static final String FIELD_NAME_MARKET = "market_c";
	private static final String FIELD_NAME_ID = "id";
	private static final String FIELD_NAME_ASSIGNED_USER = "assigned_user_id";// team_id
																				// team_set_id
	private static final String FIELD_NAME_TEAM_ID = "team_id";
	private static final String FIELD_NAME_TEAM_SET_ID = "team_set_id";
	private static final String[] MODULES = { "Accounts", "Opportunities",
			"Leads", "Contacts" };

	private static final Map<String, String> marketTeams = new HashMap<String, String>();
	private static final Map<String, String> marketTeamSets = new HashMap<String, String>();
	static {
		marketTeams.put("Ogden", "eb3bf958-6003-e6e5-1218-5550aa4e91e8");
		marketTeams.put("Tandem", "de180f25-11fd-11d1-9cc3-5550ab58d9ca");

		marketTeamSets.put("Ogden", "");
		marketTeamSets.put("Tandem", "");
	}

	private Map<String, String> userIDTeamIdMap = new HashMap<String, String>();
	private Map<String, String> userIDTeamSetIdMap = new HashMap<String, String>();
	private Map<String, List<String>> userIDTeamIdMap1 = new HashMap<String, List<String>>();
	private Map<String, List<String>> userIDTeamSetIdMap1 = new HashMap<String, List<String>>();
	private List<String> modules;
	private int requestsPerSecond = 8;

	public AssignTeamsToRecords() {
		this(false);
	}

	public AssignTeamsToRecords(boolean loadUsers) {
		if (loadUsers) {
			loadUsers1();
		}
	}

	public static void main1(String[] args) {
		// new AssignTeamsToRecords().start1(true);
		AssignTeamsToRecords assignTeamsToRecords = new AssignTeamsToRecords();
		loadTeams();
		loadTeamSets();
		assignTeamsToRecords.loadUsers1();
		assignTeamsToRecords.startModule1("Opportunities", true);

	}

	public static void main(String[] args) {
		test1();
		SugarSession.getInstance().printModuleRelateFields("Accounts");
	}

	private static void test1() {
		String[] moduleNames = { "Tasks", "Calls", "Meetings", "Accounts",
				"Contacts", "Opportunities" };
		SugarSession.getInstance().loginForSandusky();
		for (String moduleName : moduleNames) {
			SugarSession.getInstance();
			new AssignTeamsToRecords(false).assignTeamToBatch(
					SugarSession.getModuleIds("Ogden", moduleName), "Ogden",
					moduleName, true);
		}

	}

	// private static void test3() {
	// new AssignAppropriateTeamsToRecords().loadUsers1();
	// }
	//
	// private static void test2() {
	// SugarSession.getInstance().loginForSandusky();
	// new AssignAppropriateTeamsToRecords().setRelationship("Accounts",
	// "teams", "0233603607e00389258655dedee249f3",
	// Arrays.asList("eb3bf958-6003-e6e5-1218-5550aa4e91e8"));
	//
	// }
	//
	// private static void test1() {
	// SugarSession session = SugarSession.getInstance();
	// session.loginForSandusky();
	// SugarsoapPortType client = session.getSugar();
	// String[] select_fields = { "id", "name" };
	// String[] select_relate_fields = { "id", "name" };
	// try {
	// Entry_value[] entry_list = client.get_entry_list(
	// session.getSessionId(), "Accounts", null, null, 0,
	// select_fields, null, 2, 0, false).getEntry_list();
	// for (Entry_value entry_value : entry_list) {
	// Get_entry_result_version2 get_relationships = client
	// .get_relationships(session.getSessionId(), "Accounts",
	// entry_value.getId(), "team_link", null,
	// select_relate_fields, null, 0, null, 0,
	// Integer.MAX_VALUE);
	// System.out.println(get_relationships);
	// }
	// } catch (RemoteException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }

	private void setRelationship(String moduleName, String linkName,
			String moduleId, List<String> relateIds,
			Name_value[] name_value_list) {
		SugarSession session = SugarSession.getInstance();
		SugarsoapPortType client = session.getSugar();
		try {
			client.set_relationship(session.getSessionId(), moduleName,
					moduleId, linkName,
					relateIds.toArray(new String[relateIds.size()]),
					name_value_list, 0);
			System.out.println("Team relationship set. Module: '" + moduleName
					+ "' id: '" + moduleId + "' relatedIds " + relateIds);
		} catch (RemoteException e) {
			if (exceptionContainsString("Invalid Session ID", e)) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				SugarSession.getInstance().loginForSandusky();
				setRelationship(moduleName, linkName, moduleId, relateIds,
						name_value_list);
			} else if (exceptionContainsString("(500)Server Error", e)) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				SugarSession.getInstance().loginForSandusky();
				setRelationship(moduleName, linkName, moduleId, relateIds,
						name_value_list);
				System.out.println("(500)Server Error ----- retry  ");
			} else {
				// e.printStackTrace();

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				SugarSession.getInstance().loginForSandusky();
				setRelationship(moduleName, linkName, moduleId, relateIds,
						name_value_list);
				System.out.println("Server Error ----- retry  ");

			}
		}
	}

	private boolean exceptionContainsString(String s, Throwable t) {
		if (t != null && t.getLocalizedMessage() != null) {
			if ((t.getLocalizedMessage().contains(s) || t.getMessage()
					.contains(s))) {
				return true;
			} else {
				return exceptionContainsString(s, t.getCause());
			}
		}
		return false;
	}

	private void setRelationship(String moduleName, String linkName,
			String moduleId, List<String> relateIds) {
		setRelationship(moduleName, linkName, moduleId, relateIds, null);
	}

	public void start(boolean multiThreed) {
		loadTeams();
		loadTeamSets();
		loadUsers();
		for (final String moduleName : getModules()) {
			if (multiThreed) {
				new Thread(new Runnable() {

					public void run() {
						startModule(moduleName);
					}
				}).start();
			} else {
				startModule(moduleName);
			}
		}

	}

	public void start1(final boolean multiThreed) {
		loadTeams();
		loadTeamSets();
		loadUsers1();
		for (final String moduleName : getModules()) {
			if (multiThreed) {
				new Thread(new Runnable() {

					public void run() {
						startModule1(moduleName, multiThreed);
					}
				}).start();
			} else {
				startModule1(moduleName, multiThreed);
			}
		}

	}

	private void startModule(String moduleName) {
		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		SugarsoapPortType client = session.getSugar();
		try {
			int recordsCount = client.get_entries_count(session.getSessionId(),
					moduleName, null, 0).getResult_count();
			int offset = 0;
			int batchSize = 200;
			while (offset < recordsCount) {
				processBatch(moduleName, batchSize, offset);
				offset += batchSize;
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void startModule1(String moduleName, boolean multiThread) {
		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		SugarsoapPortType client = session.getSugar();
		try {
			int recordsCount = client.get_entries_count(session.getSessionId(),
					moduleName, null, 0).getResult_count();
			int offset = 0;
			int batchSize = 200;
			while (offset < recordsCount) {
				processBatch1(moduleName, batchSize, offset, multiThread);
				offset += batchSize;
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final String[] M_SELECT_FIELDS = { FIELD_NAME_ID,
			FIELD_NAME_ASSIGNED_USER, FIELD_NAME_MARKET };

	private void processBatch(String moduleName, int batchSize, int offset) {
		// List<List<Name_value>> rows = new ArrayList<List<Name_value>>();
		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		SugarsoapPortType client = session.getSugar();
		try {
			Entry_value[] entry_list = client.get_entry_list(
					session.getSessionId(), moduleName, null, null, offset,
					M_SELECT_FIELDS, null, batchSize, 0, false).getEntry_list();
			Name_value[][] updateBatch = new Name_value[entry_list.length][3];
			for (Entry_value entry_value : entry_list) {
				Name_value[] name_value_list = entry_value.getName_value_list();
				for (int i = 0; i < name_value_list.length; i++) {
					Name_value name_value = name_value_list[i];
					String assignedUserId = "";
					String market = "";
					if (name_value.getName().equalsIgnoreCase(
							FIELD_NAME_ASSIGNED_USER)) {
						assignedUserId = name_value.getValue();
					} else if (name_value.getName().equalsIgnoreCase(
							FIELD_NAME_ID)) {
						updateBatch[i][0] = name_value;
					} else if (name_value.getName().equalsIgnoreCase(
							FIELD_NAME_MARKET)) {
						market = name_value.getValue();
					}
					Name_value teamNV = new Name_value(FIELD_NAME_TEAM_ID,
							marketTeams.get(market));
					Name_value teamSetNV = new Name_value(
							FIELD_NAME_TEAM_SET_ID, marketTeamSets.get(market));
					if (assignedUserId != null && !assignedUserId.isEmpty()) {
						synchronized (userIDTeamIdMap) {
							System.out.println(">>>>>>>>>>>>> userid "
									+ assignedUserId + " >> recordID >> "
									+ entry_value.getId());
							if (userIDTeamIdMap.containsKey(assignedUserId)) {
								teamNV.setValue(userIDTeamIdMap
										.get(assignedUserId));
							}
						}
						synchronized (userIDTeamSetIdMap) {
							if (userIDTeamSetIdMap.containsKey(assignedUserId)) {
								teamSetNV.setValue(userIDTeamSetIdMap
										.get(assignedUserId));
							}
						}
					}

					updateBatch[i][1] = teamNV;
					updateBatch[i][2] = teamSetNV;
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		// return rows;
	}

	private void processBatch1(final String moduleName, int batchSize,
			int offset, boolean multiThread) {
		// List<List<Name_value>> rows = new ArrayList<List<Name_value>>();
		SugarSession session = SugarSession.getInstance();

		try {
			session.loginForSandusky();
			SugarsoapPortType client = session.getSugar();
			Entry_value[] entry_list = client.get_entry_list(
					session.getSessionId(), moduleName, null, null, offset,
					M_SELECT_FIELDS, null, batchSize, 0, false).getEntry_list();
			for (final Entry_value entry_value : entry_list) {
				final Name_value[] name_value_list = entry_value
						.getName_value_list();
				String assignedUserId = "";
				String market = "";
				for (int i = 0; i < name_value_list.length; i++) {
					Name_value name_value = name_value_list[i];

					if (name_value.getName().equalsIgnoreCase(
							FIELD_NAME_ASSIGNED_USER)) {
						assignedUserId = name_value.getValue();
					} else if (name_value.getName().equalsIgnoreCase(
							FIELD_NAME_MARKET)) {
						market = name_value.getValue();
					}
				}
				final List<String> teamIds = new ArrayList<String>();
				if (market != null && !market.isEmpty()) {
					teamIds.add(marketTeams.get(market));
				}
				if (assignedUserId != null && !assignedUserId.isEmpty()) {
					System.out.println(">>>>>>>>>>>>> userid " + assignedUserId
							+ " >> recordID >> " + entry_value.getId());
					synchronized (userIDTeamIdMap1) {
						if (userIDTeamIdMap1.containsKey(assignedUserId)) {
							teamIds.addAll(userIDTeamIdMap1.get(assignedUserId));
						}
					}
				}
				if (!teamIds.isEmpty()) {
					if (multiThread) {
						new Thread(new Runnable() {

							public void run() {
								setTeamRelationship(moduleName,
										entry_value.getId(), teamIds);
							}
						}).start();
						;
					} else {
						setTeamRelationship(moduleName, entry_value.getId(),
								teamIds);
					}
				} else {
					System.out.println("No team set for record. Module: '"
							+ moduleName + "' id: '" + entry_value.getId()
							+ "'");
				}
			}
		} catch (RemoteException e) {
			if (exceptionContainsString("503", e)) {
				processBatch1(moduleName, batchSize, offset, multiThread);
			} else {
				e.printStackTrace();
			}
		}
		// return rows;
	}

	private void setTeamRelationship(String moduleName, String id,
			List<String> teamIds) {
		// teams adds a team to record
		// team_id adds a team to record and sets it as primary
		setRelationship(moduleName, "teams", id, teamIds);
		setRelationship(moduleName, "team_id", id, teamIds);

	}

	private void loadUsers() {
		// team_id | Default Teams | id
		// team_set_id | Team Set ID | id

		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		SugarsoapPortType client = session.getSugar();
		String module_name = "Users";
		String[] select_fields = { "id", "team_id", "team_set_id" };
		try {
			Entry_value[] entry_list = client.get_entry_list(
					session.getSessionId(), module_name, null, null, 0,
					select_fields, null, Integer.MAX_VALUE, 0, false)
					.getEntry_list();
			System.out.println("---Users---");
			for (Entry_value entry_value : entry_list) {
				Name_value[] name_value_list = entry_value.getName_value_list();
				for (Name_value name_value : name_value_list) {
					if (name_value.getName().equals("team_id")) {
						userIDTeamIdMap.put(entry_value.getId(),
								name_value.getValue());
					} else if (name_value.getName().equals("team_set_id")) {
						userIDTeamSetIdMap.put(entry_value.getId(),
								name_value.getValue());
					}
				}
			}
			System.out.println("---^^^^^---" + userIDTeamIdMap.size() + " "
					+ userIDTeamSetIdMap.size());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void loadUsers1() {
		// team_id | Default Teams | id
		// team_set_id | Team Set ID | id

		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		SugarsoapPortType client = session.getSugar();
		String module_name = "Teams";
		String[] select_fields = { "id", };
		try {
			Entry_value[] entry_list = client.get_entry_list(
					session.getSessionId(), module_name, null, null, 0,
					select_fields, null, Integer.MAX_VALUE, 0, false)
					.getEntry_list();
			System.out.println("---Teams---");
			for (Entry_value entry_value : entry_list) {
				String[] related_fields = { "id", "name" };
				Entry_value[] entry_list2 = client.get_relationships(
						session.getSessionId(), "Teams", entry_value.getId(),
						"users", null, related_fields, null, 0, null, 0,
						Integer.MAX_VALUE).getEntry_list();
				for (Entry_value entry_value2 : entry_list2) {
					if (!userIDTeamIdMap1.containsKey(entry_value2.getId())) {
						userIDTeamIdMap1.put(entry_value2.getId(),
								new ArrayList<String>());
					}
					userIDTeamIdMap1.get(entry_value2.getId()).add(
							entry_value.getId());
				}
			}
			System.out.println("---TeamSets---");
			entry_list = client.get_entry_list(session.getSessionId(),
					"TeamSets", null, null, 0, select_fields, null,
					Integer.MAX_VALUE, 0, false).getEntry_list();
			for (Entry_value entry_value : entry_list) {
				String[] related_fields = { "id", "name" };
				Entry_value[] entry_list2 = new Entry_value[00];
				try {
					entry_list2 = client.get_relationships(
							session.getSessionId(), "Teams",
							entry_value.getId(), "users", null, related_fields,
							null, 0, null, 0, Integer.MAX_VALUE)
							.getEntry_list();
				} catch (Exception e) {
					// this is expected since there is an exception if there is
					// no value here
				}
				for (Entry_value entry_value2 : entry_list2) {
					if (!userIDTeamSetIdMap1.containsKey(entry_value2.getId())) {
						userIDTeamSetIdMap1.put(entry_value2.getId(),
								new ArrayList<String>());
					}
					userIDTeamSetIdMap1.get(entry_value2.getId()).add(
							entry_value.getId());
				}
			}
			System.out.println("---^^^^^---" + userIDTeamIdMap1.size() + " "
					+ userIDTeamSetIdMap1.size());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void loadTeams() {
		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		SugarsoapPortType client = session.getSugar();
		String module_name = "Teams";
		String[] select_fields = { "id", "name" };
		try {
			Entry_value[] entry_list = client.get_entry_list(
					session.getSessionId(), module_name, null, null, 0,
					select_fields, null, Integer.MAX_VALUE, 0, false)
					.getEntry_list();
			System.out.println("---Teams---");
			for (Entry_value entry_value : entry_list) {
				Name_value[] name_value_list = entry_value.getName_value_list();
				for (Name_value name_value : name_value_list) {
					System.out.print(name_value.getValue() + ", ");
				}
				System.out.println();
			}
			System.out.println("---^^^^^---");
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	private static void loadTeamSets() {
		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		SugarsoapPortType client = session.getSugar();
		String module_name = "TeamSets";
		String[] select_fields = { "id", "name", "team_count" };
		try {
			Entry_value[] entry_list = client.get_entry_list(
					session.getSessionId(), module_name, null, null, 0,
					select_fields, null, Integer.MAX_VALUE, 0, false)
					.getEntry_list();
			System.out.println("---TeamSets---");
			for (Entry_value entry_value : entry_list) {
				Name_value[] name_value_list = entry_value.getName_value_list();
				for (Name_value name_value : name_value_list) {
					System.out.print(name_value.getValue() + ", ");
				}
				System.out.println();
			}
			System.out.println("---^^^^^---");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<String> getModules() {
		if (modules == null) {
			return Arrays.asList(MODULES);
		}
		return modules;
	}

	public void setModules(List<String> modules) {
		this.modules = modules;
	}

	public void assignTeamToBatch(List<String> ids, String market,
			final String moduleName, boolean multiThread) {
		int requests = 0;
		for (final String id : ids) {
			if (id != null && !id.isEmpty()) {
				final List<String> teamIds = new ArrayList<String>();
				if (market != null && marketTeams.containsKey(market)) {
					teamIds.add(marketTeams.get(market));
				}
				if (!teamIds.isEmpty()) {
					if (multiThread) {
						new Thread(new Runnable() {
							public void run() {
								setTeamRelationship(moduleName, id, teamIds);
							}
						}).start();
						requests++;
						if (requests >= requestsPerSecond) {
							try {
								Thread.sleep(1000);
								requests = 0;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} else {
						setTeamRelationship(moduleName, id, teamIds);
					}
				}
			}
		}

	}

	public void assignTeamToBatch(List<List<Name_value>> batchatch,
			final String moduleName, boolean multiThread) {
		int requests = 0;
		for (List<Name_value> row : batchatch) {
			String market = getValue(FIELD_NAME_MARKET, row);
			String assignedUser = getValue(FIELD_NAME_ASSIGNED_USER, row);
			final String id = getValue(FIELD_NAME_ID, row);
			if (id != null && !id.isEmpty()) {
				final List<String> teamIds = new ArrayList<String>();
				if (market != null && marketTeams.containsKey(market)) {
					teamIds.add(marketTeams.get(market));
				}
				if (assignedUser != null
						&& userIDTeamIdMap1.containsKey(assignedUser)) {
					teamIds.addAll(userIDTeamIdMap1.get(assignedUser));
				}
				if (!teamIds.isEmpty()) {
					if (multiThread) {
						new Thread(new Runnable() {
							public void run() {
								setTeamRelationship(moduleName, id, teamIds);
							}
						}).start();
						requests++;
						if (requests >= requestsPerSecond) {
							try {
								Thread.sleep(1000);
								requests = 0;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					} else {
						setTeamRelationship(moduleName, id, teamIds);
					}
				}
			}
		}
	}

	private String getValue(String name, List<Name_value> row) {
		for (Name_value name_value : row) {
			if (name_value.getName().equalsIgnoreCase(name)) {
				return name_value.getValue();
			}
		}
		return null;
	}

	public int getRequestsPerSecond() {
		return requestsPerSecond;
	}

	public void setRequestsPerSecond(int requestsPerSecond) {
		this.requestsPerSecond = requestsPerSecond;
	}

}
