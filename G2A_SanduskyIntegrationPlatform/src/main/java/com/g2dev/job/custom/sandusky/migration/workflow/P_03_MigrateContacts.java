package com.g2dev.job.custom.sandusky.migration.workflow;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.g2dev.job.custom.AccountDedupe;
import com.g2dev.job.custom.sandusky.migration.SanduskyInput;
import com.g2dev.job.custom.sandusky.migration.rowListeners.AccountsRowListener;
import com.g2dev.job.custom.sandusky.migration.sugar.Entry_value;
import com.g2dev.job.custom.sandusky.migration.sugar.Field;
import com.g2dev.job.custom.sandusky.migration.sugar.Get_entry_list_result;
import com.g2dev.job.custom.sandusky.migration.sugar.Name_value;
import com.g2dev.map.IntegrationMap;
import com.g2dev.output.ext.SugarOutputProcessorCSV;
import com.g2dev.sugar.connect.SugarSession;

public class P_03_MigrateContacts extends MigrationProcess {

	public P_03_MigrateContacts() {
		super("Contacts");
		// TODO Auto-generated constructor stub
	}

	@Override
	public IntegrationMap buildMap(List<Field> source,
			List<com.sugarcrm.www.sugarcrm.Field> target) {
		return buildMap(source, target, moduleName);
	}

	public static void main(String[] args) {
		new P_03_MigrateContacts().start();
	}

	@Override
	public void start() {
		IntegrationMap map = buildMap(moduleName);
		SugarOutputProcessorCSV output = new SugarOutputProcessorCSV(
				moduleName, map, new AccountsRowListener(new AccountDedupe()));
		SugarSession.getInstance().loginForSandusky();
		SanduskyInput SanduskyInput = new SanduskyInput(moduleName);
		SanduskyInput.setBatchSize(400);
		SanduskyInput.setSelectFields(names(map.getFromToMap().keySet()));
		SanduskyInput.setDeleted(1);
		// yyyy-MM-dd all>58023 date_entered>5148 date_modified>23475
		String query = ("contacts.date_modified>20130000");
		SanduskyInput.start(query);
		System.out.println("Number of records " + SanduskyInput.getRecordsCount());
		Get_entry_list_result nextBatch = null;
		long startTimeMillis = System.currentTimeMillis();
		while ((nextBatch = SanduskyInput.nextBatch(query)) != null) {

			// SugarSession.getInstance().loginForSandusky();
			// output.process(SugarInput.resultToBatch(nextBatch,
			// new ArrayList<DbField>(map.getFromToMap().keySet())));
			System.out.println("offset " + SanduskyInput.getOffset());
			// // long timeSpend = (System.currentTimeMillis() -
			// startTimeMillis);
			// // float ap = (SanduskyInput.getRecordsCount() /
			// // SanduskyInput.getOffset());
			// // float remaining = timeSpend * ap;
			// float remaining = (System.currentTimeMillis() - startTimeMillis)
			// * SanduskyInput.getRecordsCount() / SanduskyInput.getOffset();
			// System.out.println("ETA: " + (remaining * 1000));
			//
			// // timeMillis = System.currentTimeMillis();

			writeInBeckupFile(nextBatch);
		}

	}

	private void writeInBeckupFile(Get_entry_list_result nextBatch) {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("C:\\SanduskyContacts\\contacts.csv",
							true), "UTF-8"));
			Entry_value[] entry_list = nextBatch.getEntry_list();
			boolean columnsPrinted = false;
			for (Entry_value entry_value : entry_list) {
				String line = "";
				Name_value[] name_value_list = entry_value.getName_value_list();
				String columns = "";
				for (Name_value name_value : name_value_list) {
					line += (name_value.getValue() + "\t");
					columns += (name_value.getName() + "\t");
					;
				}
				try {
					if (!columnsPrinted) {
						System.out.println("Columns:");
						System.out.println(columns);
						columnsPrinted = true;
					}
					out.write(line + "\n");
					System.out.println(line);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
