package com.g2dev.job.custom.tandem.migration.workflow;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.g2dev.connect.DbField;
import com.g2dev.connect.DbObject;
import com.g2dev.connect.Schema;
import com.g2dev.job.custom.AccountDedupe;
import com.g2dev.job.custom.tandem.migration.TandemSugarSession;
import com.g2dev.job.custom.tandem.migration.rowListeners.OpportunityRowListener;
import com.g2dev.job.custom.tandem.migration.sugar.Entry_value;
import com.g2dev.job.custom.tandem.migration.sugar.Field;
import com.g2dev.job.custom.tandem.migration.sugar.Get_entry_list_result_version2;
import com.g2dev.job.custom.tandem.migration.sugar.Get_entry_result_version2;
import com.g2dev.job.custom.tandem.migration.sugar.Link_field;
import com.g2dev.job.custom.tandem.migration.sugar.Link_list2;
import com.g2dev.job.custom.tandem.migration.sugar.Link_name_to_fields_array;
import com.g2dev.job.custom.tandem.migration.sugar.Name_value;
import com.g2dev.job.custom.tandem.migration.sugar.New_module_fields;
import com.g2dev.map.IntegrationMap;
import com.g2dev.output.ext.SugarOutputProcessorCSV;
import com.g2dev.sugar.connect.SugarSession;

public class Test {

	public static void main(String[] args) throws Exception {
		testDuplicates();
	}

	private static void testDuplicates() {
		List<String> ids = new ArrayList<String>();
		ids.add("002ea7c3bfd44fbc1ac42b694862f7de");
		ids.add("252f03fb19280b50be17aea553e99c2b");
		ids.add("252f03fb19280b50be17aea553e99c2c");
		String query = "";
		for (String string : ids) {
			query += " accounts.id='" + string + "' or ";
		}
		query = query.substring(0, query.length()-3);
		System.out.println(query);
		String module_name = "Accounts";
		String[] select_fields = { "id" };
		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		try {
			com.sugarcrm.www.sugarcrm.Get_entry_list_result_version2 get_entry_list = session
					.getSugar().get_entry_list(session.getSessionId(),
							module_name, query, null, 0, select_fields, null,
							ids.size(), 0, false);
			com.sugarcrm.www.sugarcrm.Entry_value[] entry_list = get_entry_list
					.getEntry_list();
			for (com.sugarcrm.www.sugarcrm.Entry_value entry_value : entry_list) {
				System.out.println(entry_value.getId());
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void tst() throws Exception {

		// testOpportunities();
		long startTime = System.currentTimeMillis();
		System.out.println("Start " + startTime);
		testRelationships3();
		System.out.println("end " + System.currentTimeMillis());
		System.out.println("duration "
				+ (System.currentTimeMillis() - startTime));
		// TandemSugarSession.getInstance().printModuleRelationshipFields("Users");

	}

	private static void testRelationships3() throws RemoteException {
		// TandemSugarSession.getInstance().printModuleRelationshipFields("Users");
		TandemSugarSession session = TandemSugarSession.getInstance();
		session.loginForTandem();
		String module_name = "Contacts";
		String module_id = "";
		String related_module_query = "";
		int deleted = 0;
		int limit = 500;
		String order_by = "";
		Link_name_to_fields_array[] related_module_link_name_to_fields_array = null;
		Link_field[] printModuleRelationshipFields = TandemSugarSession
				.getInstance().printModuleRelationshipFields(module_name);
		for (Link_field link_field : printModuleRelationshipFields) {
			System.out.println(link_field.getBean_name());
			String link_field_name = link_field.getName();
			String[] related_fields = { "id" };
			int offset = 0;
			while (true) {
				Get_entry_result_version2 get_relationships = session
						.getSugar().get_relationships(session.getSessionId(),
								module_name, module_id, link_field_name,
								related_module_query, related_fields,
								related_module_link_name_to_fields_array,
								deleted, order_by, offset, limit);
				Entry_value[] entry_list = get_relationships.getEntry_list();
				for (Entry_value entry_value : entry_list) {
					Name_value[] name_value_list = entry_value
							.getName_value_list();
					for (Name_value name_value : name_value_list) {
						System.out.print(name_value.getName() + " / "
								+ name_value.getValue() + " | ");
					}
					System.out.println();
				}
				System.out.println(entry_list.length);
				if (entry_list.length <= 0) {
					break;

				}
				offset += limit;
			}
		}

	}

	private static void testRelationships2() throws Exception {
		TandemSugarSession session = TandemSugarSession.getInstance();
		session.loginForTandem();
		New_module_fields get_module_fields = session.getSugar()
				.get_module_fields(session.getSessionId(), "Users", null);
		Field[] module_fields = get_module_fields.getModule_fields();
		String[] select_fields = new String[module_fields.length];
		for (int i = 0; i < select_fields.length; i++) {
			select_fields[i] = module_fields[i].getName();
		}
		Get_entry_list_result_version2 get_entry_list = session.getSugar()
				.get_entry_list(session.getSessionId(), "Users", null, null, 0,
						select_fields, null, Integer.MAX_VALUE, 0, false);
		int result_count = get_entry_list.getResult_count();
	}

	private static void testRelationships() {
		TandemSugarSession session = TandemSugarSession.getInstance();
		session.loginForTandem();
		try {
			Get_entry_result_version2 get_relationships = session.getSugar()
					.get_relationships(session.getSessionId(), "Users",
							"12959503-874f-6278-ea44-544fa938bc67", null, null,
							null, null, 0, null, 0, Integer.MAX_VALUE);
			// Id_mod[] ids =
			// session.getSugar().get_relationships(session.getSessionId(),
			// "Users", "12959503-874f-6278-ea44-544fa938bc67", "Meetings",
			// null, 0).getIds();
			// for (Id_mod id_mod : ids) {
			// System.out.println(id_mod.getId());
			// }
			Link_list2[] relationship_list = get_relationships
					.getRelationship_list();
			for (Link_list2 link_list2 : relationship_list) {
				System.out.println(link_list2);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void testOpportunities() {
		IntegrationMap im = new IntegrationMap();
		Schema gen = new Schema();
		DbObject opp = new DbObject(gen);
		gen.getObjects().add(opp);
		opp.setName("Opportunities");
		DbField id = new DbField(opp);
		id.setIndex(0);
		id.setName("id");
		DbField date_closed = new DbField(opp);
		date_closed.setIndex(1);
		date_closed.setName("date_closed");
		DbField name = new DbField(opp);
		name.setIndex(2);
		name.setName("name");
		opp.getFields().add(id);
		im.getFromToMap().put(id, id);
		im.getFromToMap().put(date_closed, date_closed);
		im.getFromToMap().put(name, name);
		SugarOutputProcessorCSV output = new SugarOutputProcessorCSV(
				"Opportunities", im, new OpportunityRowListener(
						new AccountDedupe()));
		output.process(oppTestBatch());
	}

	private static List<String[]> oppTestBatch() {
		List<String[]> batch = new ArrayList<String[]>();
		String[] row = { "00-a00003-a000-000a-aaa0-00a0000aa00a", "2013-02-23",
				"TestOPP" };
		batch.add(row);
		return batch;
	}
}
