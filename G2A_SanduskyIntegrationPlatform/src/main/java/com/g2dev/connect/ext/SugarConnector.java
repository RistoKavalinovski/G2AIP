package com.g2dev.connect.ext;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.g2dev.connect.Connector;
import com.g2dev.connect.DbField;
import com.g2dev.connect.DbObject;
import com.g2dev.connect.Schema;
import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.Field;
import com.sugarcrm.www.sugarcrm.Module_list;
import com.sugarcrm.www.sugarcrm.Module_list_entry;
import com.sugarcrm.www.sugarcrm.New_module_fields;

public class SugarConnector extends Connector {

	public com.g2dev.connect.Schema getSchema() {
		if (super.getSchema() == null) {
			if (selectedModules.isEmpty()) {
				generateSchema();
			} else {
				generateSchema(selectedModules);
			}
		}
		return super.getSchema();
	};

	private List<String> availableModules;
	private List<String> selectedModules = new ArrayList<String>();

	public List<String> getAvailableModules() {
		if (availableModules == null) {

			SugarSession session = SugarSession.getInstance();
			if (session.loginForSandusky()) {
				try {
					Module_list get_available_modules = session
							.getSugar()
							.get_available_modules(session.getSessionId(), null);

					/*
					 * if (get_available_modules.getError() == null ||
					 * get_available_modules.getError().getNumber()
					 * .equals("0")) {
					 */
					availableModules = new ArrayList<String>();
					Module_list_entry[] modules2 = get_available_modules
							.getModules();
					for (Module_list_entry module_list_entry : modules2) {
						availableModules.add(module_list_entry.getModule_key());
					}
					// String[] modules = get_available_modules.getModules();
					// availableModules = Arrays.asList(modules);

					/* } */
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return availableModules;
	}

	public void generateSchema() {
		SugarSession session = SugarSession.getInstance();
		if (session.loginForSandusky()) {
			try {
				generateSchema(getAvailableModules());
				/*
				 * Module_list get_available_modules = session.getSugar()
				 * .get_available_modules(session.getSessionId()); if
				 * (get_available_modules.getError() == null ||
				 * get_available_modules.getError().getNumber() .equals("0")) {
				 * String[] modules = get_available_modules.getModules();
				 * generateSchema(Arrays.asList(modules));
				 * 
				 * }
				 */} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void generateSchema(List<String> modules) {
		Schema schema = new Schema();
		SugarSession session = SugarSession.getInstance();
		if (session.loginForSandusky()) {
			for (String module_name : modules) {
				DbObject object = new DbObject(schema);
				object.setName(module_name);
				List<DbField> fields = new ArrayList<DbField>();
				New_module_fields get_module_fields;
				try {
					get_module_fields = session.getSugar().get_module_fields(
							session.getSessionId(), module_name,null);
					/*
					 * if (get_module_fields.getError() == null ||
					 * get_module_fields.getError().getNumber() == null ||
					 * get_module_fields.getError().getNumber() .equals("0")) {
					 */
						Field[] module_fields = get_module_fields
								.getModule_fields();
						for (Field module_field : module_fields) {
							DbField field = new DbField(object);
							field.setName(module_field.getName());
							field.setType(module_field.getType());
							fields.add(field);
						}
					/*}*/
					object.setFields(fields);
					schema.getObjects().add(object);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		setSchema(schema);
	}

	public static void main(String[] args) {
		SugarConnector sugarConnector = new SugarConnector();
		sugarConnector.getSelectedModules().add("Comments");
		sugarConnector.getSelectedModules().add("Expressions");
		sugarConnector.getSelectedModules().add("Contacts");
		sugarConnector.getSelectedModules().add("Opportunities");
		Schema schema2 = sugarConnector.getSchema();
		List<DbObject> objects = schema2.getObjects();
		for (DbObject dbObject : objects) {
			System.out.println(dbObject.getName());
			List<DbField> fields = dbObject.getFields();
			for (DbField field : fields) {
				System.out.println("\t" + field.getName());
			}
		}

		// Calls
		// Meetings
		// Tasks
		// Notes
		// Reports
		// Leads
		// Contacts
		// Accounts
		// Opportunities
		// Emails
		// Campaigns
		// Prospects
		// ProspectLists
		// Quotes
		// Products
		// Forecasts
		// Contracts
		// KBDocuments
		// RevenueLineItems
		// Documents
		// Cases
		// Project
		// Bugs
		// Administration
		// Currencies
		// EditCustomFields
		// Trackers
		// Employees
		// Manufacturers
		// ProductBundles
		// ProductBundleNotes
		// ProductCategories
		// ProductTemplates
		// ProductTypes
		// Shippers
		// TaxRates
		// TeamNotices
		// Teams
		// TimePeriods
		// ForecastOpportunities
		// Quotas
		// KBDocumentRevisions
		// KBDocumentKBTags
		// KBTags
		// KBContents
		// ContractTypes
		// ForecastSchedule
		// ACLFields
		// Holidays
		// ForecastDirectReports
		// System
		// Releases
		// Users
		// Versions
		// Roles
		// EmailMarketing
		// TeamMemberships
		// TeamSets
		// MergeRecords
		// EmailAddresses
		// EmailText
		// Schedulers
		// EmailTemplates
		// CampaignTrackers
		// CampaignLog
		// EmailMan
		// Groups
		// InboundEmail
		// ACLActions
		// ACLRoles
		// DocumentRevisions
		// Empty
		// ProjectTask
		// CustomQueries
		// DataSets
		// DataSet_Attribute
		// ReportMaker
		// WorkFlow
		// WorkFlowTriggerShells
		// WorkFlowAlertShells
		// WorkFlowAlerts
		// WorkFlowActionShells
		// WorkFlowActions
		// Expressions
		// UserPreferences
		// SavedSearch
		// Styleguide
		// Notifications
		// EAPM
		// OAuthKeys
		// OAuthTokens
		// SugarFavorites
		// WebLogicHooks
		// Activities
		// Comments
		// Subscriptions
		// Filters
		// Dashboards
		// PdfManager
		// HealthCheck
	}

	public List<String> getSelectedModules() {
		return selectedModules;
	}

	public void setSelectedModules(List<String> selectedModules) {
		this.selectedModules = selectedModules;
	}

}
