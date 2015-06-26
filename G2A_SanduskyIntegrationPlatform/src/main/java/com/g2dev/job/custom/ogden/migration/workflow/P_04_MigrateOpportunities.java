package com.g2dev.job.custom.ogden.migration.workflow;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVWriter;

import com.g2dev.connect.DbField;
import com.g2dev.job.custom.AccountDedupe;
import com.g2dev.job.custom.SugarInput;
import com.g2dev.job.custom.ogden.migration.OgdenInput;
import com.g2dev.job.custom.ogden.migration.OgdenSugarSession;
import com.g2dev.job.custom.ogden.migration.rowListeners.OpportunityRowListener;
import com.g2dev.job.custom.ogden.migration.sugar.Entry_value;
import com.g2dev.job.custom.ogden.migration.sugar.Field;
import com.g2dev.job.custom.ogden.migration.sugar.Get_entry_list_result_version2;
import com.g2dev.job.custom.ogden.migration.sugar.SugarsoapPortType;
import com.g2dev.map.IntegrationMap;
import com.g2dev.output.ext.SugarOutputProcessorCSV;
import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.Name_value;
import com.sugarcrm.www.sugarcrm.rest.SugarRestSession;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarApiException;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarClient;
import com.sugarcrm.www.sugarcrm.rest.v4.impl.SugarBean;

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
		OgdenInput ogdenInput = new OgdenInput(moduleName);
		// ogdenInput.setOffset(13800);
		ogdenInput.setBatchSize(200);
		ogdenInput.setSelectFields(names(map.getFromToMap().keySet()));
		// yyyy-MM-dd all>58023 date_entered>5148 date_modified>23475
		String query = ("opportunities.date_modified>20150200");
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

	private void bckp() {

		IntegrationMap map = buildMap(moduleName);
		SugarOutputProcessorCSV output = new SugarOutputProcessorCSV(
				moduleName, map,
				new OpportunityRowListener(new AccountDedupe()));
		SugarSession.getInstance().loginForSandusky();
		OgdenInput ogdenInput = new OgdenInput(moduleName);
		ogdenInput.setBatchSize(200);
		ogdenInput.setSelectFields(names(map.getFromToMap().keySet()));
		// yyyy-MM-dd all>58023 date_entered>5148 date_modified>23475
		String query = ("opportunities.date_modified>20150100");
		ogdenInput.start(query);
		System.out.println("Number of records " + ogdenInput.getRecordsCount());
		Get_entry_list_result_version2 nextBatch = null;
		long startTimeMillis = System.currentTimeMillis();
		while ((nextBatch = ogdenInput.nextBatch(query)) != null) {

			writeInBeckupFile(nextBatch,
					ogdenInput.getOffset() == ogdenInput.getBatchSize());

		}

	}

	private static void writeInBeckupFile(
			Get_entry_list_result_version2 nextBatch, boolean writeColumns) {
		try {
			// BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
			// new FileOutputStream(
			// "C:\\OgdenContacts\\opportunities.csv", true),
			// "UTF-8"));
			CSVWriter writer = new CSVWriter(new OutputStreamWriter(
					new FileOutputStream(
							"C:\\OgdenContacts\\opportunities.csv", true),
					"UTF-8"), '\t');
			com.g2dev.job.custom.ogden.migration.sugar.Entry_value[] entry_list = nextBatch
					.getEntry_list();
			boolean columnsPrinted = false;
			for (com.g2dev.job.custom.ogden.migration.sugar.Entry_value entry_value : entry_list) {
				List<String> record = new ArrayList<String>();
				// String line = "";
				com.g2dev.job.custom.ogden.migration.sugar.Name_value[] name_value_list = entry_value
						.getName_value_list();
				// String columns = "";
				List<String> columns = new ArrayList<String>();
				for (com.g2dev.job.custom.ogden.migration.sugar.Name_value name_value : name_value_list) {
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
		 new P_04_MigrateOpportunities().updateRevenueLineItemsAmount();
//		SugarSession.getInstance().printModuleFields("RevenueLineItems");
	}

	private void updateRevenueLineItemsAmount() {
		Map<String, String> ogdenOppOldAmounts = loadOgdenOppOldAmounts();
		// the_total_amount_c
		String[] selectFields = { "id", "the_total_amount_c", "opportunity_id" };
		int offset = 0;
		SugarRestSession session = SugarRestSession.getInstance();
		SugarClient client = session.getSanduskyClient();
		int entriesCount = 0;
		try {
			entriesCount = client.getEntriesCount(session.getSession(),
					"RevenueLineItems", null, 0);
		} catch (SugarApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int batchSize = 2000;
		int updateBatchSize = 200;
		List<Name_value[]> rlisForUpdate = new ArrayList<Name_value[]>();
		while (offset < entriesCount) {
			try {
				List<SugarBean> entryList = client.getEntryList(
						session.getSession(), "RevenueLineItems", null, null, offset,
						selectFields, batchSize, 0);
				for (SugarBean sugarBean : entryList) {
					String oppId = sugarBean.get("opportunity_id");
					if (oppId != null && !oppId.isEmpty()) {
						oppId = oppId.replaceAll("\"", "");
						if (ogdenOppOldAmounts.containsKey(oppId)) {
							String id = sugarBean.get("id");
							if (id != null && !id.isEmpty()) {
								id = id.replaceAll("\"", "");
								Name_value[] rliForUpdate = new Name_value[2];
								rliForUpdate[0] = new Name_value("id", id);
								rliForUpdate[1] = new Name_value(
										"the_total_amount_c",
										ogdenOppOldAmounts.get(oppId));
								rlisForUpdate.add(rliForUpdate);
							}
						}
					}
					if (rlisForUpdate.size() == updateBatchSize) {
						updateRecords(rlisForUpdate, "RevenueLineItems");
						rlisForUpdate.clear();
					}
				}
			} catch (SugarApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			offset += batchSize;
			System.out.println(offset + " / " + entriesCount);
		}
		if (!rlisForUpdate.isEmpty()) {
			updateRecords(rlisForUpdate, "RevenueLineItems");
		}
	}

	private Map<String, String> loadOgdenOppOldAmounts() {

		SugarRestSession session = SugarRestSession.getInstance();
		SugarClient client = session.getSanduskyClient();
		String[] selectFields = { "id", "market_c", "print_amount_c" };
		int entriesCount = 0;
		try {
			entriesCount = client.getEntriesCount(session.getSession(),
					getModuleName(), null, 0);
		} catch (SugarApiException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int offset = 0;
		int batchSize = 2000;
		Map<String, String> ogdenOppsAmount = new HashMap<String, String>();
		while (offset < entriesCount) {
			try {
				List<SugarBean> entryList = client.getEntryList(
						session.getSession(), getModuleName(), null, null,
						offset, selectFields, batchSize, 0);
				for (SugarBean sugarBean : entryList) {
					String market = sugarBean.get("market_c");
					if (market != null
							&& !market.isEmpty()
							&& (market.equalsIgnoreCase("Ogden") || market
									.equalsIgnoreCase("\"Ogden\""))) {
						String id = sugarBean.get("id");
						if (id != null && !id.isEmpty()) {
							String amount = sugarBean.get("print_amount_c");
							if (amount != null && !amount.isEmpty()) {
								ogdenOppsAmount.put(id.replaceAll("\"", ""),
										amount.replaceAll("\"", ""));
							}
						}
					}
				}
			} catch (SugarApiException e) {
				e.printStackTrace();
			}
			offset += batchSize;
			System.out.println(offset + " / " + entriesCount);
		}
		// System.out.println("OgdenOppsSize > " + ogdenOppIDs.size());

		return ogdenOppsAmount;
	}

	private void updateOppStage() {
		String query = "opportunities.date_modified>20140701";
		int batchSize = 200;
		int offset = 0;
		String[] select_fields = { "id", "sales_stage" };
		String module_name = "Opportunities";
		OgdenSugarSession ogdenSession = OgdenSugarSession.getInstance();
		ogdenSession.loginForOgden();
		SugarsoapPortType client = ogdenSession.getSugar();
		try {
			int count = client.get_entries_count(ogdenSession.getSessionId(),
					module_name, query, 0).getResult_count();
			while (offset < count) {
				Entry_value[] entry_list = client.get_entry_list(
						ogdenSession.getSessionId(), module_name, query, null,
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
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void updateRecords(List<Name_value[]> rows) {
		updateRecords(rows, getModuleName());
	}

	private void updateRecords(List<Name_value[]> rows, String moduleName) {
		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		com.sugarcrm.www.sugarcrm.SugarsoapPortType client = session.getSugar();
		Name_value[][] name_value_lists = rows.toArray(new Name_value[rows
				.size()][]);
		try {
			client.set_entries(session.getSessionId(), moduleName,
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
		for (com.g2dev.job.custom.ogden.migration.sugar.Name_value oldNv : entry_value
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
						"P2 Presented Considering")) {
					newStageName = "Presentation Proposal";
				} else if (oldNv.getValue().equalsIgnoreCase(
						"P1 Sold Signed Agreement")) {
					newStageName = "Closed Won";
				} else if (oldNv.getValue().equalsIgnoreCase("Closed Lost")) {
					newStageName = "Closed Lost";
				} else if (oldNv.getValue().equalsIgnoreCase("Closed Won")) {
					newStageName = "Closed Won";
				} else if (oldNv.getValue().equalsIgnoreCase(
						"P4 – Possible Prospect")) {
					newStageName = "Prospecting";
				} else if (oldNv.getValue().equalsIgnoreCase(
						"Closed Opportunity Early Cancellation")) {
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
