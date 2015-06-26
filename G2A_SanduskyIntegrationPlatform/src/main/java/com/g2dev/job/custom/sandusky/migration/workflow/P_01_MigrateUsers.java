package com.g2dev.job.custom.sandusky.migration.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.g2dev.connect.DbField;
import com.g2dev.job.custom.AccountDedupe;
import com.g2dev.job.custom.SugarInput;
import com.g2dev.job.custom.sandusky.migration.SanduskyInput;
import com.g2dev.job.custom.sandusky.migration.rowListeners.UsersRowListener;
import com.g2dev.job.custom.sandusky.migration.sugar.Field;
import com.g2dev.job.custom.sandusky.migration.sugar.Get_entry_list_result;
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
		// SanduskySugarSession SanduskySugarSession =
		// SanduskySugarSession.getInstance();
		new P_01_MigrateUsers().start();

	}

	public void start() {
		IntegrationMap map = buildMap();
		SugarOutputProcessorCSV output = new SugarOutputProcessorCSV(
				moduleName, map, new UsersRowListener(new AccountDedupe()));
		SugarSession.getInstance().loginForSandusky();
		SanduskyInput SanduskyInput = new SanduskyInput(moduleName);
		SanduskyInput.setSelectFields(names(map.getFromToMap().keySet()));
		String query = null;
		SanduskyInput.start(query);
		Get_entry_list_result nextBatch = null;
		while ((nextBatch = SanduskyInput.nextBatch(query)) != null) {

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
