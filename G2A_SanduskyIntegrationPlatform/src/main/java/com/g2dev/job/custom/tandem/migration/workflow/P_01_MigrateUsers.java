package com.g2dev.job.custom.tandem.migration.workflow;

import java.util.ArrayList;
import java.util.List;

import com.g2dev.connect.DbField;
import com.g2dev.job.custom.AccountDedupe;
import com.g2dev.job.custom.SugarInput;
import com.g2dev.job.custom.tandem.migration.TandemInput;
import com.g2dev.job.custom.tandem.migration.rowListeners.UsersRowListener;
import com.g2dev.job.custom.tandem.migration.sugar.Field;
import com.g2dev.job.custom.tandem.migration.sugar.Get_entry_list_result_version2;
import com.g2dev.map.IntegrationMap;
import com.g2dev.output.ext.SugarOutputProcessorCSV;
import com.g2dev.sugar.connect.SugarSession;

public class P_01_MigrateUsers extends MigrationProcess {

	public P_01_MigrateUsers() {
		super("Users");
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// SugarSession sugarSession = SugarSession.getInstance();
		// TandemSugarSession tandemSugarSession =
		// TandemSugarSession.getInstance();
		new P_01_MigrateUsers().start();

	}

	public void start() {
		IntegrationMap map = buildMap();
		SugarOutputProcessorCSV output = new SugarOutputProcessorCSV(
				moduleName, map, new UsersRowListener(new AccountDedupe()));
		SugarSession.getInstance().loginForSandusky();
		TandemInput tandemInput = new TandemInput(moduleName);
		tandemInput.setSelectFields(names(map.getFromToMap().keySet()));
		String query = null;
		tandemInput.start(query);
		Get_entry_list_result_version2 nextBatch = null;
		while ((nextBatch = tandemInput.nextBatch(query)) != null) {

			output.process(SugarInput.resultToBatch(nextBatch,
					new ArrayList<DbField>(map.getFromToMap().keySet())));
		}
	}

	private IntegrationMap buildMap() {
		return buildMap("Users");
	}

	public IntegrationMap buildMap(List<Field> source,
			List<com.sugarcrm.www.sugarcrm.Field> target) {
		return buildMap(source, target, moduleName);
	}
}
