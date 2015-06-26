package com.g2dev.sugar.connect;

import java.rmi.RemoteException;
import java.util.ArrayList;

import com.g2dev.connect.DbField;
import com.g2dev.job.custom.AccountDedupe;
import com.g2dev.job.custom.NewRowListener;
import com.g2dev.job.custom.SugarInput;
import com.g2dev.job.custom.ogden.migration.OgdenInput;
import com.g2dev.job.custom.ogden.migration.OgdenSugarSession;
import com.g2dev.job.custom.ogden.migration.rowListeners.AccountsRowListener;
import com.g2dev.job.custom.ogden.migration.sugar.Get_entry_list_result_version2;
import com.g2dev.job.custom.ogden.migration.sugar.SugarsoapPortType;
import com.g2dev.job.custom.ogden.migration.workflow.MigrationProcess;
import com.g2dev.map.IntegrationMap;
import com.g2dev.output.ext.SugarOutputProcessorCSV;
import com.sugarcrm.www.sugarcrm.Entry_value;
import com.sugarcrm.www.sugarcrm.Name_value;

public class TestEverythingPOC {

	public static void main(String[] args) {
		OgdenSugarSession.getInstance().printModuleRelationshipFields("Tasks");
	}

	private static void migrateRecord(String moduleName, final String id,
			String market) {
		migrateRecord(moduleName, id, market, null);
	}

	private static void migrateRecord(String moduleName, final String id,
			String market, final NewRowListener listener) {
		if (market != null && market.equalsIgnoreCase("Ogden")) {
			MigrationProcess mp = new MigrationProcess(moduleName) {

				@Override
				public void start() {

					IntegrationMap map = buildMap(moduleName);
					SugarOutputProcessorCSV output = new SugarOutputProcessorCSV(
							moduleName, map, listener != null ? listener
									: new AccountsRowListener(
											new AccountDedupe()));
					SugarSession.getInstance().loginForSandusky();
					String[] selectFields = names(map.getFromToMap().keySet())
							.toArray(
									new String[map.getFromToMap().keySet()
											.size()]);

					output.process(SugarInput
							.resultToBatch(
									getOgdenRecord(moduleName, id, selectFields),
									new ArrayList<DbField>(map.getFromToMap()
											.keySet())));

				}
			};
			mp.start();
		}

	}

	private static Entry_value getOgdenRecord(String moduleName, String id,
			String[] selectFields) {
		OgdenSugarSession session = OgdenSugarSession.getInstance();
		session.loginForOgden();
		SugarsoapPortType client = session.getSugar();
		try {
			com.g2dev.job.custom.ogden.migration.sugar.Entry_value[] entry_list = client
					.get_entry(session.getSessionId(), moduleName, id,
							selectFields, null, false).getEntry_list();
			if (entry_list != null && entry_list.length > 0) {
				com.g2dev.job.custom.ogden.migration.sugar.Entry_value entry_value = entry_list[0];
				Entry_value convertedEntry = new Entry_value(
						entry_value.getId(), entry_value.getModule_name(), null);
				com.g2dev.job.custom.ogden.migration.sugar.Name_value[] name_value_list = entry_value
						.getName_value_list();

				Name_value[] nameValueList = new Name_value[name_value_list.length];
				for (int i = 0; i < name_value_list.length; i++) {
					nameValueList[i] = new Name_value(
							name_value_list[i].getName(),
							name_value_list[i].getValue());
				}
				convertedEntry.setName_value_list(nameValueList);
				return convertedEntry;
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

}
