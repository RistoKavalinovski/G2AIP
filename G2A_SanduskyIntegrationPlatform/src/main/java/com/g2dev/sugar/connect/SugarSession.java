package com.g2dev.sugar.connect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.g2dev.job.custom.ogden.migration.OgdenSugarSession;
import com.g2dev.job.custom.ogden.migration.workflow.SetRelationship;
import com.g2dev.job.custom.ogden.migration.workflow.SetRelationship.RelationshipUpdateListener;
import com.sugarcrm.www.sugarcrm.Entry_value;
import com.sugarcrm.www.sugarcrm.Field;
import com.sugarcrm.www.sugarcrm.Get_entry_result_version2;
import com.sugarcrm.www.sugarcrm.Link_field;
import com.sugarcrm.www.sugarcrm.Module_list;
import com.sugarcrm.www.sugarcrm.Module_list_entry;
import com.sugarcrm.www.sugarcrm.Name_value;
import com.sugarcrm.www.sugarcrm.New_module_fields;
import com.sugarcrm.www.sugarcrm.New_set_entries_result;
import com.sugarcrm.www.sugarcrm.New_set_entry_result;
import com.sugarcrm.www.sugarcrm.Return_search_result;
import com.sugarcrm.www.sugarcrm.Search_link_name_value;
import com.sugarcrm.www.sugarcrm.SugarsoapLocator;
import com.sugarcrm.www.sugarcrm.SugarsoapPortType;
import com.sugarcrm.www.sugarcrm.User_auth;
import com.sugarcrm.www.sugarcrm.rest.SugarRestSession;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarApiException;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarClient;
import com.sugarcrm.www.sugarcrm.rest.v4.impl.SugarBean;

public class SugarSession {
	private User_auth userAuth;
	private SugarsoapPortType sugar;
	private static SugarSession session;
	private String sessionId;
	final static Logger logger = Logger.getLogger(SugarSession.class);

	private String username;
	private String password;
	private boolean forceLogin;

	public static SugarSession getInstance() {
		if (session == null) {
			session = new SugarSession();
		}
		return session;
	}

	public SugarSession() {
		// https://sanduskydev.sugarondemand.com/
		// TODO remove the following 2 lines when on production
		// SugarsoapLocator.sugarsoapPort_address =
		// "https://sanduskydev.sugarondemand.com/soap.php";
		// SugarsoapBindingStub.sugarsoapPort_address =
		// SugarsoapLocator.sugarsoapPort_address;
	}

	// @Deprecated
	public boolean loginForSandusky() {
		// TODO check this
		// SugarsoapLocator.sugarsoapPort_address =
		// "https://sanduskydev.sugarondemand.com/soap.php";
		// SugarsoapBindingStub.sugarsoapPort_address =
		// SugarsoapLocator.sugarsoapPort_address;
		if (sessionId != null && !sessionId.isEmpty()) {
			return true;
		}
		return login("G2Dev.Sandusky", "G2@nalyt1c$2015", "7.5.1.0");
		// return login("mat", "q7Ts51Qp5J", "7.5.1.0");
	}

	public static String toMD5String(String target) {
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(target.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1, digest);
			String hashtext = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32
			// chars.
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public boolean login(String userName, String password, String version) {
		SugarsoapLocator sl = new SugarsoapLocator();
		try {
			sugar = sl.getsugarsoapPort();
		} catch (ServiceException e) {
			e.printStackTrace();
			logger.error("Soap init failed", e);
			return false;
		}
		/* userAuth = new User_auth(userName, password, version); */
		password = toMD5String(password);
		userAuth = new User_auth(userName, password);
		try {
			Entry_value login = sugar.login(userAuth, "sanduskynews", null);
			setSessionId(login.getId());
			/* Set_entry_result login = sugar.login(userAuth, "sanduskynews"); */
			/*
			 * Error_value error = login.getError(); if
			 * (error.getNumber().equals("0")) { } else {
			 * logger.error("Log IN error >> "); logger.error("error name" +
			 * error.getName()); logger.error("error number" +
			 * error.getNumber()); logger.error("error description" +
			 * error.getDescription()); logger.error("Log IN error << "); return
			 * false; }
			 */
		} catch (RemoteException e) {
			e.printStackTrace();
			logger.error("Log error", e);
			if (forceLogin) {
				return login(userName, password, version);
			}
			return false;
		}
		logger.info("Log In success.");
		this.username = userName;
		this.password = password;
		return true;

	}

	public boolean isConnected() {
		return getSessionId() != null;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public SugarsoapPortType getSugar() {
		return sugar;
	}

	public void setSugar(SugarsoapPortType sugar) {
		this.sugar = sugar;
	}

	public static void main2(String[] args) {
		// getInstance().printRecordCounts(null);
		// getInstance().printModuleRecordCounts(null, "Leads");
		// getInstance().printModules();
		// getInstance().printModuleFields("Meetings");
		// testTeams();

		// parent_name
		// getInstance().loginForSandusky();
		// setOgdenUserEmails();
		// replaceApostropheFromName();
		// removeSNG();

		// List<String> meetingsNoParent = getMeetingsNoParent();
		// System.out.println(meetingsNoParent);

		// pullDeletedRecordIDs("Accounts");
		// pullRecordIDs("Accounts");

		// List<String> deletedRecordIDs = loadDeletedRecordIDs("Accounts");
		// List<String> loadRecordIDs = loadRecordIDs("Accounts");
		// System.out.println(deletedRecordIDs);
		// System.out.println(loadRecordIDs);

		// OgdenSugarSession instance = OgdenSugarSession.getInstance();
		// instance.loginForOgden();
		// String[] select_fields = { "id", "name", "deleted" };
		// try {
		// System.out.println(instance
		// .getSugar()
		// .get_entries_count(instance.getSessionId(), "Meetings",
		// null, 0).getResult_count());
		// } catch (RemoteException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// cleanMeetings();
		// cleanCalls();
		// cleanTasks();

		// String[] selectFields = { "id", "deleted", "date_modified" };
		// getInstance().loginForSandusky();
		// try {
		// Get_entry_result_version2 get_entry = getInstance().getSugar()
		// .get_entry(getInstance().getSessionId(), "Accounts",
		// "0251bb84c61655cb16fc89bdb0fd958e", selectFields,
		// null, false);
		// Entry_value[] entry_list = get_entry.getEntry_list();
		// for (Entry_value entry_value : entry_list) {
		// System.out.println(entry_value.getId());
		// Name_value[] name_value_list = entry_value.getName_value_list();
		// for (Name_value name_value : name_value_list) {
		// System.out.println(name_value.getName() + " "
		// + name_value.getValue());
		// }
		// }
		//
		// } catch (RemoteException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	private static SetRelationship setRelationship;

	public static void main(String[] args) {
		getInstance().loginForSandusky();
		pullDeletedRecordIDs("Accounts");
		// getInstance().printModuleFields("Meetings");
		// List<String> meetingsNoStartDate = getMeetingsNoStartDate("Ogden");
		// System.out.println(meetingsNoStartDate.size());
		OgdenSugarSession ogdenSugarSession = OgdenSugarSession.getInstance();
		ogdenSugarSession.loginForOgden();
		try {
			System.out.println(ogdenSugarSession
					.getSugar()
					.get_entries_count(ogdenSugarSession.getSessionId(),
							"Accounts", null, 0).getResult_count());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getInstance().loginForSandusky();
		try {
			System.out.println(getInstance()
					.getSugar()
					.get_entries_count(getInstance().getSessionId(),
							"Accounts", null, 1).getResult_count());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// OgdenSugarSession.getInstance().printModuleRelationshipFields(
		// "Documents");

	}

	public static void main7(String[] args) {
		OgdenSugarSession.getInstance().printModuleRelationshipFields(
				"Documents");
		getInstance().loginForSandusky();
		String moduleName = "Documents";
		String relationshipName = "opportunities";
		setRelationship = new SetRelationship(moduleName, "Opportunities",
				relationshipName, 8, new RelationshipUpdateListener() {

					public void beforeUpdate(String id, List<String> relatedIds) {

						System.out.println("Document id" + id
								+ " Related ids: " + relatedIds);
					}
				});
		setRelationship.start(getModuleIds("Ogden", moduleName));
	}

	public static void main5(String[] args) {
		getInstance().loginForSandusky();
		String moduleName = "Meetings";
		String relationshipName = "accounts";
		// pullDeletedRecordIDs("Accounts");
		// pullRecordIDs("Accounts");
		final List<String> deletedAccountIDs = loadDeletedRecordIDs("Accounts");
		final List<String> accountIDs = loadRecordIDs("Accounts");
		System.out.println(deletedAccountIDs.size()
				+ " deleted accounts loaded");

		final Set<String> referencedDeletedAccounts = new HashSet<String>();
		final Set<String> notExistingAccounts = new HashSet<String>();
		final Set<String> relatedExistingAccounts = new HashSet<String>();

		setRelationship = new SetRelationship(moduleName, "Accounts",
				relationshipName, 8, new RelationshipUpdateListener() {

					public void beforeUpdate(String id, List<String> relatedIds) {

						for (String relID : relatedIds) {
							if (deletedAccountIDs.contains(relID)) {
								referencedDeletedAccounts.add(relID);
							} else if (!accountIDs.contains(relID)) {
								notExistingAccounts.add(relID);
								setRelationship.migrateRelatedRecord(relID);
								accountIDs.add(relID);
							} else {
								relatedExistingAccounts.add(relID);
							}
							System.out.println(id + " deletedAccountIDs "
									+ referencedDeletedAccounts.size()
									+ "; notExistingAccounts "
									+ notExistingAccounts.size()
									+ "; relatedExistingAccounts "
									+ relatedExistingAccounts.size());
						}
					}
				});
		setRelationship.start(getMeetingsNoParent("Ogden"));
		;
		System.out.println(deletedAccountIDs);
		System.out.println(notExistingAccounts);
		System.out.println(relatedExistingAccounts);
	}

	private static void removeSNG() {
		int numberOfRecords = 778 - 400 - 180 - 180 - 18;
		int offset = -200;
		int batchSize = 200;
		String[] selectFields = { "id", "date_entered", "deleted" };
		while (offset < numberOfRecords) {
			offset += batchSize;
			try {
				Entry_value[] entry_list = getInstance()
						.getSugar()
						.get_entry_list(getInstance().getSessionId(),
								"SNGR1_SNGRevenueDetail", null, "date_entered",
								offset, selectFields, null, batchSize, 0, false)
						.getEntry_list();
				Name_value toDelete[][] = new Name_value[entry_list.length][];
				for (int i = 0; i < entry_list.length; i++) {
					Entry_value entry_value = entry_list[i];
					Name_value[] name_value_list = entry_value
							.getName_value_list();
					for (Name_value name_value : name_value_list) {
						if (name_value.getName().equals("deleted")) {
							name_value.setValue("1");
						}
					}
					toDelete[i] = name_value_list;
				}
				String[] ids = getInstance()
						.getSugar()
						.set_entries(getInstance().getSessionId(),
								"SNGR1_SNGRevenueDetail", toDelete).getIds();
				System.out.println(ids);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static void setOgdenUserEmails() {
		OgdenSugarSession ogdenSession = OgdenSugarSession.getInstance();
		ogdenSession.loginForOgden();
		com.g2dev.job.custom.ogden.migration.sugar.SugarsoapPortType ogdenClient = ogdenSession
				.getSugar();
		int numberOfRecords = 0;
		try {
			numberOfRecords = ogdenClient.get_entries_count(
					ogdenSession.getSessionId(), "Users", null, 0)
					.getResult_count();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int offset = -100;
		int batchSize = 100;
		String selectFields[] = { "id", "email", "email1" };
		while (offset < numberOfRecords) {
			offset += batchSize;
			try {
				com.g2dev.job.custom.ogden.migration.sugar.Entry_value[] entry_list = ogdenClient
						.get_entry_list(ogdenSession.getSessionId(), "Users",
								null, null, offset, selectFields, null,
								batchSize, 0, false).getEntry_list();
				for (com.g2dev.job.custom.ogden.migration.sugar.Entry_value entry_value : entry_list) {
					if (entry_value.getId().equalsIgnoreCase("0")
							|| entry_value.getId().equalsIgnoreCase("1")) {
						continue;
					}
					SugarsoapPortType client = getInstance().getSugar();
					Get_entry_result_version2 userEntry = client.get_entry(
							getInstance().getSessionId(), "Users",
							entry_value.getId(), selectFields, null, false);
					Entry_value[] entry_list2 = userEntry.getEntry_list();
					for (Entry_value entry_value2 : entry_list2) {
						Name_value[] name_value_list = entry_value2
								.getName_value_list();
						for (Name_value name_value : name_value_list) {
							if (name_value.getName().equalsIgnoreCase("email")) {
								String email = "";
								com.g2dev.job.custom.ogden.migration.sugar.Name_value[] name_value_list2 = entry_value
										.getName_value_list();
								for (com.g2dev.job.custom.ogden.migration.sugar.Name_value name_value2 : name_value_list2) {
									if (name_value2.getName().equalsIgnoreCase(
											"email")) {
										email = name_value2.getValue();
									}
								}
								name_value.setValue(email);
							} else if (name_value.getName().equalsIgnoreCase(
									"email1")) {
								String email = "";
								com.g2dev.job.custom.ogden.migration.sugar.Name_value[] name_value_list2 = entry_value
										.getName_value_list();
								for (com.g2dev.job.custom.ogden.migration.sugar.Name_value name_value2 : name_value_list2) {
									if (name_value2.getName().equalsIgnoreCase(
											"email1")) {
										email = name_value2.getValue();
									}
								}
								name_value.setValue(email);
							}
						}
						Name_value[][] entries = new Name_value[1][];
						entries[0] = name_value_list;
						New_set_entry_result set_entry = client.set_entry(
								getInstance().getSessionId(), "Users",
								name_value_list);
						System.out.println(set_entry.getId());
					}
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void replaceApostropheFromName() {
		String searchString = "%&#039;";
		String replaceString = "&#039;";
		SugarsoapPortType client = getInstance().getSugar();

		try {
			Module_list_entry[] modules = client.get_available_modules(
					getInstance().getSessionId(), null).getModules();
			List<String> moduleNamesList = new ArrayList<String>();
			for (final Module_list_entry module_list_entry : modules) {
				moduleNamesList.add(module_list_entry.getModule_key());
			}

			String moduless[] = moduleNamesList
					.toArray(new String[moduleNamesList.size()]);
			String selectFields[] = { "id", "name" };
			int offset = 0;
			int batchSize = 200;
			List<String> checkedModules = new ArrayList<String>();
			checkedModules.add("Contacts");
			checkedModules.add("Emails");
			checkedModules.add("Documents");
			while (true) {
				for (int i = 0; i < moduless.length; i++) {
					String modName = moduless[i];
					if (checkedModules.contains(modName)) {
						continue;
					}
					checkedModules.add(modName);
					while (true) {
						try {
							// getInstance().getSugar().get_user_id(getInstance().getSessionId())
							String[] modNames = { modName };
							Return_search_result search_by_module = client
									.search_by_module(getInstance()
											.getSessionId(), searchString,
											modNames, offset, batchSize, null,
											selectFields, false, false);
							Search_link_name_value[] entry_list = search_by_module
									.getEntry_list();
							boolean noValues = true;
							for (Search_link_name_value search_link_name_value : entry_list) {
								String moduleName = search_link_name_value
										.getName();
								Name_value[][] records = search_link_name_value
										.getRecords();
								if (records != null && records.length > 0) {
									noValues = false;
									for (Name_value[] name_values : records) {
										for (Name_value name_value : name_values) {
											if (name_value.getName()
													.equalsIgnoreCase("name")) {
												name_value.setValue(name_value
														.getValue().replaceAll(
																replaceString,
																"'"));
											}
										}
									}
									New_set_entries_result set_entries = client
											.set_entries(getInstance()
													.getSessionId(),
													moduleName, records);
									System.out.println("No.Records updated"
											+ set_entries.getIds() == null ? 0
											: set_entries.getIds().length);
								}
							}
							if (noValues) {
								break;
							}
							System.out.println(search_by_module);
							// offset += batchSize;
						} catch (RemoteException e1) {
							// e1.printStackTrace();
							break;
						}

					}
					if (checkedModules.size() == moduless.length) {
						break;
					}
				}
			}

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void pullDeletedRecordIDs(String moduleName) {
		pullRecordIDs(moduleName, 1);
	}

	private static void pullRecordIDs(String moduleName) {
		pullRecordIDs(moduleName, 0);
	}

	private static void pullRecordIDs(String moduleName, int deleted) {
		SugarRestSession session = SugarRestSession.getInstance();
		SugarClient client = session.getSanduskyClient();
		int numberOfRecords = 0;
		try {
			numberOfRecords = client.getEntriesCount(session.getSession(),
					moduleName, null, deleted);
		} catch (SugarApiException e) {
			e.printStackTrace();
		}
		int offset = 0;
		int batchSize = 2000;
		String[] selectFields = { "id" };
		List<String> deletedIDs = new ArrayList<String>();
		while (offset < numberOfRecords) {
			try {
				Entry_value[] entry_list = getInstance()
						.getSugar()
						.get_entry_list(getInstance().getSessionId(),
								moduleName, null, null, offset, selectFields,
								null, batchSize, deleted, false)
						.getEntry_list();
				for (Entry_value entry_value : entry_list) {
					deletedIDs.add(entry_value.getId());
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			offset += batchSize;
			printProgress(offset, numberOfRecords);
		}
		File f = new File(moduleName + deleted);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		BufferedWriter fw = null;
		try {
			fw = new BufferedWriter(new FileWriter(f, false));
			for (String id : deletedIDs) {
				fw.write(id);
				fw.newLine();
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			if (fw != null) {
				try {
					fw.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private static List<String> loadDeletedRecordIDs(String moduleName) {
		return loadRecordIDs(moduleName, 1);
	}

	private static List<String> loadRecordIDs(String moduleName) {
		return loadRecordIDs(moduleName, 0);
	}

	private static List<String> loadRecordIDs(String moduleName, int deleted) {
		List<String> ids = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(moduleName
					+ deleted)));
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					ids.add(line);
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				if (br != null) {
					try {
						br.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return ids;
	}

	public static void printProgress(int offset, int numberOfRecords) {
		System.out.println(numberOfRecords + " / " + offset);
	}

	private static List<String> getMeetingsNoParent(String market) {
		// date created before 1/1/2014
		// delete meetings and calls
		List<String> ids = new ArrayList<String>();
		SugarRestSession session = SugarRestSession.getInstance();
		SugarClient client = session.getSanduskyClient();
		int entriesCount = 0;
		try {
			entriesCount = client.getEntriesCount(session.getSession(),
					"Meetings", null, 0);
		} catch (SugarApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int offset = 0;
		int batchSize = 2000;
		String selectFields[] = { "id", "parent_name", "market_c" };
		while (offset < entriesCount) {
			try {
				List<SugarBean> entryList = client.getEntryList(
						session.getSession(), "Meetings", null, null, offset,
						selectFields, batchSize, 0);
				for (SugarBean sugarBean : entryList) {
					String parentName = sugarBean.get("parent_name")
							.replaceAll("\"", "");
					String marketValue = sugarBean.get("market_c").replaceAll(
							"\"", "");
					if (marketValue.equalsIgnoreCase(market)
							&& (parentName == null || parentName.isEmpty())) {
						String id = sugarBean.get("id").replaceAll("\"", "");
						if (id != null && !id.isEmpty()) {
							ids.add(id);
						}

					} else {
						parentName = parentName;
					}
				}
			} catch (SugarApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			offset += batchSize;
		}
		return ids;
	}

	private static List<String> getMeetingsNoStartDate(String market) {
		// date created before 1/1/2014
		// delete meetings and calls
		List<String> ids = new ArrayList<String>();
		SugarRestSession session = SugarRestSession.getInstance();
		SugarClient client = session.getSanduskyClient();
		int entriesCount = 0;
		try {
			entriesCount = client.getEntriesCount(session.getSession(),
					"Meetings", null, 0);
		} catch (SugarApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int offset = 0;
		int batchSize = 2000;
		String selectFields[] = { "id", "date_end", "market_c" };
		while (offset < entriesCount) {
			try {
				List<SugarBean> entryList = client.getEntryList(
						session.getSession(), "Meetings", null, null, offset,
						selectFields, batchSize, 0);
				for (SugarBean sugarBean : entryList) {
					String parentName = sugarBean.get("date_end").replaceAll(
							"\"", "");
					String marketValue = sugarBean.get("market_c").replaceAll(
							"\"", "");
					if (marketValue.equalsIgnoreCase(market)
							&& (parentName == null || parentName.isEmpty())) {
						String id = sugarBean.get("id").replaceAll("\"", "");
						if (id != null && !id.isEmpty()) {
							ids.add(id);
						}

					} else {
						parentName = parentName;
					}
				}
			} catch (SugarApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			offset += batchSize;
		}
		return ids;
	}

	public static List<String> getModuleIds(String market, String moduleName) {
		getInstance().loginForSandusky();
		// date created before 1/1/2014
		// delete meetings and calls
		List<String> ids = new ArrayList<String>();
		SugarRestSession session = SugarRestSession.getInstance();
		SugarClient client = session.getSanduskyClient();
		int entriesCount = 0;
		try {
			entriesCount = client.getEntriesCount(session.getSession(),
					moduleName, null, 0);
		} catch (SugarApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int offset = 0;
		int batchSize = 2000;
		String selectFields[] = { "id", "market_c" };
		while (offset < entriesCount) {
			try {
				List<SugarBean> entryList = client.getEntryList(
						session.getSession(), moduleName, null, null, offset,
						selectFields, batchSize, 0);
				for (SugarBean sugarBean : entryList) {

					String marketValue = sugarBean.get("market_c").replaceAll(
							"\"", "");
					if (marketValue.equalsIgnoreCase(market)) {
						String id = sugarBean.get("id").replaceAll("\"", "");
						if (id != null && !id.isEmpty()) {
							ids.add(id);
						}

					}
				}
			} catch (SugarApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			offset += batchSize;
		}
		return ids;
	}

	private static void createDummyRecord() {
		Name_value[] name_value_list = {
				new Name_value("id", "nef947d44-fc6f-face-7434-5488cc9055c8"),
				new Name_value("name", "123") };
		try {
			New_set_entry_result set_entry = getInstance().getSugar()
					.set_entry(getInstance().getSessionId(), "Accounts",
							name_value_list);
			System.out.println(set_entry);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void undeleteREcords(Entry_value[] entry_list,
			String moduleName) {
		Name_value[][] updates = new Name_value[entry_list.length][];
		for (int i = 0; i < entry_list.length; i++) {
			Name_value[] name_value_list = entry_list[i].getName_value_list();
			for (Name_value name_value : name_value_list) {
				if (name_value.getName().equalsIgnoreCase("deleted")) {
					name_value.setValue("00");
				}
			}
			updates[i] = name_value_list;
		}
		SugarsoapPortType client = getInstance().getSugar();
		try {
			New_set_entries_result set_entries = client.set_entries(
					getInstance().getSessionId(), moduleName, updates);
			System.out.println(set_entries);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void testTeams() {
		// TeamSets //Teams
		getInstance().printModuleFields("TeamSets");

	}

	public static void main1(String[] args) {
		// System.out.println(getInstance().loginForSandusky());
		// logger.info("test log");
		// getInstance().test();

		// getInstance().printModules();
		// getInstance().printModuleFields("SNGR1_SNGRevenueDetail");

		// SugarSession session2 = getInstance();
		// session2.loginForSandusky();
		// try {
		// System.out.println(session2
		// .getSugar()
		// .get_entries_count(session2.getSessionId(), "Accounts",
		// null, 1).getResult_count());
		// } catch (RemoteException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// test2();

		// IntegrationMap im = new IntegrationMap().loadFromAccountsFile(new
		// File(StaticProperties.SNG_MAP_FILE_PATH));
		// Map<DbField, DbField> fromToMap = im.getFromToMap();
		// for (DbField from : fromToMap.keySet()) {
		// System.out.println(from.getIndex() + " | "
		// + fromToMap.get(from).getName());
		// }

		// test4();

		// getInstance().printModules();
		// getInstance().printModuleFields("RevenueLineItems");
		// OgdenSugarSession.getInstance().printModuleFields("Opportunities");
		// testCount();

		// updateRevenueLineItems();
		// getInstance().printModules();
		// getInstance().printModuleFields("Leads");
		// TandemSugarSession.getInstance().printRecordCounts(null);
		// testCount();

	}

	private static void testCount() {
		// List<String> iDs = getIDs("RevenueLineItems",
		// null); 6338
		System.out.println(getRecordsCount("Opportunities",
				"opportunities.date_closed<20140701"));
	}

	private static void updateRevenueLineItems() {
		ArrayList<Map<String, Name_value>> revenueLineItems = new ArrayList<Map<String, Name_value>>(
				getRevenueLineItems());
		int batch = 200;
		int offset = 0;
		int count = revenueLineItems.size();
		SugarSession instance = getInstance();
		instance.loginForSandusky();
		while (offset < count) {
			List<Map<String, Name_value>> recordsList = revenueLineItems
					.subList(offset, Math.min(offset + batch, count));
			Name_value[][] valuesMatrix = new Name_value[recordsList.size()][2];
			for (int i = 0; i < recordsList.size(); i++) {
				Map<String, Name_value> record = recordsList.get(i);
				if (record.containsKey("name")) {
					Name_value name_value = record.get("name");
					if (!(name_value.getValue().equalsIgnoreCase("oldcrm") || name_value
							.getValue().isEmpty())) {
						continue;
					}
					valuesMatrix[i][0] = record.get("id");
					valuesMatrix[i][1] = new Name_value("name", "oldcrm");
				}

			}
			try {
				instance.getSugar().set_entries(instance.getSessionId(),
						"RevenueLineItems", valuesMatrix);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			offset += batch;
			System.out.println("update revenueLineItems " + offset + " / "
					+ count);
		}
	}

	private static List<Map<String, Name_value>> getRevenueLineItems() {
		List<Map<String, Name_value>> records = new ArrayList<Map<String, Name_value>>();
		String[] select_fields = { "id", "name", };
		int batch = 200;
		int offset = 0;
		String moduleName = "RevenueLineItems";
		int count = getRecordsCount(moduleName, null);
		SugarSession instance = getInstance();
		instance.loginForSandusky();
		while (offset < count) {
			try {
				Entry_value[] entry_list = instance
						.getSugar()
						.get_entry_list(instance.getSessionId(), moduleName,
								null, null, offset, select_fields, null, batch,
								0, false).getEntry_list();
				for (Entry_value entry_value : entry_list) {
					Map<String, Name_value> record = new HashMap<String, Name_value>();
					for (Name_value name_value : entry_value
							.getName_value_list()) {
						record.put(name_value.getName(), name_value);
					}
					records.add(record);
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			offset += batch;
			System.out.println("getRevenueLineItems " + offset + " / " + count);
		}
		return records;
	}

	private static void cleanMeetings() {
		// System.out
		// .println(getIDs("Meetings", "meetings.date_entered<20140101"));
		deleteRecords("Meetings",
				getIDs("Meetings", "meetings.date_entered<20140101"));
	}

	private static void cleanCalls() {
		// System.out
		// .println(getIDs("Meetings", "meetings.date_entered<20140101"));
		deleteRecords("Calls", getIDs("Calls", "calls.date_entered<20140101"));
	}

	private static void cleanTasks() {
		// System.out
		// .println(getIDs("Meetings", "meetings.date_entered<20140101"));
		deleteRecords("Tasks", getIDs("Tasks", "tasks.date_entered<20140101"));
	}

	private static void cleanOpportunities() {
		// 6321
		deleteRecords("Opportunities",
				getIDs("Opportunities", "opportunities.date_modified>20140701"));
	}

	public static List<String> getIDs(String moduleName, String query) {
		List<String> ids = new ArrayList<String>();
		int batch = 200;
		int count = getRecordsCount(moduleName, query);
		int offset = 0;
		SugarSession instance = getInstance();
		instance.loginForSandusky();
		String[] select_fields = { "id" };
		while (offset < count) {
			try {
				Entry_value[] entry_list = instance
						.getSugar()
						.get_entry_list(instance.getSessionId(), moduleName,
								query, null, offset, select_fields, null,
								batch, 0, false).getEntry_list();
				for (Entry_value entry_value : entry_list) {
					ids.add(entry_value.getId());
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			offset += batch;
			System.out.println("getIds " + offset + " / " + count);
		}
		return ids;
	}

	public static void deleteRecords(String moduleName, List<String> recordIDs) {
		int batch = 200;
		int count = recordIDs.size();
		int offset = 0;
		ArrayList<String> recordIDsAL = new ArrayList<String>(recordIDs);
		SugarSession instance = getInstance();
		instance.loginForSandusky();
		while (offset < count) {
			List<String> batchList = recordIDsAL.subList(offset,
					Math.min(offset + batch, count));
			Name_value[][] name_value_lists = new Name_value[batchList.size()][2];
			for (int i = 0; i < batchList.size(); i++) {
				name_value_lists[i][0] = new Name_value("id", batchList.get(i));
				name_value_lists[i][1] = new Name_value("deleted", "1");

			}
			try {
				instance.getSugar().set_entries(instance.getSessionId(),
						moduleName, name_value_lists);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			offset += batch;
			System.out.println("delete records " + offset + " / " + count);
		}
	}

	public static int getRecordsCount(String moduleName, String query) {
		int count = -1;
		SugarSession instance = getInstance();
		instance.loginForSandusky();
		try {
			count = instance
					.getSugar()
					.get_entries_count(instance.getSessionId(), moduleName,
							query, 0).getResult_count();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}

	private static void test4() {
		/*
		 * try { SugarSession instance = getInstance(); SugarsoapPortType client
		 * = instance.getSugar(); Get_relationships_result get_relationships =
		 * client.get_relationships(instance.getSessionId(), "Accounts", null,
		 * "Campaigns", null, 0); // get_relationships. } catch (RemoteException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 */

	}

	private static void test3() {
		SugarSession instance = getInstance();
		instance.loginForSandusky();
		Name_value[] nvs = {
				new Name_value("id", "ac6c6430-88b4-a472-f9b0-54f89f4c5d76"),
				new Name_value("deleted", "1") };
		/*
		 * try { Set_entry_result set_entry = instance.getSugar().set_entry(
		 * instance.getSessionId(), "Accounts", nvs); if (set_entry.getError()
		 * != null) { System.out.println(set_entry.getError().getName());
		 * System.out.println(set_entry.getError().getNumber());
		 * System.out.println(set_entry.getError().getDescription()); } } catch
		 * (RemoteException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
	}

	private static void test2() {
		String query = null;// "(accounts.market_c='Ogden')";
		SugarSession instance = getInstance();
		instance.loginForSandusky();
		String[] select_fields = { "id", "name", "market_c" };
		/*
		 * try { Entry_value[] entry_list = instance .getSugar()
		 * .get_entry_list(instance.getSessionId(), "Accounts", query, null, 0,
		 * select_fields, 1, 2).getEntry_list(); for (Entry_value entry_value :
		 * entry_list) { Name_value[] name_value_list =
		 * entry_value.getName_value_list(); for (Name_value name_value :
		 * name_value_list) { System.out.println(name_value.getName() + " | " +
		 * name_value.getValue()); }
		 * 
		 * } } catch (RemoteException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
	}

	private void test() {
		loginForSandusky();
		String[] select_fields = { "id", "first_name", "oldid_c",
				"date_modified" };
		/*
		 * Get_entry_list_result get_entry_list; try { get_entry_list =
		 * getSugar().get_entry_list(getSessionId(), "Users", null, null, 0,
		 * select_fields, 150, 0); Entry_value[] entry_list =
		 * get_entry_list.getEntry_list(); for (Entry_value entry_value :
		 * entry_list) { Name_value[] name_value_list =
		 * entry_value.getName_value_list(); System.out.println("______"); for
		 * (Name_value name_value : name_value_list) {
		 * System.out.println(name_value.getValue()); }
		 * System.out.println("-----"); } } catch (RemoteException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

	}

	public Return_search_result searchByModule(String searchString,
			String[] modules, int offset, int maxResults) {
		try {
			return getSugar().search_by_module(getSessionId(), searchString,
					modules, offset, maxResults, null, null, false, false);
			/*
			 * return getSugar().search_by_module(username, password,
			 * searchString, modules, offset, maxResults);
			 */
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Return_search_result searchByModule(String searchString,
			String[] modules) {
		return searchByModule(searchString, modules, 0, Integer.MAX_VALUE);
	}

	public void printModules() {

		if (loginForSandusky()) {
			try {
				Module_list get_available_modules = getSugar()
						.get_available_modules(getSessionId(), null);
				Module_list_entry[] modules = get_available_modules
						.getModules();
				for (Module_list_entry string : modules) {
					System.out.println(string.getModule_key());
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public Field[] printModuleFields(String moduleName) {
		if (loginForSandusky()) {
			try {
				New_module_fields module_fields2 = getSugar()
						.get_module_fields(getSessionId(), moduleName, null);
				// get_module_fields = getSugar().get_module_fields(
				// getSessionId(), moduleName);
				// Field[] module_fields = get_module_fields.getModule_fields();
				Field[] module_fields = module_fields2.getModule_fields();
				for (Field field : module_fields) {
					System.out.println(field.getName() + " | "
							+ field.getLabel() + " | " + field.getType());
				}
				return module_fields;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public void printModuleRelateFields(String moduleName) {
		if (loginForSandusky()) {
			try {
				New_module_fields module_fields2 = getSugar()
						.get_module_fields(getSessionId(), moduleName, null);
				// get_module_fields = getSugar().get_module_fields(
				// getSessionId(), moduleName);
				// Field[] module_fields = get_module_fields.getModule_fields();
				Field[] module_fields = module_fields2.getModule_fields();
				for (Field field : module_fields) {
					if (field.getType().equalsIgnoreCase("relate")) {
						System.out.println(field.getName() + " | "
								+ field.getLabel() + " | " + field.getType());
					}
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Link_field[] printModuleRelationshipFields(String moduleName) {
		if (loginForSandusky()) {
			try {
				New_module_fields module_fields2 = getSugar()
						.get_module_fields(getSessionId(), moduleName, null);
				// get_module_fields = getSugar().get_module_fields(
				// getSessionId(), moduleName);
				// Field[] module_fields = get_module_fields.getModule_fields();
				Link_field[] module_fields = module_fields2.getLink_fields();
				for (Link_field field : module_fields) {
					System.out
							.println(field.getName() + " | " + field.getType()
									+ " | " + field.getRelationship());
				}
				return module_fields;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public void printModuleRecordCounts(final String query, String moduleName) {
		SugarSession instance = getInstance();
		instance.loginForSandusky();
		final SugarsoapPortType client = instance.getSugar();
		// try {
		// Module_list_entry[] modules = client.get_available_modules(
		// instance.getSessionId(), null).getModules();
		// for (final Module_list_entry module_list_entry : modules) {
		try {
			// new Thread(new Runnable() {
			//
			// public void run() {
			// int result_count;
			// try {
			// result_count = client.get_entries_count(
			// getSessionId(),
			// module_list_entry.getModule_key(),
			// query, 0).getResult_count();
			// System.out.println(module_list_entry
			// .getModule_key() + " : " + result_count);
			// } catch (RemoteException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// }
			// }).start();

			int result_count;
			try {
				result_count = client.get_entries_count(getSessionId(),
						moduleName, query, 0).getResult_count();
				System.out.println(moduleName + " : " + result_count);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		// }
		// } catch (RemoteException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public void printRecordCounts(final String query) {
		SugarSession instance = getInstance();
		instance.loginForSandusky();
		final SugarsoapPortType client = instance.getSugar();
		try {
			Module_list_entry[] modules = client.get_available_modules(
					instance.getSessionId(), null).getModules();
			for (final Module_list_entry module_list_entry : modules) {
				try {
					// new Thread(new Runnable() {
					//
					// public void run() {
					// int result_count;
					// try {
					// result_count = client.get_entries_count(
					// getSessionId(),
					// module_list_entry.getModule_key(),
					// query, 0).getResult_count();
					// System.out.println(module_list_entry
					// .getModule_key() + " : " + result_count);
					// } catch (RemoteException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					//
					// }
					// }).start();

					int result_count;
					try {
						result_count = client.get_entries_count(getSessionId(),
								module_list_entry.getModule_key(), query, 0)
								.getResult_count();
						System.out.println(module_list_entry.getModule_key()
								+ " : " + result_count);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long printETA(int numberOfRecords, int offset, long startTime) {
		String print = offset + "/" + numberOfRecords;
		long elapsedTime = System.currentTimeMillis() - startTime;
		long remaining = elapsedTime * (numberOfRecords / offset);
		long eta = System.currentTimeMillis() + remaining;
		Calendar etaCal = Calendar.getInstance();
		etaCal.setTimeInMillis(eta);
		print += (" ETA " + /* eta + " " + */etaCal.get(Calendar.HOUR) + ":"
				+ etaCal.get(Calendar.MINUTE) + ":" + etaCal
				.get(Calendar.SECOND));
		print += (" remaining: " + (remaining / 1000 / 60) + " min.");
		System.out.println(print);
		// logger.info(print);
		return remaining;
	}
}
