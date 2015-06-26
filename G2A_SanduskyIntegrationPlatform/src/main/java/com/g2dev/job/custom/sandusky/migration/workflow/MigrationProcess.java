package com.g2dev.job.custom.sandusky.migration.workflow;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.g2dev.connect.DbField;
import com.g2dev.connect.DbObject;
import com.g2dev.connect.Schema;
import com.g2dev.job.custom.AccountDedupe;
import com.g2dev.job.custom.SugarInput;
import com.g2dev.job.custom.sandusky.migration.SanduskyInput;
import com.g2dev.job.custom.sandusky.migration.SanduskySugarSession;
import com.g2dev.job.custom.sandusky.migration.rowListeners.AccountsRowListener;
import com.g2dev.job.custom.sandusky.migration.sugar.Field;
import com.g2dev.job.custom.sandusky.migration.sugar.Get_entries_count_result;
import com.g2dev.job.custom.sandusky.migration.sugar.Get_entry_list_result;
import com.g2dev.map.IntegrationMap;
import com.g2dev.output.ext.SugarOutputProcessorCSV;
import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.New_module_fields;

public abstract class MigrationProcess {

	protected final String moduleName;
	private boolean similarMatch;

	// Accounts | 6744
	// Contacts | 1484
	// Opportunities | 26399
	// Documents | 81
	// Campaigns | 64
	// MSG_Reports | 0
	// MSG_Teams | 6
	// iFrames | 3
	// MSG_Travels | 0
	// MSG_Mileage | 1
	// Calls | 59859
	// Meetings | 15985
	// Tasks | 24
	// Notes | 162
	// Prospects | 0
	// ProspectLists | 0
	// KReports | 0
	// AOW_WorkFlow | 0
	// MSP_Periods | 12
	// MSP_Goals | 0
	// MSP_Products | 3
	// MSP_ProductGroups | 3
	// Administration | 0
	// Currencies | 0
	// EditCustomFields | 0
	// Trackers | 0
	// Employees | 0
	// Releases | 0
	// Users | 40
	// Versions | 2
	// Roles | 0
	// EmailMarketing | 0
	// MergeRecords | 0
	// EmailAddresses | 0
	// EmailText | 0
	// Schedulers | 7
	// CampaignTrackers | 0
	// CampaignLog | 0
	// EmailMan | 0
	// Groups | 0
	// InboundEmail | 0
	// ACLActions | 0
	// ACLRoles | 0
	// DocumentRevisions | 0
	// UserPreferences | 0
	// SavedSearch | 0
	// SugarFeed | 26272
	// EAPM | 0
	// OAuthKeys | 0
	// OAuthTokens | 0
	// AOW_Actions | 0
	// AOW_Processed | 0
	// AOW_Conditions | 0
	// MSP_ProductRevenue | 0
	// SecurityGroups | 0
	public MigrationProcess(String moduleName) {
		this.moduleName = moduleName;
	}

	public IntegrationMap buildMap(String module_name) {
		List<com.sugarcrm.www.sugarcrm.Field> targetFields = new ArrayList<com.sugarcrm.www.sugarcrm.Field>();
		SugarSession sugarSession = SugarSession.getInstance();
		if (sugarSession.loginForSandusky()) {
			try {
				/*Module_fields get_module_fields = sugarSession.getSugar()
						.get_module_fields(sugarSession.getSessionId(),
								module_name);*/
				New_module_fields get_module_fields = sugarSession.getSugar()
						.get_module_fields(sugarSession.getSessionId(),
								module_name,null);
				com.sugarcrm.www.sugarcrm.Field[] module_fields = get_module_fields
						.getModule_fields();
				if (module_fields != null) {
					targetFields.addAll(Arrays.asList(module_fields));
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<Field> sourceFields = new ArrayList<Field>();
		SanduskySugarSession session = SanduskySugarSession.getInstance();
		if (session.loginForSandusky()) {
			try {
				com.g2dev.job.custom.sandusky.migration.sugar.Module_fields get_module_fields = session
						.getSugar().get_module_fields(session.getSessionId(),
								module_name);
				Field[] module_fields = get_module_fields.getModule_fields();
				if (module_fields != null) {
					sourceFields.addAll(Arrays.asList(module_fields));
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return buildMap(sourceFields, targetFields);
	}

	public IntegrationMap buildMap(List<Field> source,
			List<com.sugarcrm.www.sugarcrm.Field> target) {
		return buildMap(source, target, moduleName);
	}

	public abstract void start();

	public void defaultStart() {
		defaultStart(moduleName.toLowerCase() + ".date_modified>20130000");
	}

	public void defaultStart(String query) {

		IntegrationMap map = buildMap(moduleName);
		SugarOutputProcessorCSV output = new SugarOutputProcessorCSV(
				moduleName, map, new AccountsRowListener(new AccountDedupe()));
		SugarSession.getInstance().loginForSandusky();
		SanduskyInput SanduskyInput = new SanduskyInput(moduleName);
		SanduskyInput.setBatchSize(200);
		SanduskyInput.setSelectFields(names(map.getFromToMap().keySet()));
		// yyyy-MM-dd all>58023 date_entered>5148 date_modified>23475
		SanduskyInput.start(query);
		System.out.println("Number of records "
				+ SanduskyInput.getRecordsCount());
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
					* SanduskyInput.getRecordsCount()
					/ SanduskyInput.getOffset();
			System.out.println("ETA: " + (remaining));

			// timeMillis = System.currentTimeMillis();
		}

	}

	public IntegrationMap buildMap(List<Field> source,
			List<com.sugarcrm.www.sugarcrm.Field> target, String moduleName) {
		IntegrationMap im = new IntegrationMap();
		System.out.println("Target size " + target.size());
		System.out.println("Source size " + source.size());

		Schema sourceSchema = new Schema();
		DbObject sourceObject = new DbObject(sourceSchema);
		sourceObject.setName(moduleName);
		sourceSchema.getObjects().add(sourceObject);

		Schema targetSchema = new Schema();
		DbObject targetObject = new DbObject(targetSchema);
		targetObject.setName(moduleName);
		targetSchema.getObjects().add(targetObject);

		List<String> noMatch = new ArrayList<String>();
		List<String> matchedList = new ArrayList<String>();
		im.getFrom().add(sourceSchema);
		im.setTo(targetSchema);
		int c = 0;
		for (Field sourceField : source) {
			boolean matched = false;
			for (com.sugarcrm.www.sugarcrm.Field targetField : target) {
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

		if (similarMatch) {
			// similar
		}

		return im;
	}

	public String getModuleName() {
		return moduleName;
	}

	public static Collection<String> names(Set<DbField> keySet) {
		List<String> list = new ArrayList<String>();
		for (DbField dbField : keySet) {
			list.add(dbField.getName());
		}
		return list;
	}

	public static void main(String[] args) {
		SanduskySugarSession session = SanduskySugarSession.getInstance();
		session.loginForSandusky();
		String[] moduleNames = null;
		;
		try {
			moduleNames = session.getSugar()
					.get_available_modules(session.getSessionId()).getModules();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (String moduleName : moduleNames) {
			String query = moduleName.toLowerCase() + ".date_modified>20130000";
			try {
				Get_entries_count_result get_entries_count = session.getSugar()
						.get_entries_count(session.getSessionId(), moduleName,
								query, 0);
				System.out.println(moduleName + " | "
						+ get_entries_count.getResult_count());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
