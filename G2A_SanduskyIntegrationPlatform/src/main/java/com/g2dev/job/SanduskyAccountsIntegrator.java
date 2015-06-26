package com.g2dev.job;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.g2dev.dropbox.DropboxSanduskyUtil;
import com.g2dev.input.ext.InputCsv;
import com.g2dev.integrator.StaticProperties;
import com.g2dev.integrator.schedule.Scheduler;
import com.g2dev.job.custom.AccountDedupe;
import com.g2dev.job.custom.triCircles.TriCirclesRowListener;
import com.g2dev.map.IntegrationMap;
import com.g2dev.output.ext.SugarOutputProcessorCSV;
import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.Entry_value;
import com.sugarcrm.www.sugarcrm.Get_entry_list_result_version2;
import com.sugarcrm.www.sugarcrm.Name_value;
import com.sugarcrm.www.sugarcrm.Return_search_result;
import com.sugarcrm.www.sugarcrm.Search_link_name_value;

public class SanduskyAccountsIntegrator implements Runnable {

	public static Map<String, Integer> sandusky_TriCities_AccountsMap = new HashMap<String, Integer>();
	static {
		// sandusky_TriCities_AccountsMap.put(key, value);
	}

	public static void main(String[] args) {
		new Scheduler(new SanduskyAccountsIntegrator(), 600000).start();
		// ;

		// test2();
		// test3();

	}

	private static void test3() {
		new AccountDedupe().getAccounts();
	}

	private static void test1() {
		SugarSession.getInstance().loginForSandusky();
		float avgDuration = 0;
		for (int i = 0; i < 50; i++) {
			long duration = System.currentTimeMillis();
			getDuplicateIdsAccounts(getRandomString(), null);
			duration = System.currentTimeMillis() - duration;
			avgDuration += duration;
			System.out.println(duration);
		}
		avgDuration = avgDuration / 50;
		System.out.println(avgDuration);

	}

	private static Random random = new Random();

	private static String getRandomString() {
		StringBuffer rnd = new StringBuffer();
		for (int i = 0; i < 20; i++) {
			rnd.append(new Character((char) random.nextInt(120)));
		}
		return stripNonValidXMLCharacters(rnd.toString());
	}

	public static String stripNonValidXMLCharacters(String in) {
		StringBuffer out = new StringBuffer(); // Used to hold the output.
		char current; // Used to reference the current character.

		if (in == null || ("".equals(in)))
			return ""; // vacancy test.
		for (int i = 0; i < in.length(); i++) {
			current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught
									// here; it should not happen.
			if ((current == 0x9) || (current == 0xA) || (current == 0xD)
					|| ((current >= 0x20) && (current <= 0xD7FF))
					|| ((current >= 0xE000) && (current <= 0xFFFD))
					|| ((current >= 0x10000) && (current <= 0x10FFFF)))
				out.append(current);
		}
		return out.toString();
	}

	private static void test2() {

		SugarSession session = SugarSession.getInstance();
		if (session.loginForSandusky()) {
			String[] modules = { "Accounts" };
			String[] selectFields = { "id", "name", "acctno_c" };
			try {
				// searchByModule = session.getSugar().get_entry_list(
				// session.getSessionId(), "Accounts",
				// "Accounts.name='NEWPORT DRY GOODS COMPANY'", "", 0,
				// selectFields, Integer.MAX_VALUE, 0);
				Get_entry_list_result_version2 searchByModule = null;/*
																	 * session.
																	 * getSugar
																	 * () .
																	 * get_entry_list
																	 * (session.
																	 * getSessionId
																	 * (),
																	 * "Accounts"
																	 * ,
																	 * "Accounts.name='NEWPORT DRY GOODS COMPANY'"
																	 * , "", 0,
																	 * selectFields
																	 * ,
																	 * Integer.
																	 * MAX_VALUE
																	 * , 0);
																	 */

				if (searchByModule != null) {

					Entry_value[] entry_list = searchByModule.getEntry_list();
					if (entry_list != null) {
						for (Entry_value entry_value : entry_list) {
							Name_value[] name_value_list = entry_value
									.getName_value_list();
							System.out.println();
							for (Name_value name_value : name_value_list) {
								System.out.print(name_value.getName() + " > "
										+ name_value.getValue() + " | ");
							}
							System.out.println();
						}
					}

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static void test() {
		SugarSession session = SugarSession.getInstance();
		if (session.loginForSandusky()) {
			String[] modules = { "Accounts" };
			String[] selectFields = { "id", "name", "acctno_c" };
			Get_entry_list_result_version2 searchByModule;
			try {
				/*
				 * searchByModule = session.getSugar().get_entry_list(
				 * session.getSessionId(), "Accounts",
				 * "Accounts.name='NEWPORT DRY GOODS COMPANY'", "", 0,
				 * selectFields, Integer.MAX_VALUE, 0);
				 */

				/*
				 * if (searchByModule != null) { if (searchByModule.getError()
				 * == null || searchByModule.getError().getNumber() == null ||
				 * searchByModule.getError().getNumber() .equals("0")) {
				 */
				Entry_value[] entry_list = null/*
												 * searchByModule.getEntry_list()
												 */;
				if (entry_list != null) {
					for (Entry_value entry_value : entry_list) {
						Name_value[] name_value_list = entry_value
								.getName_value_list();
						System.out.println();
						for (Name_value name_value : name_value_list) {
							System.out.print(name_value.getName() + " > "
									+ name_value.getValue() + " | ");
						}
						System.out.println();
					}
				}
				/*
				 * } }
				 */
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static List<String> getDuplicateIdsAccounts(String accountName,
			String accountCode) {

		SugarSession session = SugarSession.getInstance();
		String[] modules = { "Accounts" };
		/*
		 * Get_entry_list_result_version2 searchByModule =
		 * session.searchByModule( accountName, modules);
		 */
		Return_search_result searchByModule = session.searchByModule(
				accountName, modules);
		if (searchByModule != null) {
			/*
			 * if (searchByModule.getError() == null ||
			 * searchByModule.getError().getNumber() == null ||
			 * searchByModule.getError().getNumber().equals("0")) {
			 */
			Search_link_name_value[] entry_list = searchByModule
					.getEntry_list();
			/* Entry_value[] entry_list = searchByModule.getEntry_list(); */
			if (entry_list != null) {
				List<String> duplicateAccountIDs = new ArrayList<String>();
				for (Search_link_name_value entry_value : entry_list) {
					// TODO
					// TODO
					// TODO FIX THE FOLLOWING!!!
					// TODO
					// TODO
					/*
					 * Name_value[] name_value_list = entry_value .getRecords();
					 * for (Name_value name_value : name_value_list) { if
					 * (name_value.getName().equals("id")) {
					 * duplicateAccountIDs.add(name_value.getValue()); } }
					 */
				}
				return duplicateAccountIDs;
			}
			/* } */
		}

		return null;
	}

	public void run() {

		InputCsv inputCsv = new InputCsv();
		inputCsv.setBatchSize(100);
		IntegrationMap map = new IntegrationMap();
		map.loadFromAccountsFile(new File(
				StaticProperties.ACCOUNTS_MAP_FILE_PATH));
		SugarOutputProcessorCSV output = new SugarOutputProcessorCSV(
				"Accounts", map, new TriCirclesRowListener(new AccountDedupe()));
		SugarSession.getInstance().loginForSandusky();
		DropboxSanduskyUtil.checkAndStartIntegration(inputCsv, output);

		// TODO run this again and remove parent_relate field from listview in
		// sugar

		// test2();

	}

}
