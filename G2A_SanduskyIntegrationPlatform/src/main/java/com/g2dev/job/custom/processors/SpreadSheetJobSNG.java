package com.g2dev.job.custom.processors;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.g2dev.input.ext.InputCsv;
import com.g2dev.integrator.StaticProperties;
import com.g2dev.job.custom.SpreadSheet_SugarJob;
import com.g2dev.job.custom.NewRowListener;
import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.Name_value;
import com.sugarcrm.www.sugarcrm.rest.SugarRestSession;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarApiException;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarClient;
import com.sugarcrm.www.sugarcrm.rest.v4.impl.SugarBean;

public class SpreadSheetJobSNG extends SpreadSheet_SugarJob {

	private Map<String, String> accountNumberIdMap;// = new HashMap<String,
													// String>();

	public SpreadSheetJobSNG() {
		super("SNGR1_SNGRevenueDetail");
	}

	private void loadAccounts(List<String> accountNumbers) {
		if (accountNumberIdMap != null) {
			return;
		}
		accountNumberIdMap = new HashMap<String, String>();

		SugarRestSession session = SugarRestSession.getInstance();
		String query = null;// buildQuery(accountNumbers);
		SugarClient client = session.getSanduskyClient();
		if (client != null) {
			String accNumberName = "acctno_c"/* findOldIdName(moduleName) */;
			String[] select_fields = { "id", "acctno_c" };
			try {
				int batchSize = 2000;
				int offset = 0;
				int numberOfRecords = client.getEntriesCount(
						session.getSession(), "Accounts", query, 0);
				long startTime = System.currentTimeMillis();
				while (numberOfRecords > (offset + batchSize) || offset == 0) {

					List<SugarBean> entryList = client.getEntryList(
							session.getSession(), "Accounts", query, null,
							offset, select_fields, batchSize, 0);
					for (SugarBean entry_value : entryList) {
						String id = entry_value.get("id").replaceAll("\"", "");
						String accNumber = entry_value.get(accNumberName)
								.replaceAll("\"", "");

						System.out.println(accNumber + " | " + id);
						accountNumberIdMap.put(accNumber, id);
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

	private String buildQuery(List<String> accountNumbers) {
		String query = "(";
		for (String accNumber : accountNumbers) {
			query += "accounts.acctno_c='" + accNumber + "' or ";
		}
		query = query.substring(0, query.length() - 4);
		query += ")";
		return query;
	}

	private Random random = new Random();

	@Override
	public void start() {

		setFirstRowHeader(true);
		inputSpreadSheet = new InputCsv();
		inputSpreadSheet.setForceInput(false);
		// inputSpreadSheet.setSeparator(',');
		inputSpreadSheet.setSeparator('\t');
		inputSpreadSheet.setBatchSize(200);
		defaultStart(new File(StaticProperties.SNG_MAP_FILE_PATH),
				new NewRowListener() {

					public List<Name_value> newRowEvent(
							List<Name_value> currentRow) {
						Name_value accountId = new Name_value("account_id_c",
								"");
						Name_value name = new Name_value("name", "SNG "
								+ generateRandomNumber());
						for (Name_value name_value : currentRow) {
							if (name_value.getName().equalsIgnoreCase(
									"accountnumber")) {
								accountId.setValue(getAccountId(name_value
										.getValue()));
							}
							if (name_value.getName().equalsIgnoreCase("name")) {
								name.setValue(name_value.getValue());
								System.out.println("name " + name.getValue());
							}
							if (name_value.getName().equalsIgnoreCase(
									"run_date")) {
								name_value.setValue(formatDate(name_value
										.getValue()));
							}
						}
						List<Name_value> custom = new ArrayList<Name_value>();
						custom.add(accountId);
						custom.add(name);
						return custom;
					}

					private String generateRandomNumber() {
						String number = "";
						for (int i = 0; i < 6; i++) {
							number += (random.nextInt(9));
						}
						return number;
					}

					public void afterInsertBatch(
							List<List<Name_value>> targetBatch) {
						// TODO Auto-generated method stub

					}

					public void beforeProcessBatch(List<String[]> sourceBatch) {
						List<String> accNumbers = new ArrayList<String>();
						for (String[] strings : sourceBatch) {
							// this is not generic I know the index from the map
							// file
							String accNumber = strings[0];
							if (accNumber != null
									&& !accNumbers.contains(accNumber)) {
								accNumbers.add(accNumber);
							}
						}
						loadAccounts(accNumbers);
					}

					public List<List<Name_value>> trimRows(
							List<List<Name_value>> targetBatch) {
						if (targetBatch != null && !targetBatch.isEmpty()) {
							List<List<Name_value>> newBatch = new ArrayList<List<Name_value>>();
							List<List<Name_value>> rowsToBeRemoved = new ArrayList<List<Name_value>>();
							for (List<Name_value> list : targetBatch) {
								if (list != null && !list.isEmpty()) {
									for (Name_value name_value2 : list) {
										if (name_value2.getName()
												.equalsIgnoreCase("rate_code")) {
											if ((name_value2.getValue().equals(
													"HOUSD") || name_value2
													.getValue().equals("HOUSE"))) {
												rowsToBeRemoved.add(list);
												// Account number 10000 and
												// 10005
												break;
											} else {
												newBatch.add(list);
											}

										}
									}
								}
							}
							boolean removeAll = targetBatch
									.removeAll(rowsToBeRemoved);
							System.out.println("Removed HOUSE/HOUSD records "
									+ removeAll);
							// return newBatch;
						}
						return targetBatch;
					}
				}, true);

	}

	private String getAccountId(String accountNumber) {
		String accID = "";
		if (accountNumber != null
				&& accountNumberIdMap.containsKey(accountNumber)) {
			accID = accountNumberIdMap.get(accountNumber);
		}
		return accID;
	}

	private static String formatDate(String value) {
		String dateString = "";
		if (value != null) {
			try {
				return new SimpleDateFormat("yyyy-mm-dd")
						.format(new SimpleDateFormat("mm/dd/yyyy").parse(value));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
		}
		return dateString;
	}

	public static void main(String[] args) {
		// System.out.println(formatDate("2/1/2015"));
		SpreadSheetJobSNG process = new SpreadSheetJobSNG();
		process.setSpreadSheetFile(new File(StaticProperties.SNG_DATA_FILE_PATH));
		process.start();
	}

}
