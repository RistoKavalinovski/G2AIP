package com.g2dev.job.custom.ogden.migration.workflow;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.g2dev.input.ext.InputCsv;
import com.g2dev.integrator.StaticProperties;
import com.g2dev.output.Output;
import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.Entry_value;
import com.sugarcrm.www.sugarcrm.Get_entry_list_result_version2;
import com.sugarcrm.www.sugarcrm.Get_entry_result_version2;
import com.sugarcrm.www.sugarcrm.Link_list2;
import com.sugarcrm.www.sugarcrm.Link_name_to_fields_array;
import com.sugarcrm.www.sugarcrm.Name_value;
import com.sugarcrm.www.sugarcrm.SugarsoapPortType;
import com.sugarcrm.www.sugarcrm.rest.SugarRestSession;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarClient;

public class C_01_CleanAccounts {

	private static final String MODULE_NAME = "Accounts";

	public static void main(String[] args) {
		new C_01_CleanAccounts().start();
		// SugarSession.getInstance().printModuleFields("Opportunities");
	}

	public void start() {
		InputCsv in = new InputCsv();
		in.setSeparator(',');
		final Set<String> accountNumbers = new HashSet<String>();
		in.parse(new File(StaticProperties.MASTER_FILE_PATH), new Output() {

			@Override
			public void process(List<String[]> batch) {
				for (String[] fields : batch) {
					accountNumbers.add(fields[0]);
				}
			}
		});
		Set<String> opportunitiesAccountsNames = getOpportunitiesAccountsNames();// getAccountIds(accountNumbers);
		System.out.println("ACCOUNT NUMBERS: " + accountNumbers.size());
		System.out.println("ACCOUNT NAMES: "
				+ opportunitiesAccountsNames.size());
		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		SugarsoapPortType client = session.getSugar();
		try {
			int result_count = client.get_entries_count(session.getSessionId(),
					MODULE_NAME, null, 0).getResult_count();
			String[] select_fields = { "id", "name", "acctno_c" };
			int offset = 0;
			int batchSize = 500;
			while (offset == 0 || (offset) < result_count) {
				Entry_value[] entry_list = client.get_entry_list(
						session.getSessionId(), MODULE_NAME, null, null,
						offset, select_fields, null, batchSize, 0, false)
						.getEntry_list();
				List<String> deleteAcccountIDS = new ArrayList<String>();
				for (Entry_value entry_value : entry_list) {
					Name_value[] name_value_list = entry_value
							.getName_value_list();
					boolean deleteEntry = true;
					for (Name_value name_value : name_value_list) {
						if (name_value.getName().equalsIgnoreCase("name")
								&& name_value.getValue() != null
								&& !name_value.getValue().isEmpty()
								&& opportunitiesAccountsNames
										.contains(name_value.getValue())) {
							deleteEntry = false;
							break;
						} else if (name_value.getName().equalsIgnoreCase(
								"acctno_c")
								&& name_value.getValue() != null
								&& !name_value.getValue().isEmpty()
								&& accountNumbers.contains(name_value
										.getValue())) {
							deleteEntry = false;
							break;
						}
					}
					if (deleteEntry) {
						deleteAcccountIDS.add(entry_value.getId());
					}
				}
				deleteAccounts(deleteAcccountIDS);
				offset += batchSize;

			}
			client.get_entry_list(session.getSessionId(), MODULE_NAME, null,
					null, offset, select_fields, null, batchSize, 0, false);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void deleteAccounts(List<String> deleteAcccountIDS) {
		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		SugarsoapPortType client = session.getSugar();
		Name_value[][] name_value_lists = new Name_value[deleteAcccountIDS
				.size()][2];
		for (int i = 0; i < deleteAcccountIDS.size(); i++) {
			String accID = deleteAcccountIDS.get(i);
			Name_value nvID = new Name_value("id", accID);
			Name_value nvDeleted = new Name_value("deleted", "1");
			name_value_lists[i][0] = nvID;
			name_value_lists[i][1] = nvDeleted;
		}

		try {
			client.set_entries(session.getSessionId(), MODULE_NAME,
					name_value_lists);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static Set<String> getAccountIds(Set<String> accountNumbers) {
		Set<String> accountIds = new HashSet<String>();
		int batchSize = 200;
		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		SugarsoapPortType client = session.getSugar();

		String query = buildAccountNumberQuery(accountNumbers);
		String order_by = null;
		int offset = 0;
		String[] select_fields = { "id" };
		Link_name_to_fields_array[] link_name_to_fields_array = null;// { new
		// Link_name_to_fields_array(
		// "acctno_c", accountNumbers.toArray(new String[accountNumbers
		// .size()])) };
		int result_count = 7000;
		// try {
		// //result_count = client.get_entries_count(session.getSessionId(),
		// module_name, query, deleted)
		// } catch (SugarApiException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		while (offset == 0 || (offset + batchSize) <= result_count) {
			try {

				Get_entry_list_result_version2 get_entry_list = client
						.get_entry_list(session.getSessionId(), MODULE_NAME,
								query, order_by, offset, select_fields,
								link_name_to_fields_array, batchSize, 0, false);
				Entry_value[] entry_list = get_entry_list.getEntry_list();
				for (Entry_value entry_value : entry_list) {
					accountIds.add(entry_value.getId());
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			offset += batchSize;
			System.out
					.println("batch loaded: " + offset + " / " + result_count);
		}
		return accountIds;
	}

	private static String buildAccountNumberQuery(Set<String> accountNumbers) {
		// return
		// "accounts.id IN (SELECT accounts_cstm.bean_id FROM accounts_cstm JOIN acctns ON accounts_cstm.acctno_c = acctno_c.id WHERE acctns.acctno_c = '14589')";
		StringBuffer sb = new StringBuffer();
		for (String string : accountNumbers) {
			// acctno_c
			sb.append(MODULE_NAME.toLowerCase() + "_cstm.acctno_c=" + string
					+ " or ");
		}
		return sb.toString().substring(0, sb.toString().length() - 5);
	}

	public static Set<String> getOpportunitiesAccountsIDS1() {
		Set<String> oppsAccountIds = new HashSet<String>();
		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		SugarsoapPortType client = session.getSugar();
		int result_count = 0;
		try {
			result_count = client.get_entries_count(session.getSessionId(),
					"Opportunities", null, 0).getResult_count();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// opportunities_accounts_1
		return oppsAccountIds;
	}

	public static Set<String> getOpportunitiesAccountsNames() {
		Set<String> oppsAccountNames = new HashSet<String>();
		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		SugarsoapPortType client = session.getSugar();
		int result_count = 0;
		try {
			result_count = client.get_entries_count(session.getSessionId(),
					"Opportunities", null, 0).getResult_count();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int batchSize = 2000;
		int offset = result_count - batchSize;
		String[] select_fields = { "id", "account_name" };
		// Link_name_to_fields_array[] link_name_to_fields_array = {new
		// Link_name_to_fields_array(name, value)};
		Link_name_to_fields_array[] link_name_to_fields_arrays = null;
		String[] related_fields = { "id" };

		while (offset > 0) {
			try {

				Get_entry_list_result_version2 get_entry_list = client
						.get_entry_list(session.getSessionId(),
								"Opportunities", null, "account_name", offset,
								select_fields, null, batchSize, 0, false);
				Entry_value[] entry_list = get_entry_list.getEntry_list();
				for (Entry_value entry_value : entry_list) {
					// Get_entry_result_version2 get_relationships = client
					// .get_relationships(session.getSessionId(),
					// "Opportunities", entry_value.getId(),
					// "accounts_opportunities", null,
					// related_fields, link_name_to_fields_arrays,
					// 0, null, 0, Integer.MAX_VALUE);
					// Entry_value[] entry_list2 = get_relationships
					// .getEntry_list();
					// for (Entry_value entry_value2 : entry_list2) {
					// oppsAccountIds.add(entry_value2.getId());
					// }
					Name_value[] name_value_list = entry_value
							.getName_value_list();
					for (Name_value name_value : name_value_list) {
						if (name_value.getName().equalsIgnoreCase(
								"account_name")
								&& name_value.getValue() != null
								&& !name_value.getValue().isEmpty()) {
							oppsAccountNames.add(name_value.getValue());
						}
					}
					// accountIds.add(entry_value.getId());
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			offset -= batchSize;
			System.out
					.println("batch loaded: " + offset + " / " + result_count);
		}
		return oppsAccountNames;
	}

}
