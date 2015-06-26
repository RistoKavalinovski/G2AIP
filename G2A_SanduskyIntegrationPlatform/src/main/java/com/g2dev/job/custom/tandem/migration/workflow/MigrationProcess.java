package com.g2dev.job.custom.tandem.migration.workflow;

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
import com.g2dev.job.custom.tandem.migration.TandemInput;
import com.g2dev.job.custom.tandem.migration.TandemSugarSession;
import com.g2dev.job.custom.tandem.migration.rowListeners.AccountsRowListener;
import com.g2dev.job.custom.tandem.migration.sugar.Field;
import com.g2dev.job.custom.tandem.migration.sugar.Get_entry_list_result_version2;
import com.g2dev.job.custom.tandem.migration.sugar.New_module_fields;
import com.g2dev.map.IntegrationMap;
import com.g2dev.output.ext.SugarOutputProcessorCSV;
import com.g2dev.sugar.connect.SugarSession;

public abstract class MigrationProcess {

	protected final String moduleName;
	private boolean similarMatch;

	// Campaigns: 19
	// Users: 81
	// EmailMarketing: 0
	// Emails: 5
	// InboundEmail: 0
	// Meetings: 15580
	// SecurityGroups: 0
	// AOW_Actions: 0
	// la_LoginAudit: 13458
	// Tasks: 428
	// ACLRoles: 0
	// OAuthKeys: 0
	// UserPreferences: 0
	// SavedSearch: 0
	// MSR_Revenue: 4058
	// MergeRecords: 0
	// MSG_Teams: 5
	// MSP_ProductGroups: 4
	// Prospects: 0
	// DocumentRevisions: 0
	// EmailMan: 0
	// EmailTemplates: 0
	// Versions: 2
	// MSP_Periods: 12
	// Employees: 0
	// CampaignTrackers: 0
	// Calls: 19361
	// SugarFeed: 47371
	// AOW_Processed: 0
	// Notes: 394
	// EAPM: 0
	// Documents: 108
	// OAuthTokens: 0
	// iFrames: 4
	// EmailAddresses: 0
	// AOW_Conditions: 0
	// Groups: 0
	// AOW_WorkFlow: 0
	// MSG_Reports: 0
	// Currencies: 0
	// KReports: 0
	// Releases: 0
	// Administration: 0
	// ACLActions: 0
	// Contacts: 4
	// EditCustomFields: 0
	// Roles: 0
	// Opportunities: 18287
	// EmailText: 0
	// Accounts: 23496
	// CampaignLog: 0
	// Trackers: 0
	// ProspectLists: 0
	// MSP_Products: 4
	// MSG_Mileage: 0
	// MSP_ProductRevenue: 0
	// Schedulers: 7
	// Leads: 36
	// MSP_Goals: 0
	public MigrationProcess(String moduleName) {
		this.moduleName = moduleName;
	}

	public IntegrationMap buildMap(String module_name) {
		List<com.sugarcrm.www.sugarcrm.Field> targetFields = new ArrayList<com.sugarcrm.www.sugarcrm.Field>();
		SugarSession sugarSession = SugarSession.getInstance();
		if (sugarSession.loginForSandusky()) {
			try {
				/*
				 * Module_fields get_module_fields = sugarSession.getSugar()
				 * .get_module_fields(sugarSession.getSessionId(), module_name);
				 */
				com.sugarcrm.www.sugarcrm.New_module_fields get_module_fields = sugarSession
						.getSugar().get_module_fields(
								sugarSession.getSessionId(), module_name, null);
				com.sugarcrm.www.sugarcrm.Field[] module_fields = get_module_fields
						.getModule_fields();
				if (module_fields != null) {
					targetFields.addAll(Arrays.asList(module_fields));
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		List<Field> sourceFields = new ArrayList<Field>();
		TandemSugarSession tandemSugarSession = TandemSugarSession.getInstance();
		if (tandemSugarSession.loginForTandem()) {
			try {
				New_module_fields get_module_fields = tandemSugarSession
						.getSugar().get_module_fields(
								tandemSugarSession.getSessionId(), module_name,
								null);
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
		TandemInput tandemInput = new TandemInput(moduleName);
		tandemInput.setBatchSize(200);
		tandemInput.setSelectFields(names(map.getFromToMap().keySet()));
		// yyyy-MM-dd all>58023 date_entered>5148 date_modified>23475
		tandemInput.start(query);
		System.out.println("Number of records " + tandemInput.getRecordsCount());
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
			}
		}
		System.out.println("fields matched " + c);
		System.out.println("Fields failed to match: " + noMatch);

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

}
