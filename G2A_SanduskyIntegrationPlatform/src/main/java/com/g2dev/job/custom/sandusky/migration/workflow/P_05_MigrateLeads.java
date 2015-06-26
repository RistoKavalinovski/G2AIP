package com.g2dev.job.custom.sandusky.migration.workflow;

import java.util.ArrayList;
import java.util.List;

import com.g2dev.connect.DbField;
import com.g2dev.job.custom.AccountDedupe;
import com.g2dev.job.custom.SugarInput;
import com.g2dev.job.custom.sandusky.migration.SanduskyInput;
import com.g2dev.job.custom.sandusky.migration.rowListeners.AccountsRowListener;
import com.g2dev.job.custom.sandusky.migration.sugar.Field;
import com.g2dev.job.custom.sandusky.migration.sugar.Get_entry_list_result;
import com.g2dev.map.IntegrationMap;
import com.g2dev.output.ext.SugarOutputProcessorCSV;
import com.g2dev.sugar.connect.SugarSession;

public class P_05_MigrateLeads extends MigrationProcess {

	public P_05_MigrateLeads() {
		super("Leads");
		// TODO Auto-generated constructor stub
	}

	@Override
	public IntegrationMap buildMap(List<Field> source,
			List<com.sugarcrm.www.sugarcrm.Field> target) {
		return buildMap(source, target, moduleName);
	}

	public static void main(String[] args) {
		new P_05_MigrateLeads().start();
	}

	@Override
	public void start() {
		IntegrationMap map = buildMap(moduleName);
		 SugarOutputProcessorCSV output = new SugarOutputProcessorCSV(
		 moduleName, map, new AccountsRowListener(new AccountDedupe()));
		 SugarSession.getInstance().loginForSandusky();
		 SanduskyInput SanduskyInput = new SanduskyInput(moduleName);
		 SanduskyInput.setBatchSize(200);
		 SanduskyInput.setSelectFields(names(map.getFromToMap().keySet()));
		 // yyyy-MM-dd all>58023 date_entered>5148 date_modified>23475
		 String query = ("leads.date_modified>20130000");
		 SanduskyInput.start(query);
		 System.out.println("Number of records " +
		 SanduskyInput.getRecordsCount());
		 Get_entry_list_result nextBatch = null;
		 long startTimeMillis = System.currentTimeMillis();
		 while ((nextBatch = SanduskyInput.nextBatch(query)) != null) {
		
		 SugarSession.getInstance().loginForSandusky();
		 output.process(SugarInput.resultToBatch(nextBatch,
		 new ArrayList<DbField>(map.getFromToMap().keySet())));
		 System.out.println("offset " + SanduskyInput.getOffset());
		 // long timeSpend = (System.currentTimeMillis() - startTimeMillis);
		 // float ap = (SanduskyInput.getRecordsCount() /
		 // SanduskyInput.getOffset());
		 // float remaining = timeSpend * ap;
		 float remaining = (System.currentTimeMillis() - startTimeMillis)
		 * SanduskyInput.getRecordsCount() / SanduskyInput.getOffset();
		 System.out.println("ETA: " + (remaining));
		
		 // timeMillis = System.currentTimeMillis();
		 }

	}

}
