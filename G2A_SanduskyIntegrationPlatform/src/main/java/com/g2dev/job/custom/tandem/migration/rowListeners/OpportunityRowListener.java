package com.g2dev.job.custom.tandem.migration.rowListeners;

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
		Name_value oldCloseDate = new Name_value("oldclosedate_c", "");
		Name_value oldAccount = new Name_value("oldaccount_c", "");
		for (Name_value name_value : currentRow) {
			if (name_value.getName().equalsIgnoreCase("id")) {
				oldId.setValue(name_value.getValue());
				newId.setValue("1");
				// remove after hard delete
				// name_value.setValue("");
			}
			if (name_value.getName().equalsIgnoreCase("deleted")) {
				name_value.setValue("0");
			}
			if (name_value.getName().equalsIgnoreCase("date_closed")) {
				oldCloseDate.setValue(name_value.getValue());
			}
			if (name_value.getName().equalsIgnoreCase("account_name")) {
				oldAccount.setValue(name_value.getValue());
			}

		}
		List<Name_value> custom = new ArrayList<Name_value>();
		custom.add(oldId);
		// uncomment after hard delete
		custom.add(newId);
		custom.add(getMarketNameValue());
		custom.add(oldCloseDate);
		custom.add(oldAccount);
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
		System.out.println("RevenueLineItems fields :_______________");
		SugarSession.getInstance().printModuleFields("RevenueLineItems");
		System.out.println("Opportunities fields :_______________");
		SugarSession.getInstance().printModuleFields("Opportunities");
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
			new OpportunityRowListener(null).buildMap(asList, asList1,
					"Opportunities", "RevenueLineItems");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
			}
		}
		System.out.println("fields matched " + c);
		System.out.println("Fields failed to match: " + noMatch);

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
			marketNameValue = new Name_value("market_c", "Tandem");

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
			for (int j = 0; j < oppRecords.size(); j++) {
				Name_value oppField = oppRecords.get(j);
				if (oppField.getName().equals("id")) {
					record.add(new Name_value("opportunity_id", oppField
							.getValue()));
				} else if (oppField.getName().equals("id")) {
				} else if (revenueLineItemsMap.getFromToMap().containsKey(
						oppField.getName())) {
					record.add(new Name_value(revenueLineItemsMap
							.getFromToMap().get(oppField.getName()).getName(),
							oppField.getValue()));
				} else if (oppField.getName().equalsIgnoreCase("date_closed")) {
					record.add(new Name_value("oldclosedate_c", oppField
							.getValue()));
				} else if (oppField.getName().equalsIgnoreCase("amount")) {
					record.add(new Name_value("the_total_amount_c", oppField
							.getValue()));
				}
			}
			record.add(new Name_value("name", "oldcrm"));
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

	public void beforeProcessBatch(List<String[]> sourceBatch) {
	}

	public List<List<Name_value>> trimRows(List<List<Name_value>> targetBatch) {
		// TODO Auto-generated method stub
		return null;
	}

}
