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
		 OgdenInput ogdenInput = new OgdenInput(moduleName);
		 ogdenInput.setBatchSize(200);
		 ogdenInput.setSelectFields(names(map.getFromToMap().keySet()));
		 // yyyy-MM-dd all>58023 date_entered>5148 date_modified>23475
		 String query = ("leads.date_modified>20150200");
		 ogdenInput.start(query);
		 System.out.println("Number of records " +
		 ogdenInput.getRecordsCount());
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

}
