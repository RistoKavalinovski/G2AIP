package com.g2dev.job.custom.processors;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.g2dev.input.ext.InputCsv;
import com.g2dev.input.ext.InputExcel;
import com.g2dev.integrator.StaticProperties;
import com.g2dev.job.custom.SpreadSheet_SugarJob;
import com.g2dev.job.custom.NewRowListener;
import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.Name_value;
import com.sugarcrm.www.sugarcrm.rest.SugarRestSession;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarApiException;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarClient;
import com.sugarcrm.www.sugarcrm.rest.v4.impl.SugarBean;

public class SpreadSheetJobInfoGroup extends SpreadSheet_SugarJob {

	private Map<String, String> leadsLocnumIdMap;// = new HashMap<String,
													// String>();// locnum_c
	private boolean isExcel = false;
	private static final String FIELD_NAME_LOCK_NUMBER = "locnum_c";

	public SpreadSheetJobInfoGroup() {
		super("Leads");
	}

	public static void main(String[] args) {
		new SpreadSheetJobInfoGroup().removeDuplicateLeads();
	}

	private static String formatDate(String value) {
		String dateString = "";
		if (value != null) {
			try {
				return new SimpleDateFormat("yyyy-mm-dd")
						.format(new SimpleDateFormat("yyyymm").parse(value));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
		}
		return dateString;
	}

	@Override
	public void setSpreadSheetFile(File csvFile) {
		isExcel = (csvFile.getName().contains(".xlsx"));
		super.setSpreadSheetFile(csvFile);
	}

	@Override
	public void start() {

		setFirstRowHeader(true);
		inputSpreadSheet = isExcel ? new InputExcel() : new InputCsv();
		inputSpreadSheet.setForceInput(false);
		inputSpreadSheet.setSeparator(',');
		inputSpreadSheet.setBatchSize(200);
		defaultStart(new File(StaticProperties.INFO_GROUP_MAP_FILE_PATH),
				new NewRowListener() {

					public List<Name_value> newRowEvent(
							List<Name_value> currentRow) {
						// Name_value name = new Name_value("name", "SNG "
						// + generateRandomNumber());
						List<Name_value> custom = new ArrayList<Name_value>();
						for (Name_value name_value : currentRow) {
							if (name_value.getName().equalsIgnoreCase(
									FIELD_NAME_LOCK_NUMBER)
									&& leadsLocnumIdMap.containsKey(name_value
											.getValue())) {
								custom.add(new Name_value("id",
										leadsLocnumIdMap.get(name_value
												.getValue())));
							}
							// if
							// (name_value.getName().equalsIgnoreCase("name")) {
							// name.setValue(name_value.getValue());
							// System.out.println("name " + name.getValue());
							// }
							// if (name_value.getName().equalsIgnoreCase(
							// "run_date")) {
							// name_value.setValue(formatDate(name_value
							// .getValue()));
							// }
						}
						// custom.add(accountId);
						// custom.add(name);
						custom.add(new Name_value("market_c", getMarket()));
						return custom;
					}

					// private String generateRandomNumber() {
					// String number = "";
					// for (int i = 0; i < 6; i++) {
					// number += (random.nextInt(9));
					// }
					// return number;
					// }

					public void afterInsertBatch(
							List<List<Name_value>> targetBatch) {
						// TODO Auto-generated method stub

					}

					public void beforeProcessBatch(List<String[]> sourceBatch) {
						List<String> leadLockNumbers = new ArrayList<String>();
						for (String[] strings : sourceBatch) {
							// this is not generic I know the index from the map
							// file //TODO find a way this not to be hard-coded
							String leadLockNumber = strings[90];
							if (leadLockNumber != null
									&& !leadLockNumbers
											.contains(leadLockNumber)) {
								leadLockNumbers.add(leadLockNumber);
							}
						}
						loadLeads(leadLockNumbers);
					}

					public List<List<Name_value>> trimRows(
							List<List<Name_value>> targetBatch) {
						// TODO Auto-generated method stub
						return null;
					}
				}, false);

	}

	private void loadLeads(List<String> locNumbers) {
		if (leadsLocnumIdMap != null) {
			return;
		}
		leadsLocnumIdMap = new HashMap<String, String>();

		SugarRestSession session = SugarRestSession.getInstance();
		String query = null;// buildQuery(accountNumbers);
		SugarClient client = session.getSanduskyClient();
		if (client != null) {
			String leadLockNumberName = FIELD_NAME_LOCK_NUMBER/*
															 * findOldIdName(
															 * moduleName)
															 */;
			String[] select_fields = { "id", FIELD_NAME_LOCK_NUMBER };
			try {
				int batchSize = 2000;
				int offset = 0;
				int numberOfRecords = client.getEntriesCount(
						session.getSession(), getModuleName(), query, 0);
				long startTime = System.currentTimeMillis();
				while (numberOfRecords > (offset + batchSize) || offset == 0) {

					List<SugarBean> entryList = client.getEntryList(
							session.getSession(), getModuleName(), query, null,
							offset, select_fields, batchSize, 0);
					for (SugarBean entry_value : entryList) {
						String id = entry_value.get("id").replaceAll("\"", "");
						String leadLockNumber = entry_value.get(
								leadLockNumberName).replaceAll("\"", "");

						System.out.println(leadLockNumber + " | " + id);
						leadsLocnumIdMap.put(leadLockNumber, id);
					}
					offset += batchSize;
					SugarSession.getInstance().printETA(numberOfRecords,
							offset, startTime);
					// startTime = System.currentTimeMillis();
				}
			} catch (SugarApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// SugarRestSession session = SugarRestSession.getInstance();
		// String query = "";
		// String[] select_fields = {"id","acctno_c"};
		// session.getSanduskyClient().

	}

	private void removeDuplicateLeads() {
		Map<String, List<String>> locNumLeadIdsMap = new HashMap<String, List<String>>();

		SugarRestSession session = SugarRestSession.getInstance();
		String query = null;// buildQuery(accountNumbers);
		SugarClient client = session.getSanduskyClient();
		if (client != null) {
			String leadLockNumberName = FIELD_NAME_LOCK_NUMBER/*
															 * findOldIdName(
															 * moduleName)
															 */;
			String[] select_fields = { "id", FIELD_NAME_LOCK_NUMBER };
			try {
				int batchSize = 2000;
				int offset = 0;
				int numberOfRecords = client.getEntriesCount(
						session.getSession(), getModuleName(), query, 0);
				long startTime = System.currentTimeMillis();
				while (numberOfRecords > (offset + batchSize) || offset == 0) {

					List<SugarBean> entryList = client.getEntryList(
							session.getSession(), getModuleName(), query, null,
							offset, select_fields, batchSize, 0);
					for (SugarBean entry_value : entryList) {
						String id = entry_value.get("id").replaceAll("\"", "");
						String leadLockNumber = entry_value.get(
								leadLockNumberName).replaceAll("\"", "");

						System.out.println(leadLockNumber + " | " + id);
						if (!leadLockNumber.isEmpty()) {
							if (!locNumLeadIdsMap.containsKey(leadLockNumber)) {
								locNumLeadIdsMap.put(leadLockNumber,
										new ArrayList<String>());
							}
							locNumLeadIdsMap.get(leadLockNumber).add(id);
						}
					}
					offset += batchSize;
					SugarSession.getInstance().printETA(numberOfRecords,
							offset, startTime);
					// startTime = System.currentTimeMillis();
				}
			} catch (SugarApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// SugarRestSession session = SugarRestSession.getInstance();
		// String query = "";
		// String[] select_fields = {"id","acctno_c"};
		// session.getSanduskyClient().

		System.out.println(locNumLeadIdsMap.keySet().size());
		List<String> duplicateRecordIds = new ArrayList<String>();
		for (String locNum : locNumLeadIdsMap.keySet()) {
			List<String> list = locNumLeadIdsMap.get(locNum);
			if (list.size() > 1) {
				list.remove(0);
				duplicateRecordIds.addAll(list);
			}
		}

		System.out.println(duplicateRecordIds.size());
		SugarSession.deleteRecords("Leads", duplicateRecordIds);

	}

}
