package com.g2dev.job.custom.sandusky.migration.workflow;

import java.rmi.RemoteException;

import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.Entry_value;

public class MigrateMainActionStarter {

	public static void main(String[] args) {
		// P_01_MigrateUsers.main(null);
		// String[] modules = { "Campaigns" };
		// P_99_MigrateDefaults.main(modules);

		// TODO P_05_MigrateLeads.main(null);
		//	P_02_MigrateAccounts.main(null);
		 P_04_MigrateOpportunities.main(null);;
		// P_03_MigrateContacts.main(null);

		// String[] modules1 = { "Emails", "Meetings", "Calls", "Notes",
		// "Documents", "Schedulers", "Tasks" };
		// P_99_MigrateDefaults.main(modules1);
		// test1();
	}

	private static void test1() {/*
								 * SugarSession session =
								 * SugarSession.getInstance();
								 * session.loginForSandusky(); String[]
								 * select_fields = { "id" }; try { Entry_value[]
								 * entry_list = session .getSugar()
								 * .get_entry_list(session.getSessionId(),
								 * "Leads", null, null, 0, select_fields, 8000,
								 * 1).getEntry_list(); for (Entry_value
								 * entry_value : entry_list) {
								 * System.out.println(entry_value.getId()); } }
								 * catch (RemoteException e) {
								 * e.printStackTrace(); }
								 */}

}
