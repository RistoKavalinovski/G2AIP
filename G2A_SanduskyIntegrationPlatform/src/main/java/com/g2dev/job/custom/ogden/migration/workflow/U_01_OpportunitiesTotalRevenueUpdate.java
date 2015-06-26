package com.g2dev.job.custom.ogden.migration.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.g2dev.connect.DbField;
import com.g2dev.connect.DbObject;
import com.g2dev.connect.Schema;
import com.g2dev.job.custom.NewRowListener;
import com.g2dev.job.custom.SugarInput;
import com.g2dev.job.custom.ogden.migration.OgdenInput;
import com.g2dev.job.custom.ogden.migration.OgdenSugarSession;
import com.g2dev.job.custom.ogden.migration.sugar.Get_entry_list_result_version2;
import com.g2dev.map.IntegrationMap;
import com.g2dev.output.ext.SugarOutputProcessorCSV;
import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.Name_value;

public class U_01_OpportunitiesTotalRevenueUpdate extends MigrationProcess {

	public U_01_OpportunitiesTotalRevenueUpdate() {
		super("Opportunities");
	}

	@Override
	public void start() {
		IntegrationMap map = buildMap(moduleName);
		SugarOutputProcessorCSV output = new SugarOutputProcessorCSV(
				moduleName, map, new NewRowListener() {

					public List<Name_value> newRowEvent(
							List<Name_value> currentRow) {
						return null;
					}

					public void beforeProcessBatch(List<String[]> sourceBatch) {

					}

					public void afterInsertBatch(
							List<List<Name_value>> targetBatch) {

					}

					public List<List<Name_value>> trimRows(
							List<List<Name_value>> targetBatch) {
						// TODO Auto-generated method stub
						return null;
					}
				});
		output.setRemoveDuplicates(false);
		SugarSession.getInstance().loginForSandusky();
		OgdenInput ogdenInput = new OgdenInput(moduleName);
		ogdenInput.setBatchSize(500);
		ogdenInput.setSelectFields(names(map.getFromToMap().keySet()));
		// yyyy-MM-dd all>58023 date_entered>5148 date_modified>23475
		String query = ("opportunities.date_modified>20130000");
		ogdenInput.start(query);
		System.out.println("Number of records " + ogdenInput.getRecordsCount());
		Get_entry_list_result_version2 nextBatch = null;
		long startTimeMillis = System.currentTimeMillis();
		while ((nextBatch = ogdenInput.nextBatch(query)) != null) {

			SugarSession.getInstance().loginForSandusky();
			output.process(SugarInput.resultToBatch(nextBatch,
					new ArrayList<DbField>(map.getFromToMap().keySet())));
			System.out.println("offset " + ogdenInput.getOffset());
			float remaining = (System.currentTimeMillis() - startTimeMillis)
					* ogdenInput.getRecordsCount() / ogdenInput.getOffset();
			System.out.println("ETA: " + (remaining));

		}

	}

	public static void main(String[] args) {
		new U_01_OpportunitiesTotalRevenueUpdate().start();
	}

	@Override
	public IntegrationMap buildMap(String module_name) {
		IntegrationMap im = new IntegrationMap();
		Map<DbField, DbField> fromToMap = im.getFromToMap();
		DbField fromId = new DbField(new DbObject(new Schema()));
		fromId.setIndex(0);
		fromId.setName("id");
		DbField toId = new DbField(new DbObject(new Schema()));
		toId.setIndex(0);
		toId.setName("id");
		fromToMap.put(fromId, toId);

		DbField fromAmount = new DbField(new DbObject(new Schema()));
		fromAmount.setIndex(1);
		fromAmount.setName("amount");
		DbField toConverted_the_total_amount_c = new DbField(new DbObject(
				new Schema()));
		toConverted_the_total_amount_c.setIndex(1);
		toConverted_the_total_amount_c.setName("print_amount_c");
		fromToMap.put(fromAmount, toConverted_the_total_amount_c);

		return im;
	}

}
