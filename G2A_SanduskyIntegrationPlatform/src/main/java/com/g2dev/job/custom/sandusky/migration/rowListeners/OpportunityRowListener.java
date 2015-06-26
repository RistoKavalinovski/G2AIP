package com.g2dev.job.custom.sandusky.migration.rowListeners;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.g2dev.connect.DbField;
import com.g2dev.connect.DbObject;
import com.g2dev.connect.Schema;
import com.g2dev.job.custom.AccountDedupe;
import com.g2dev.job.custom.NewRowListener;
import com.g2dev.map.IntegrationMap;
import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.Name_value;
import com.sugarcrm.www.sugarcrm.New_set_entries_result;

public class OpportunityRowListener implements NewRowListener {

	private AccountDedupe accountDedupe;
	private Name_value marketNameValue;
	private IntegrationMap revenueLineItemsMap;

	public OpportunityRowListener(AccountDedupe accountDedupe) {
		super();
		this.accountDedupe = accountDedupe;
	}

	public List<Name_value> newRowEvent(List<Name_value> currentRow) {
		// TODO STAGES
		Name_value oldId = new Name_value("oldid_c", "");
		Name_value newId = new Name_value("new_with_id", "");
		for (Name_value name_value : currentRow) {
			if (name_value.getName().equalsIgnoreCase("id")) {
				oldId.setValue(name_value.getValue());
				newId.setValue("1");
			}
			if (name_value.getName().equalsIgnoreCase("deleted")) {
				name_value.setValue("0");
			}
		}
		List<Name_value> custom = new ArrayList<Name_value>();
		custom.add(oldId);
		custom.add(newId);
		custom.add(getMarketNameValue());
		return custom;
	}

	public static void main(String[] args) {
		// try {
		// SugarSession.getInstance().loginForSandusky();
		// Module_fields get_module_fields = SugarSession
		// .getInstance()
		// .getSugar()
		// .get_module_fields(
		// SugarSession.getInstance().getSessionId(), "Users");
		// Field[] module_fields = get_module_fields.getModule_fields();
		// for (Field field : module_fields) {
		// if (field.getName().equals("market_c")) {
		// Name_value[] options = field.getOptions();
		// for (Name_value name_value : options) {
		// System.out.println(name_value.getValue());
		// }
		// }
		// }
		// } catch (RemoteException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// SugarSession.getInstance().printModules();

		// System.out.println("RevenueLineItems fields :_______________");
		// SugarSession.getInstance().printModuleFields("Opportunities");

		// System.out.println("Opportunities fields :_______________");
		// SugarSession.getInstance().printModuleFields("Opportunities");
		// try {
		// List<com.sugarcrm.www.sugarcrm.Field> asList;
		// asList = Arrays.asList(SugarSession
		// .getInstance()
		// .getSugar()
		// .get_module_fields(
		// SugarSession.getInstance().getSessionId(),
		// "Opportunities").getModule_fields());
		// List<com.sugarcrm.www.sugarcrm.Field> asList1 = Arrays
		// .asList(SugarSession
		// .getInstance()
		// .getSugar()
		// .get_module_fields(
		// SugarSession.getInstance().getSessionId(),
		// "RevenueLineItems").getModule_fields());
		// new OpportunityRowListener(null).buildMap(asList, asList1,
		// "Opportunities", "RevenueLineItems");
		// } catch (RemoteException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// Date: 2014-07-30
		// Date: 2013-10-08
		// Date: 2013-02-27
		// Date: 2013-12-10
		// Date: 2014-08-12
		// Date: 2013-06-11
		// Date: 2015-03-31
		// Date: 2014-06-27
		// Date: 2014-09-29
		// Date: 2014-02-14
		// Date: 2014-02-21
		// Date: 2013-04-19
		System.out.println(formatDate("2013-04-19"));
		testOpportunityBatch();

	}

	private static void testOpportunityBatch() {/*
												 * SugarSession session =
												 * SugarSession.getInstance();
												 * if
												 * (session.loginForSandusky())
												 * { String module_name =
												 * "RevenueLineItems"; String[]
												 * select_fields = {
												 * "date_closed",
												 * "date_closed_timestamp" };
												 * try { Entry_value[]
												 * entry_list = session
												 * .getSugar()
												 * .get_entry_list(session
												 * .getSessionId(), module_name,
												 * null, null, 0, select_fields,
												 * 100, 0) .getEntry_list(); for
												 * (Entry_value entry_value :
												 * entry_list) { Name_value[]
												 * name_value_list = entry_value
												 * .getName_value_list(); for
												 * (Name_value name_value :
												 * name_value_list) {
												 * System.out.
												 * println(name_value.getName()
												 * + " | " +
												 * name_value.getValue()); } } }
												 * catch (RemoteException e) {
												 * // TODO Auto-generated catch
												 * block e.printStackTrace(); }
												 * }
												 */
	}

	private IntegrationMap buildRevenueLineItemsMap() {
		try {
			List<com.sugarcrm.www.sugarcrm.Field> asList;
			asList = Arrays.asList(SugarSession
					.getInstance()
					.getSugar()
					.get_module_fields(
							SugarSession.getInstance().getSessionId(),
							"Opportunities", null).getModule_fields());
			List<com.sugarcrm.www.sugarcrm.Field> asList1 = Arrays
					.asList(SugarSession
							.getInstance()
							.getSugar()
							.get_module_fields(
									SugarSession.getInstance().getSessionId(),
									"RevenueLineItems", null)
							.getModule_fields());
			return new OpportunityRowListener(null).buildMap(asList, asList1,
					"Opportunities", "RevenueLineItems");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private IntegrationMap buildMap(
			List<com.sugarcrm.www.sugarcrm.Field> sourceFields,
			List<com.sugarcrm.www.sugarcrm.Field> targetFields,
			String sourceModuleName, String targetModuleName) {

		IntegrationMap im = new IntegrationMap();
		System.out.println("Target size " + targetFields.size());
		System.out.println("Source size " + sourceFields.size());

		Schema sourceSchema = new Schema();
		DbObject sourceObject = new DbObject(sourceSchema);
		sourceObject.setName(sourceModuleName);
		sourceSchema.getObjects().add(sourceObject);

		Schema targetSchema = new Schema();
		DbObject targetObject = new DbObject(targetSchema);
		targetObject.setName(targetModuleName);
		targetSchema.getObjects().add(targetObject);

		List<String> noMatch = new ArrayList<String>();
		List<String> matchedList = new ArrayList<String>();
		im.getFrom().add(sourceSchema);
		im.setTo(targetSchema);
		int c = 0;
		for (com.sugarcrm.www.sugarcrm.Field sourceField : sourceFields) {
			boolean matched = false;
			for (com.sugarcrm.www.sugarcrm.Field targetField : targetFields) {
				if (sourceField.getName().equals(targetField.getName())
				/* && sourceField.getType().equals(targetField.getType()) */) {
					matched = true;
					DbField sourceDbField = new DbField(sourceObject);
					sourceObject.getFields().add(sourceDbField);
					sourceDbField.setIndex(c);
					sourceDbField.setName(sourceField.getName());
					sourceDbField.setType(sourceField.getType());
					sourceDbField.setObject(sourceObject);

					DbField targetDbField = new DbField(targetObject);
					targetObject.getFields().add(targetDbField);
					targetDbField.setIndex(c);
					targetDbField.setName(targetField.getName());
					targetDbField.setType(targetField.getType());
					targetDbField.setObject(targetObject);

					im.getFromToMap().put(sourceDbField, targetDbField);
					c++;
					continue;
				}
				// else if (sourceField.getName().equals(targetField.getName()))
				// {
				// System.out.println(">>>>>>");
				// System.out.println(sourceField.getName() + " | "
				// + targetField.getName());
				// System.out.println(sourceField.getType() + " | "
				// + targetField.getType());
				// System.out.println("<<<<<<");
				// }
			}
			if (!matched) {
				noMatch.add(sourceField.getName() + " | "
						+ sourceField.getType());
			} else {
				matchedList.add(sourceField.getName() + " | "
						+ sourceField.getType());
			}
		}
		System.out.println("fields matched " + c);

		System.out.println("Fields failed to match: " + noMatch);
		System.out.println("Fields matched: " + matchedList);

		return im;

	}

	public AccountDedupe getAccountDedupe() {
		return accountDedupe;
	}

	public void setAccountDedupe(AccountDedupe accountDedupe) {
		this.accountDedupe = accountDedupe;
	}

	public Name_value getMarketNameValue() {
		if (marketNameValue == null) {
			marketNameValue = new Name_value("market_c", "Sandusky");

		}
		return marketNameValue;
	}

	public void setMarket(Name_value market) {
		this.marketNameValue = market;
	}

	public void afterInsertBatch(List<List<Name_value>> oppsBatch) {
		if (revenueLineItemsMap == null) {
			revenueLineItemsMap = buildRevenueLineItemsMap();
		}
		Name_value[][] targetBatch = new Name_value[oppsBatch.size()][];
		for (int i = 0; i < oppsBatch.size(); i++) {
			List<Name_value> oppRecords = oppsBatch.get(i);
			List<Name_value> record = new ArrayList<Name_value>();
			record.add(getMarketNameValue());
			for (int j = 0; j < oppRecords.size(); j++) {
				Name_value oppField = oppRecords.get(j);
				if (oppField.getName().equals("id")) {
					record.add(new Name_value("opportunity_id", oppField
							.getValue()));
				} else if (oppField.getName().equals("name")) {
					record.add(new Name_value("name", oppField.getValue()
							+ " RLI"));
				} /*
				 * else if (oppField.getName().equalsIgnoreCase("date_closed")
				 * || oppField.getName().equalsIgnoreCase(
				 * "date_closed_timestamp")) { System.out.println("Date: " +
				 * oppField.getValue());
				 * 
				 * record.add(new Name_value(revenueLineItemsMap
				 * .getFromToMap().get(oppField.getName()).getName(),
				 * formatDate(oppField.getValue())));
				 * 
				 * }
				 */else if (revenueLineItemsMap.getFromToMap().containsKey(
						oppField.getName())) {
					record.add(new Name_value(revenueLineItemsMap
							.getFromToMap().get(oppField.getName()).getName(),
							oppField.getValue()));
				}

			}
			targetBatch[i] = record.toArray(new Name_value[record.size()]);
		}

		try {
			New_set_entries_result set_entries = SugarSession
					.getInstance()
					.getSugar()
					.set_entries(SugarSession.getInstance().getSessionId(),
							"RevenueLineItems", targetBatch);
			System.out.println(set_entries);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static String formatDate(String value) {
		return value;
		// String dateString = "";
		// if (value != null) {
		// try {
		// return new SimpleDateFormat("mm/dd/yyyy")
		// .format(new SimpleDateFormat("yyyy-mm-dd").parse(value));
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// return dateString;
	}

	public void beforeProcessBatch(List<String[]> sourceBatch) {
	}

	public List<List<Name_value>> trimRows(List<List<Name_value>> targetBatch) {
		// TODO Auto-generated method stub
		return null;
	}
}
