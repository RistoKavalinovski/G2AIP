package com.g2dev.job.custom.ogden.migration.workflow;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.g2dev.connect.DbField;
import com.g2dev.job.custom.AccountDedupe;
import com.g2dev.job.custom.SugarInput;
import com.g2dev.job.custom.ogden.migration.OgdenInput;
import com.g2dev.job.custom.ogden.migration.OgdenSugarSession;
import com.g2dev.job.custom.ogden.migration.rowListeners.AccountsRowListener;
import com.g2dev.job.custom.ogden.migration.sugar.Entry_value;
import com.g2dev.job.custom.ogden.migration.sugar.Get_entry_result_version2;
import com.g2dev.job.custom.ogden.migration.sugar.Link_field;
import com.g2dev.job.custom.ogden.migration.sugar.Module_list_entry;
import com.g2dev.job.custom.ogden.migration.sugar.SugarsoapPortType;
import com.g2dev.map.IntegrationMap;
import com.g2dev.output.ext.SugarOutputProcessorCSV;
import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.Name_value;

public class SetRelationship {

	private String moduleName;
	private String relatedModuleName;
	private String relationshipName;
	private int requestsPerSecond = 2;
	private RelationshipUpdateListener listener;

	public SetRelationship(String moduleName, String relatedModuleName,
			String relationshipName, int requestsPerSecond,
			RelationshipUpdateListener listener) {
		super();
		this.moduleName = moduleName;
		this.relatedModuleName = relatedModuleName;
		this.relationshipName = relationshipName;
		this.requestsPerSecond = requestsPerSecond;
		this.listener = listener;
	}

	public static void main(String[] args) {

		OgdenSugarSession.getInstance().loginForOgden();
		try {
			Module_list_entry[] modules = OgdenSugarSession
					.getInstance()
					.getSugar()
					.get_available_modules(
							OgdenSugarSession.getInstance().getSessionId(),
							null).getModules();
			for (Module_list_entry module_list_entry : modules) {
				Link_field[] moduleRelationshipFields = OgdenSugarSession
						.getInstance().getModuleRelationshipFields(
								module_list_entry.getModule_key());
				System.out.println("------------------------------------");
				System.out.println(module_list_entry.getModule_key());
				System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
				if (moduleRelationshipFields != null) {
					for (Link_field link_field : moduleRelationshipFields) {
						System.out.println(link_field.getName());
					}
				}
			}
		} catch (RemoteException e) {
			// TODO Auto^generated catch block
			e.printStackTrace();
		}

		// String relationshipName = "accounts";
		// String[] selectFields = { "id" };
		// OgdenSugarSession ogdenSession = OgdenSugarSession.getInstance();
		// ogdenSession.loginForOgden();
		// SugarsoapPortType ogdenClient = ogdenSession.getSugar();
		// // SugarSession.getInstance().get
		// Link_field[] moduleRelationshipFields =
		// OgdenSugarSession.getInstance()
		// .getModuleRelationshipFields("Meetings");
		// for (Link_field link_field : moduleRelationshipFields) {
		// System.out.println(link_field.getName());
		// }
		// try {
		// Get_entry_result_version2 get_relationships2 = ogdenClient
		// .get_relationships(ogdenSession.getSessionId(), "Meetings",
		// "1001039d-46a2-b9ec-857f-4f594084aee3",
		// relationshipName, null, selectFields, null, 0,
		// null, 0, Integer.MAX_VALUE);
		// Get_entry_result_version2 get_relationships = ogdenClient
		// .get_relationships(ogdenSession.getSessionId(), "Meetings",
		// "1001039d-46a2-b9ec-857f-4f594084aee3",
		// relationshipName, null, selectFields, null, 0,
		// null, 0, Integer.MAX_VALUE);
		// System.out.println(get_relationships);
		// } catch (RemoteException e) {
		// e.printStackTrace();
		// }

	}

	public void start(final List<String> ids) {
		OgdenSugarSession.getInstance().loginForOgden();
		SugarSession.getInstance().loginForSandusky();
		int requests = 0;
		for (int i = 0; i < ids.size(); i++) {
			final int index = i;
			new Thread(new Runnable() {
				public void run() {
					setRelationship(ids.get(index));
				}
			}).start();
			requests++;
			if (requests >= requestsPerSecond) {
				try {
					Thread.sleep(1000);
					requests = 0;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static String[] selectFields = { "id" };

	private void setRelationship(String id) {
		OgdenSugarSession ogdenSession = OgdenSugarSession.getInstance();
		SugarsoapPortType clientOgden = ogdenSession.getSugar();

		try {
			Entry_value[] entry_list = clientOgden.get_relationships(
					ogdenSession.getSessionId(), moduleName, id,
					relationshipName, null, selectFields, null, 0, null, 0,
					Integer.MAX_VALUE).getEntry_list();
			if (entry_list != null && entry_list.length > 0) {
				String[] related_ids = new String[entry_list.length];
				Name_value[] name_value_list = new Name_value[related_ids.length];
				for (int i = 0; i < entry_list.length; i++) {
					related_ids[i] = entry_list[i].getId();
					name_value_list[i] = new Name_value("id", related_ids[i]);
				}
				SugarSession sugarSession = SugarSession.getInstance();
				com.sugarcrm.www.sugarcrm.SugarsoapPortType client = sugarSession
						.getSugar();
				if (listener != null) {
					synchronized (listener) {
						listener.beforeUpdate(id, Arrays.asList(related_ids));
					}
				}
				try {
					client.set_relationship(sugarSession.getSessionId(),
							moduleName, id, relationshipName, related_ids,
							name_value_list, 0);
				} catch (Exception e) {
					if (exceptionContainsString("Invalid Session ID", e)) {
						SugarSession.getInstance().setSessionId(null);
						sugarSession.loginForSandusky();
						setRelationship(id);
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private boolean exceptionContainsString(String s, Throwable t) {
		if (t != null) {
			if (t.getLocalizedMessage().contains(s)
					|| t.getMessage().contains(s)) {
				return true;
			} else {
				return exceptionContainsString(s, t.getCause());
			}
		}
		return false;
	}

	MigrationProcess migrationProcess = null;
	IntegrationMap map = null;

	public void migrateRelatedRecord(final String id) {
		migrationProcess = new MigrationProcess(relatedModuleName) {

			@Override
			public void start() {
				synchronized (map) {
					if (map == null) {
						map = migrationProcess.buildMap(relatedModuleName);
					}
				}
				SugarOutputProcessorCSV output = new SugarOutputProcessorCSV(
						relatedModuleName, map, new AccountsRowListener(
								new AccountDedupe()));
				Collection<String> selectedFields = names(map.getFromToMap()
						.keySet());
				Get_entry_result_version2 get_entry;
				try {
					get_entry = OgdenSugarSession
							.getInstance()
							.getSugar()
							.get_entry(
									OgdenSugarSession.getInstance()
											.getSessionId(),
									relatedModuleName,
									id,
									selectedFields
											.toArray(new String[selectedFields
													.size()]), null, false);

					output.process(SugarInput
							.resultToBatch(get_entry, new ArrayList<DbField>(
									map.getFromToMap().keySet())));
					System.out.println("migrated " + relatedModuleName + " "
							+ id);
				} catch (RemoteException e) {
					e.printStackTrace();
				}

			}
		};
		migrationProcess.start();
	}

	public interface RelationshipUpdateListener {
		void beforeUpdate(String id, List<String> relatedIds);
	}

}
