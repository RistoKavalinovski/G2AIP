package com.g2dev.job.custom.tandem.migration;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.g2dev.job.custom.tandem.migration.sugar.Entry_value;
import com.g2dev.job.custom.tandem.migration.sugar.Field;
import com.g2dev.job.custom.tandem.migration.sugar.Get_entries_count_result;
import com.g2dev.job.custom.tandem.migration.sugar.Get_entry_list_result_version2;
import com.g2dev.job.custom.tandem.migration.sugar.Link_field;
import com.g2dev.job.custom.tandem.migration.sugar.Module_list;
import com.g2dev.job.custom.tandem.migration.sugar.Module_list_entry;
import com.g2dev.job.custom.tandem.migration.sugar.Name_value;
import com.g2dev.job.custom.tandem.migration.sugar.New_module_fields;
import com.g2dev.job.custom.tandem.migration.sugar.New_set_entries_result;
import com.g2dev.job.custom.tandem.migration.sugar.Return_search_result;
import com.g2dev.job.custom.tandem.migration.sugar.SugarsoapLocator;
import com.g2dev.job.custom.tandem.migration.sugar.SugarsoapPortType;
import com.g2dev.job.custom.tandem.migration.sugar.User_auth;

public class TandemSugarSession {

	private User_auth userAuth;
	private SugarsoapPortType sugar;
	private String sessionId;
	final static Logger logger = Logger.getLogger(TandemSugarSession.class);

	private String username;
	private String password;
	private boolean forceLogin;

	public TandemSugarSession() {
		super();
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
		password = toMD5String(password);
		userAuth = new User_auth(userName, password);
		try {
			Entry_value login2 = sugar.login(userAuth, "sanduskynews", null);
			setSessionId(login2.getId());
			// Set_entry_resul login = sugar.login(userAuth, "sanduskynews");
			// Error_value error = login.getError();
			// if (error.getNumber().equals("0")) {
			// setSessionId(login.getId());
			// } else {
			// logger.error("Log IN error >> ");
			// logger.error("error name" + error.getName());
			// logger.error("error number" + error.getNumber());
			// logger.error("error description" + error.getDescription());
			// logger.error("Log IN error << ");
			// return false;
			// }
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

	public Return_search_result searchByModule(String searchString,
			String[] modules, int offset, int maxResults, String[] select_fields) {
		try {
			return getSugar().search_by_module(getSessionId(), searchString,
					modules, offset, maxResults, null, select_fields, false,
					false);
			// return getSugar().search_by_module(username, password,
			// searchString, modules, offset, maxResults);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Return_search_result searchByModule(String searchString,
			String[] modules, String[] selectFields) {
		return searchByModule(searchString, modules, 0, Integer.MAX_VALUE,
				selectFields);
	}

	private static TandemSugarSession instance;

	public static TandemSugarSession getInstance() {
		if (instance == null) {
			instance = new TandemSugarSession();
		}
		return instance;
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

	public boolean loginForTandem() {
		// TODO check this
		return login("vkoper", "koper", "6.5.16");
	}

	public static void main(String[] args) {
		// TandemSugarSession.getInstance().printModuleFields("Contacts");

		/*
		 * TandemSugarSession instance2 = TandemSugarSession.getInstance();
		 * instance2.loginForTandem(); String module_name = "Contacts"; String[]
		 * select_fields = { "id", "account_id" }; try {
		 * Get_entries_count_result get_entries_count = instance2.getSugar()
		 * .get_entries_count(instance2.getSessionId(), module_name, null, 1);
		 * System.out.println(get_entries_count.getResult_count());
		 * 
		 * // Get_entry_list_result get_entry_list = instance2.getSugar() //
		 * .get_entry_list(instance2.getSessionId(), module_name, // null, null,
		 * 0, select_fields, 200, 0); // Entry_value[] entry_list =
		 * get_entry_list.getEntry_list(); // for (Entry_value entry_value :
		 * entry_list) { // Name_value[] name_value_list =
		 * entry_value.getName_value_list(); // System.out.println(">>>"); //
		 * for (Name_value name_value : name_value_list) { //
		 * System.out.println(name_value.getValue()); // } //
		 * System.out.println("<<<"); // } } catch (RemoteException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

		// getInstance().undeleteRecords("Contacts", "");

		// getInstance().printRecordCounts(null);
		getInstance().printModuleFields("Leads");

	}

	public void deleteRecords(String moduleName, String query) {
		new DeleteEntriesJob(query, moduleName).start();
	}

	public void undeleteRecords(String moduleName, String query) {
		new UndeleteEntriesJob(query, moduleName).start();
	}

	public static class UndeleteEntriesJob {
		private String query;
		private String moduleName;
		private int batchSize = 1000;

		public UndeleteEntriesJob(String query, String moduleName) {
			super();
			this.query = query;
			this.moduleName = moduleName;
		}

		public void start() {
			List<String> selectFields = new ArrayList<String>();
			selectFields.add("id");
			TandemInput tandemInput = new TandemInput(getModuleName(),
					selectFields);
			tandemInput.setBatchSize(getBatchSize());
			tandemInput.setDeleted(1);
			tandemInput.start(getQuery());
			// Get_entry_list_result nextBatch = null;
			// while ((nextBatch = tandemInput.nextBatch(getQuery())) != null) {
			// undeleteBatch(nextBatch);
			// }
		}

		public void undeleteBatch(Get_entry_list_result_version2 nextBatch) {
			Entry_value[] entry_list = nextBatch.getEntry_list();
			Name_value[][] name_value_lists = new Name_value[entry_list.length][];
			for (int i = 0; i < entry_list.length; i++) {
				Entry_value entry_value = entry_list[i];
				Name_value[] targetNV = new Name_value[2];
				Name_value[] name_value_list = entry_value.getName_value_list();
				for (Name_value name_value : name_value_list) {
					if (name_value.getName().equalsIgnoreCase("id")) {
						targetNV[0] = name_value;
						break;
					}
				}
				targetNV[1] = new Name_value("deleted", "0");
				name_value_lists[i] = targetNV;
			}
			TandemSugarSession session = TandemSugarSession.getInstance();
			if (session.loginForTandem()) {
				try {
					New_set_entries_result set_entries = session.getSugar()
							.set_entries(session.getSessionId(),
									getModuleName(), name_value_lists);
					System.out.println(set_entries.getIds());
					// if (set_entries.getError() == null
					// || set_entries.getError().getNumber() == null
					// || set_entries.getError().getNumber().equals("0")) {
					// System.out.println("Sucess!! "
					// + set_entries.getIds().length
					// + " records are undeleted. ");
					//
					// } else {
					// System.out.println(set_entries.getError().getName());
					// System.out.println(set_entries.getError().getNumber());
					// System.out.println(set_entries.getError()
					// .getDescription());
					// }
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public String getQuery() {
			return query;
		}

		public void setQuery(String query) {
			this.query = query;
		}

		public int getBatchSize() {
			return batchSize;
		}

		public void setBatchSize(int batchSize) {
			this.batchSize = batchSize;
		}

		public String getModuleName() {
			return moduleName;
		}

		public void setModuleName(String moduleName) {
			this.moduleName = moduleName;
		}

	}

	public static class DeleteEntriesJob {
		private String query;
		private String moduleName;
		private int batchSize = 1000;

		public DeleteEntriesJob(String query, String moduleName) {
			super();
			this.query = query;
			this.moduleName = moduleName;
		}

		public void start() {
			List<String> selectFields = new ArrayList<String>();
			selectFields.add("id");
			TandemInput tandemInput = new TandemInput(getModuleName(),
					selectFields);
			tandemInput.setBatchSize(getBatchSize());
			tandemInput.start(getQuery());
			// Get_entry_list_result nextBatch = null;
			// while ((nextBatch = tandemInput.nextBatch(getQuery())) != null) {
			// deleteBatch(nextBatch);
			// }
		}

		public void deleteBatch(Get_entry_list_result_version2 nextBatch) {
			Entry_value[] entry_list = nextBatch.getEntry_list();
			Name_value[][] name_value_lists = new Name_value[entry_list.length][];
			for (int i = 0; i < entry_list.length; i++) {
				Entry_value entry_value = entry_list[i];
				Name_value[] targetNV = new Name_value[2];
				Name_value[] name_value_list = entry_value.getName_value_list();
				for (Name_value name_value : name_value_list) {
					if (name_value.getName().equalsIgnoreCase("id")) {
						targetNV[0] = name_value;
						break;
					}
				}
				targetNV[1] = new Name_value("deleted", "1");
				name_value_lists[i] = targetNV;
			}
			TandemSugarSession session = TandemSugarSession.getInstance();
			if (session.loginForTandem()) {
				try {
					New_set_entries_result set_entries = session.getSugar()
							.set_entries(session.getSessionId(),
									getModuleName(), name_value_lists);
					System.out.println(set_entries);
					// if (set_entries.getError() == null
					// || set_entries.getError().getNumber() == null
					// || set_entries.getError().getNumber().equals("0")) {
					// System.out.println("Sucess!! "
					// + set_entries.getIds().length
					// + " records are deleted. ");
					//
					// } else {
					// System.out.println(set_entries.getError().getName());
					// System.out.println(set_entries.getError().getNumber());
					// System.out.println(set_entries.getError()
					// .getDescription());
					// }
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public String getQuery() {
			return query;
		}

		public void setQuery(String query) {
			this.query = query;
		}

		public int getBatchSize() {
			return batchSize;
		}

		public void setBatchSize(int batchSize) {
			this.batchSize = batchSize;
		}

		public String getModuleName() {
			return moduleName;
		}

		public void setModuleName(String moduleName) {
			this.moduleName = moduleName;
		}

	}

	public void printModules() {

		if (loginForTandem()) {
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
		if (loginForTandem()) {
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
		if (loginForTandem()) {
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

	public Link_field[] getModuleRelationshipFields(String moduleName) {
		if (loginForTandem()) {
			try {
				New_module_fields module_fields2 = getSugar()
						.get_module_fields(getSessionId(), moduleName, null);
				Link_field[] module_fields = module_fields2.getLink_fields();
				return module_fields;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Link_field[] printModuleRelationshipFields(String moduleName) {
		if (loginForTandem()) {
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

	public void printRecordCounts(String query) {
		if (loginForTandem()) {
			try {
				// query = string.getModule_key().toLowerCase() +
				// ".date_modified>20130000"
				Map<String, Integer> moduleRecordsCount = new HashMap<String, Integer>();
				Module_list get_available_modules = getSugar()
						.get_available_modules(getSessionId(), null);
				Module_list_entry[] modules = get_available_modules
						.getModules();
				for (Module_list_entry string : modules) {
					Get_entries_count_result get_entries_count = getSugar()
							.get_entries_count(getSessionId(),
									string.getModule_key(), query, 0);
					moduleRecordsCount.put(string.getModule_key(),
							get_entries_count.getResult_count());

				}
				for (String module : moduleRecordsCount.keySet()) {
					System.out.println(module + ": "
							+ moduleRecordsCount.get(module));
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
