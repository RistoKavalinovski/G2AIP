package com.g2dev.job.custom.tandem.migration.workflow;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

import com.g2dev.connect.DbField;
import com.g2dev.job.custom.AccountDedupe;
import com.g2dev.job.custom.SugarInput;
import com.g2dev.job.custom.tandem.migration.workflow.P_04_MigrateOpportunities;
import com.g2dev.job.custom.tandem.migration.TandemSugarSession;
import com.g2dev.job.custom.tandem.migration.sugar.Entry_value;
import com.g2dev.job.custom.tandem.migration.sugar.SugarsoapPortType;
import com.g2dev.job.custom.tandem.migration.TandemInput;
import com.g2dev.job.custom.tandem.migration.rowListeners.OpportunityRowListener;
import com.g2dev.job.custom.tandem.migration.sugar.Field;
import com.g2dev.job.custom.tandem.migration.sugar.Get_entry_list_result_version2;
import com.g2dev.map.IntegrationMap;
import com.g2dev.output.ext.SugarOutputProcessorCSV;
import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.Name_value;

public class P_04_MigrateOpportunities extends MigrationProcess {

	public P_04_MigrateOpportunities() {
		super("Opportunities");
		// TODO Auto-generated constructor stub
	}

	@Override
	public IntegrationMap buildMap(List<Field> source,
			List<com.sugarcrm.www.sugarcrm.Field> target) {
		return buildMap(source, target, moduleName);
	}

	public static void main1(String[] args) {
		new P_04_MigrateOpportunities().start();
		// new P_04_MigrateOpportunities().bckp();
	}

	@Override
	public void start() {
		IntegrationMap map = buildMap(moduleName);
		SugarOutputProcessorCSV output = new SugarOutputProcessorCSV(
				moduleName, map,
				new OpportunityRowListener(new AccountDedupe()));
		SugarSession.getInstance().loginForSandusky();
		TandemInput tandemInput = new TandemInput(moduleName);
		// tandemInput.setOffset(13800);
		tandemInput.setBatchSize(200);
		tandemInput.setSelectFields(names(map.getFromToMap().keySet()));
		// yyyy-MM-dd all>58023 date_entered>5148 date_modified>23475
		String query = ("opportunities.date_modified>20130501");
		tandemInput.start(query);
		System.out
				.println("Number of records " + tandemInput.getRecordsCount());
		Get_entry_list_result_version2 nextBatch = null;
		long startTimeMillis = System.currentTimeMillis();
		while ((nextBatch = tandemInput.nextBatch(query)) != null) {

			SugarSession.getInstance().loginForSandusky();
			output.process(SugarInput.resultToBatch(nextBatch,
					new ArrayList<DbField>(map.getFromToMap().keySet())));
			System.out.println("offset " + tandemInput.getOffset());
			// long timeSpend = (System.currentTimeMillis() - startTimeMillis);
			// float ap = (tandemInput.getRecordsCount() /
			// tandemInput.getOffset());
			// float remaining = timeSpend * ap;
			float remaining = (System.currentTimeMillis() - startTimeMillis)
					* tandemInput.getRecordsCount() / tandemInput.getOffset();
			System.out.println("ETA: " + (remaining));

			// timeMillis = System.currentTimeMillis();
		}

	}

	private void bckp() {

		IntegrationMap map = buildMap(moduleName);
		SugarOutputProcessorCSV output = new SugarOutputProcessorCSV(
				moduleName, map,
				new OpportunityRowListener(new AccountDedupe()));
		SugarSession.getInstance().loginForSandusky();
		TandemInput tandemInput = new TandemInput(moduleName);
		tandemInput.setBatchSize(200);
		tandemInput.setSelectFields(names(map.getFromToMap().keySet()));
		// yyyy-MM-dd all>58023 date_entered>5148 date_modified>23475
		String query = ("opportunities.date_modified>20150100");
		tandemInput.start(query);
		System.out
				.println("Number of records " + tandemInput.getRecordsCount());
		Get_entry_list_result_version2 nextBatch = null;
		long startTimeMillis = System.currentTimeMillis();
		while ((nextBatch = tandemInput.nextBatch(query)) != null) {

			writeInBeckupFile(nextBatch,
					tandemInput.getOffset() == tandemInput.getBatchSize());

		}

	}

	private static void writeInBeckupFile(
			Get_entry_list_result_version2 nextBatch, boolean writeColumns) {
		try {
			// BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
			// new FileOutputStream(
			// "C:\\TandemContacts\\opportunities.csv", true),
			// "UTF-8"));
			CSVWriter writer = new CSVWriter(new OutputStreamWriter(
					new FileOutputStream(
							"C:\\TandemContacts\\opportunities.csv", true),
					"UTF-8"), '\t');
			com.g2dev.job.custom.tandem.migration.sugar.Entry_value[] entry_list = nextBatch
					.getEntry_list();
			boolean columnsPrinted = false;
			for (com.g2dev.job.custom.tandem.migration.sugar.Entry_value entry_value : entry_list) {
				List<String> record = new ArrayList<String>();
				// String line = "";
				com.g2dev.job.custom.tandem.migration.sugar.Name_value[] name_value_list = entry_value
						.getName_value_list();
				// String columns = "";
				List<String> columns = new ArrayList<String>();
				for (com.g2dev.job.custom.tandem.migration.sugar.Name_value name_value : name_value_list) {
					// line += (name_value.getValue().replaceAll("\t", " ")
					// .replaceAll("\n", " ") + "\t");
					record.add(name_value.getValue());
					// columns += (name_value.getName() + "\t");
					columns.add(name_value.getName());
				}
				try {
					if (!columnsPrinted) {
						System.out.println("Columns:");
						System.out.println(columns);
						columnsPrinted = true;
					}
					if (writeColumns) {
						// out.write(columns + "\n");
						writer.writeNext(columns.toArray(new String[columns
								.size()]));
						writeColumns = false;
					}
					writer.writeNext(record.toArray(new String[record.size()]));
					// out.write(line + "\n");
					// System.out.println(line);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				// out.close();
			} catch (Exception e) {
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

	public static void main(String[] args) {
		new P_04_MigrateOpportunities().updateOppStage();
	}

	private void updateOppStage() {
		String query = "opportunities.date_modified>20130501";
		int batchSize = 200;
		int offset = 0;
		String[] select_fields = { "id", "sales_stage" };
		String module_name = "Opportunities";
		TandemSugarSession tandemSession = TandemSugarSession.getInstance();
		tandemSession.loginForTandem();
		SugarsoapPortType client = tandemSession.getSugar();
		try {
			int count = client.get_entries_count(tandemSession.getSessionId(),
					module_name, query, 0).getResult_count();
			while (offset < count) {
				Entry_value[] entry_list = client.get_entry_list(
						tandemSession.getSessionId(), module_name, query, null,
						offset, select_fields, null, batchSize, 0, false)
						.getEntry_list();
				List<Name_value[]> rows = new ArrayList<Name_value[]>();
				for (Entry_value entry_value : entry_list) {
					Name_value[] newStageNameValue = getNewStageNameValue(entry_value);
					if (newStageNameValue != null) {
						rows.add(newStageNameValue);
					}
				}
				updateRecords(rows);
				offset += batchSize;
				System.out.println(offset + " / " + count);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void updateRecords(List<Name_value[]> rows) {
		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		com.sugarcrm.www.sugarcrm.SugarsoapPortType client = session.getSugar();
		Name_value[][] name_value_lists = rows.toArray(new Name_value[rows
				.size()][]);
		try {
			client.set_entries(session.getSessionId(), getModuleName(),
					name_value_lists);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public static Name_value[] getNewStageNameValue(Entry_value entry_value) {
		Name_value[] newNVS = new Name_value[3];
		Name_value id = new Name_value("id", entry_value.getId());
		Name_value stage = null;
		Name_value stage1 = null;
		for (com.g2dev.job.custom.tandem.migration.sugar.Name_value oldNv : entry_value
				.getName_value_list()) {
			// salesstage_c:
			// Prospecting
			// Needs Analysis
			// Presentation Proposal
			// Negotiation/Review
			// Closed Won
			// Closed Lost

			// sales_stage:
			//
			if (oldNv.getName().equalsIgnoreCase("sales_stage")
					&& oldNv.getValue() != null && !oldNv.getValue().isEmpty()) {
				// sales_stage // salesstage_c
				String newStageName = null;
				if (oldNv.getValue().equalsIgnoreCase("Prospecting")) {
					newStageName = "Prospecting";
				} else if (oldNv.getValue().equalsIgnoreCase(
						"P3 Had Conversation")) {
					newStageName = "Needs Analysis";
				} else if (oldNv.getValue().equalsIgnoreCase(
						"P2 - Info/consideration")) {
					newStageName = "Presentation Proposal";
				} else if (oldNv.getValue().equalsIgnoreCase("P1 - Contract")) {
					newStageName = "Closed Won";
				} else if (oldNv.getValue().equalsIgnoreCase("Closed Lost")) {
					newStageName = "Closed Lost";
				} else if (oldNv.getValue().equalsIgnoreCase("Closed Won")) {
					newStageName = "Closed Won";
				} else if (oldNv.getValue().equalsIgnoreCase(
						"P4 - Possible Prospect")) {
					newStageName = "Prospecting";
				} else if (oldNv.getValue().equalsIgnoreCase("Dropped")) {
					newStageName = "Closed Lost";
				}// P4 – Possible Prospect
				if (newStageName != null) {
					stage = new Name_value("sales_stage", newStageName);
					stage1 = new Name_value("salesstage_c", newStageName);
				}
			}
		}
		if (stage != null) {
			newNVS[0] = id;
			newNVS[1] = stage;
			newNVS[2] = stage1;
			return newNVS;
		}

		return null;
	}

}
