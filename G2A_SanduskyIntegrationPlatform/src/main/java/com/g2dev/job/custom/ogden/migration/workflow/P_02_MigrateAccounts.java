package com.g2dev.job.custom.ogden.migration.workflow;

import java.util.ArrayList;
import java.util.List;

import com.g2dev.connect.DbField;
import com.g2dev.job.custom.AccountDedupe;
import com.g2dev.job.custom.SugarInput;
import com.g2dev.job.custom.ogden.migration.OgdenInput;
import com.g2dev.job.custom.ogden.migration.rowListeners.AccountsRowListener;
import com.g2dev.job.custom.ogden.migration.sugar.Field;
import com.g2dev.job.custom.ogden.migration.sugar.Get_entry_list_result_version2;
import com.g2dev.map.IntegrationMap;
import com.g2dev.output.ext.SugarOutputProcessorCSV;
import com.g2dev.sugar.connect.SugarSession;

public class P_02_MigrateAccounts extends MigrationProcess {

	// private static final String moduleName = "Accounts";

	public P_02_MigrateAccounts() {
		super("Accounts");
		// TODO Auto-generated constructor stub
	}

	public void start() {
		IntegrationMap map = buildMap();
		SugarOutputProcessorCSV output = new SugarOutputProcessorCSV(
				moduleName, map, new AccountsRowListener(new AccountDedupe()));
		SugarSession.getInstance().loginForSandusky();
		OgdenInput ogdenInput = new OgdenInput(moduleName);
		ogdenInput.setBatchSize(100);
		ogdenInput.setSelectFields(names(map.getFromToMap().keySet()));
		// yyyy-MM-dd all>58023 date_entered>5148 date_modified>23475
		String query = "accounts.date_modified>20150200";//null;// encode("accounts.date_modified>20130000");
		ogdenInput.start(query);
		System.out.println("Number of records " + ogdenInput.getRecordsCount());
		Get_entry_list_result_version2 nextBatch = null;
		long startTimeMillis = System.currentTimeMillis();
		while ((nextBatch = ogdenInput.nextBatch(query)) != null) {

			SugarSession.getInstance().loginForSandusky();
			output.process(SugarInput.resultToBatch(nextBatch,
					new ArrayList<DbField>(map.getFromToMap().keySet())));
			System.out.println("offset " + ogdenInput.getOffset());
			// long timeSpend = (System.currentTimeMillis() - startTimeMillis);
			// float ap = (ogdenInput.getRecordsCount() /
			// ogdenInput.getOffset());
			// float remaining = timeSpend * ap;
			float remaining = (System.currentTimeMillis() - startTimeMillis)
					* ogdenInput.getRecordsCount() / ogdenInput.getOffset();
			System.out.println("ETA: " + (remaining));

			// timeMillis = System.currentTimeMillis();
		}

	}

	private static String encode(String string) {
		return string;
		// return new String(Charset.forName("UTF-8").encode(string).array());
	}

	public static void main(String[] args) {
		new P_02_MigrateAccounts().start();

		// OgdenSugarSession session = OgdenSugarSession.getInstance();
		// session.printModuleRelateFields("Accounts");
		// test();
		// test2();
	}

	// private static void test2() {
	// SugarSession session = SugarSession.getInstance();
	// if (session.loginForSandusky()) {
	// String[] select_fields = { "id", "assigned_user_id" };
	// Get_entry_result get_entry;
	// try {
	// get_entry = session.getSugar().get_entry(
	// session.getSessionId(), "Accounts",
	// "de9fa21f-82ba-f4ca-a8db-54f79dc1f69f", select_fields);
	// Entry_value[] entry_list = get_entry.getEntry_list();
	// for (Entry_value entry_value : entry_list) {
	// Name_value[] name_value_list = entry_value
	// .getName_value_list();
	// for (Name_value name_value : name_value_list) {
	// System.out.println(name_value.getValue());
	// }
	// }
	// } catch (RemoteException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	// }
	//
	// private static void test() {
	// SugarSession session = SugarSession.getInstance();
	// if (session.loginForSandusky()) {
	// Name_value[] name_value_list = {
	// new Name_value("assigned_user_id",
	// "bc4bc68f-6a2c-b3d9-a618-53ac2d14503e"),
	// new Name_value("name", "G2 TEST 123") };
	// try {
	// Set_entry_result set_entry = session.getSugar().set_entry(
	// session.getSessionId(), "Accounts", name_value_list);
	// System.out.println(set_entry.getId());
	// System.out.println(set_entry.getError().getName());
	// System.out.println(set_entry.getError().getNumber());
	// System.out.println(set_entry.getError().getDescription());
	// } catch (RemoteException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// }

	private IntegrationMap buildMap() {
		return buildMap(getModuleName());
	}

	@Override
	public IntegrationMap buildMap(List<Field> source,
			List<com.sugarcrm.www.sugarcrm.Field> target) {
		return buildMap(source, target, moduleName);
	}
	
}
