package com.g2dev.job.custom.tandem.migration.workflow;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.g2dev.job.custom.tandem.migration.TandemSugarSession;
import com.g2dev.job.custom.tandem.migration.sugar.Entry_value;
import com.g2dev.job.custom.tandem.migration.sugar.Get_entry_result_version2;
import com.g2dev.job.custom.tandem.migration.sugar.Link_field;
import com.g2dev.job.custom.tandem.migration.sugar.Link_name_to_fields_array;
import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.New_set_relationship_list_result;

public class MigrateMainActionStarter {

	public static void main(String[] args) {
		// P_01_MigrateUsers.main(null);

		// String[] modules = { "Campaigns" };
		// P_99_MigrateDefaults.main(modules);

		// P_05_MigrateLeads.main(null);
		// P_02_MigrateAccounts.main(null);
		// offset 13800
		// P_04_MigrateOpportunities.main(null);
		// TandemSugarSession.getInstance().printRecordCounts(".date_modified>20130000");
		// P_03_MigrateContacts.main(null);

		// String[] modules1 = { "Emails", "Meetings", "Calls", "Notes",
		// "Documents", "Schedulers", "Tasks" };
		// P_99_MigrateDefaults.main(modules1);

		// test1();
		// test2();

		migrateMultiThread();

		// updateRelationshipsAllModules(true);
	}

	private static void migrateMultiThread() {
		try {
			new Thread(new Runnable() {

				public void run() {
					String[] modules1 = { "Emails", };
					P_99_MigrateDefaults.main(modules1);
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			new Thread(new Runnable() {

				public void run() {
					String[] modules1 = { "Meetings", };
					P_99_MigrateDefaults.main(modules1);
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			new Thread(new Runnable() {

				public void run() {
					String[] modules1 = { "Calls", };
					P_99_MigrateDefaults.main(modules1);
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			new Thread(new Runnable() {

				public void run() {
					String[] modules1 = { "Notes", };
					P_99_MigrateDefaults.main(modules1);
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			new Thread(new Runnable() {

				public void run() {
					String[] modules1 = { "Documents", };
					P_99_MigrateDefaults.main(modules1);
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			new Thread(new Runnable() {

				public void run() {
					String[] modules1 = { "Schedulers", };
					P_99_MigrateDefaults.main(modules1);
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			new Thread(new Runnable() {

				public void run() {
					String[] modules1 = { "Tasks" };
					P_99_MigrateDefaults.main(modules1);
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void updateRelationshipsAllModules(boolean multithread) {
		final List<String> moduleNames = new ArrayList<String>();
		// moduleNames.add("Users");
		// moduleNames.add("Campaigns");
		// moduleNames.add("Leads");
		// moduleNames.add("Accounts");
		// moduleNames.add("Opportunities");
		// moduleNames.add("Contacts");
		moduleNames.add("Emails");
		moduleNames.add("Meetings");
		moduleNames.add("Calls");
		moduleNames.add("Notes");
		moduleNames.add("Documents");
		moduleNames.add("Schedulers");
		moduleNames.add("Tasks");
		for (final String moduleName : moduleNames) {
			if (multithread) {
				new Thread(new Runnable() {

					public void run() {
						new UpdateRelationships(moduleNames, moduleName)
								.start();
					}
				}).start();
			} else {
				new UpdateRelationships(moduleNames, moduleName).start();
			}
		}
	}

	private static void test2() {
		TandemSugarSession session = TandemSugarSession.getInstance();
		session.loginForTandem();
		com.g2dev.job.custom.tandem.migration.sugar.SugarsoapPortType client = session
				.getSugar();
		// try {
		// Get_relationships_result get_relationships = client
		// .get_relationships(session.getSessionId(), "Accounts",
		// "9e1a36515d6704d7eb7a30d783400e5d", "Accounts",
		// null, 0);
		// Id_mod[] ids = get_relationships.getIds();
		// for (Id_mod id_mod : ids) {
		// System.out.println(id_mod.getId());
		// }
		// } catch (RemoteException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public static class UpdateRelationships {
		private List<String> moduleNames;

		public UpdateRelationships(List<String> moduleNames, String moduleName) {
			super();
			this.moduleNames = moduleNames;
			this.moduleName = moduleName;
		}

		private String moduleName;
		private RelationshipBatch rb = new RelationshipBatch();

		int offset = 0;

		public void start() {
			start(null);
		}
		public void start(final String query) {
			final SugarSession sugarSession = SugarSession.getInstance();
			sugarSession.loginForSandusky();

			try {
				final int size = sugarSession
						.getSugar()
						.get_entries_count(sugarSession.getSessionId(),
								getModuleName(), query, 0).getResult_count();

				final String[] select_fields = { "id" };
				final int batchSize = 10;

				while (offset == 0 || (offset + batchSize) <= size) {
					new Thread(new Runnable() {

						public void run() {
							splitJob(offset);

						}

						private void splitJob(int offset) {

							System.out.println("Updating relationships on '"
									+ getModuleName() + "' " + offset + " / "
									+ size);
							/*
							 * Entry_value[] entry_list = sugarSession
							 * .getSugar()
							 * .get_entry_list(sugarSession.getSessionId(),
							 * getModuleName(), null, null, offset,
							 * select_fields, batchSize, 0) .getEntry_list();
							 */
							com.sugarcrm.www.sugarcrm.Entry_value[] entry_list;
							try {
								entry_list = sugarSession
										.getSugar()
										.get_entry_list(
												sugarSession.getSessionId(),
												getModuleName(), query, null,
												offset, select_fields, null,
												batchSize, 0, false)
										.getEntry_list();
								TandemSugarSession.getInstance()
										.loginForTandem();
								for (int i = 0; i < entry_list.length; i++) {
									// addRelationshipRecord(entry_list[i].getId());
									pushRelationshipRecord(entry_list[i]
											.getId());
								}
								pushRelBatchToServer();
								/*
								 * for (com.sugarcrm.www.sugarcrm.Entry_value
								 * entry_value : entry_list) {
								 * 
								 * }
								 */
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}).start();
					offset += batchSize;
				}
				System.out.println("Updating relationships on '"
						+ getModuleName() + "' " + offset + " / " + size);
				System.out.println("done");

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void pushRelBatchToServer() {
			rb.module_names.addAll(getModuleNames());
			SugarSession session = SugarSession.getInstance();
			session.loginForSandusky();
			try {
				New_set_relationship_list_result set_relationships = session
						.getSugar().set_relationships(session.getSessionId(),
								rb.module_names(), rb.module_ids(),
								rb.link_field_names(), rb.related_ids(), null,
								rb.delete_array());
				System.out.println("__ relationship batch loaded. size > "
						+ (set_relationships.getCreated() + set_relationships
								.getFailed()) + "_____________________");
				System.out.println("Relationships created > "
						+ set_relationships.getCreated());
				System.out.println("Relationships deleted > "
						+ set_relationships.getDeleted());
				System.out.println("Relationships failed > "
						+ set_relationships.getFailed());
				System.out
						.println("<________________________________________________________");

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rb = new RelationshipBatch();

		}

		private void addRelationshipRecord(String id) {

			// TandemSugarSession.getInstance().printModuleRelationshipFields("Users");
			TandemSugarSession session = TandemSugarSession.getInstance();
			session.loginForTandem();
			String module_name = getModuleName();
			String module_id = id;
			String related_module_query = "";
			int deleted = 0;
			int limit = 500;
			String order_by = "";
			Link_name_to_fields_array[] related_module_link_name_to_fields_array = null;
			Link_field[] printModuleRelationshipFields = TandemSugarSession
					.getInstance().printModuleRelationshipFields(module_name);
			for (Link_field link_field : printModuleRelationshipFields) {
				System.out.println(link_field.getBean_name());
				String link_field_name = link_field.getName();
				String[] related_fields = { "id" };
				int offset = 0;
				List<String> relIds = new ArrayList<String>();
				while (true) {
					Get_entry_result_version2 get_relationships = new Get_entry_result_version2();
					get_relationships.setEntry_list(new Entry_value[0]);
					try {
						get_relationships = session
								.getSugar()
								.get_relationships(
										session.getSessionId(),
										module_name,
										module_id,
										link_field_name,
										related_module_query,
										related_fields,
										related_module_link_name_to_fields_array,
										deleted, order_by, offset, limit);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Entry_value[] entry_list = get_relationships
							.getEntry_list();
					for (Entry_value entry_value : entry_list) {
						relIds.add(entry_value.getId());
						// Name_value[] name_value_list = entry_value
						// .getName_value_list();
						// for (Name_value name_value : name_value_list) {
						// hasRels = true;
						// System.out.print(name_value.getName() + " / "
						// + name_value.getValue() + " | ");
						// }
						// System.out.println();
					}

					System.out.println(entry_list.length);
					if (entry_list.length <= 1) {
						break;

					}
					offset += limit;
				}
				if (!relIds.isEmpty()) {
					rb.module_ids.add(id);
					rb.link_field_names.add(link_field_name);
					rb.related_ids.add(relIds);
				}
			}

		}

		private void pushRelationshipRecord(String id) {

			// TandemSugarSession.getInstance().printModuleRelationshipFields("Users");
			TandemSugarSession session = TandemSugarSession.getInstance();
			session.loginForTandem();
			String module_name = getModuleName();
			String module_id = id;
			String related_module_query = "";
			int deleted = 0;
			int limit = 500;
			String order_by = "";
			Link_name_to_fields_array[] related_module_link_name_to_fields_array = null;
			Link_field[] printModuleRelationshipFields = TandemSugarSession
					.getInstance().getModuleRelationshipFields(module_name);
			for (Link_field link_field : printModuleRelationshipFields) {
				System.out.println(link_field.getBean_name());
				String link_field_name = link_field.getName();
				String[] related_fields = { "id" };
				int offset = 0;
				List<String> relIds = new ArrayList<String>();
				while (true) {
					Get_entry_result_version2 get_relationships = new Get_entry_result_version2();
					get_relationships.setEntry_list(new Entry_value[0]);
					try {
						get_relationships = session
								.getSugar()
								.get_relationships(
										session.getSessionId(),
										module_name,
										module_id,
										link_field_name,
										related_module_query,
										related_fields,
										related_module_link_name_to_fields_array,
										deleted, order_by, offset, limit);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Entry_value[] entry_list = get_relationships
							.getEntry_list();
					for (Entry_value entry_value : entry_list) {
						relIds.add(entry_value.getId());
						// Name_value[] name_value_list = entry_value
						// .getName_value_list();
						// for (Name_value name_value : name_value_list) {
						// hasRels = true;
						// System.out.print(name_value.getName() + " / "
						// + name_value.getValue() + " | ");
						// }
						// System.out.println();
					}

					if (!relIds.isEmpty()) {
						pushRelRecord(id, link_field_name, relIds);
					}

					// System.out.println(entry_list.length);
					if (entry_list.length <= 1) {
						break;

					}
					offset += limit;
				}

			}

		}

		private void pushRelRecord(String id, String link_field_name,
				List<String> relIds) {
			SugarSession session = SugarSession.getInstance();
			session.loginForSandusky();
			try {
				New_set_relationship_list_result set_relationship = session
						.getSugar().set_relationship(session.getSessionId(),
								getModuleName(), id, link_field_name,
								relIds.toArray(new String[relIds.size()]),
								null, 0);
				System.out.println("__ relationship batch loaded. size > "
						+ (set_relationship.getCreated() + set_relationship
								.getFailed()) + "_____________________");
				System.out.println("Relationships created > "
						+ set_relationship.getCreated());
				System.out.println("Relationships deleted > "
						+ set_relationship.getDeleted());
				System.out.println("Relationships failed > "
						+ set_relationship.getFailed());
				System.out.println(id + " <> " + relIds);
				System.out
						.println("<________________________________________________________");

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private void updateRelationships1(String id) {/*
													 * TandemSugarSession
													 * tandemSugarSession =
													 * TandemSugarSession
													 * .getInstance(); for
													 * (String relatedModule :
													 * getModuleNames()) { try {
													 * Get_entry_result_version2
													 * get_relationships2 =
													 * tandemSugarSession
													 * .getSugar
													 * ().get_relationships
													 * (tandemSugarSession
													 * .getSessionId(),
													 * getModuleName(), id,
													 * relatedModule, null,
													 * null, null, 0, null, 0,
													 * Integer.MAX_VALUE);
													 * Get_relationships_result
													 * get_relationships =
													 * tandemSugarSession
													 * .getSugar()
													 * .get_relationships(
													 * tandemSugarSession
													 * .getSessionId(),
													 * getModuleName(), id,
													 * relatedModule, null, 0);
													 * get_relationships2
													 * .getRelationship_list
													 * ()[0]
													 * .getLink_list()[0].getRecords
													 * ()[0].getLink_value()
													 * Id_mod[] ids =
													 * get_relationships
													 * .getIds(); if (ids !=
													 * null && ids.length > 0) {
													 * Set_relationship_value[]
													 * set_relationship_list =
													 * new
													 * Set_relationship_value
													 * [ids.length]; for (int i
													 * = 0; i < ids.length; i++)
													 * {
													 * set_relationship_list[i]
													 * = new
													 * Set_relationship_value(
													 * getModuleName(), id,
													 * relatedModule,
													 * ids[i].getId()); }
													 * 
													 * try { SugarSession
													 * .getInstance()
													 * .getSugar()
													 * .set_relationships(
													 * SugarSession
													 * .getInstance()
													 * .getSessionId(),
													 * set_relationship_list);
													 * System.out .println(
													 * "Updated relationships for Related module: '"
													 * + relatedModule +
													 * "' on '" +
													 * getModuleName() +
													 * "' with record id " +
													 * id); } catch (Exception
													 * e) { System.out .println(
													 * "exception durring update relationships on module '"
													 * + getModuleName() +
													 * "' with related '" +
													 * relatedModule + " " +
													 * Arrays.asList(ids)); } }
													 * 
													 * } catch (RemoteException
													 * ) * e) { // TODO
													 * Auto-genera)ted catch
													 * block
													 * e.printStackTrace(); } }
													 */
		}

		public List<String> getModuleNames() {
			return moduleNames;
		}

		public void setModuleNames(List<String> moduleNames) {
			this.moduleNames = moduleNames;
		}

		public String getModuleName() {
			return moduleName;
		}

		public void setModuleName(String moduleName) {
			this.moduleName = moduleName;
		}
	}

	public static class RelationshipBatch {

		public List<String> module_ids = new ArrayList<String>();
		public List<String> link_field_names = new ArrayList<String>();
		public List<List<String>> related_ids = new ArrayList<List<String>>();
		public List<String> module_names = new ArrayList<String>();

		public String[] module_ids() {
			return module_ids.toArray(new String[module_ids.size()]);
		}

		public String[] module_names() {
			return module_names.toArray(new String[module_names.size()]);
		}

		private static String[][] toMatrix(List<List<String>> targetBatch) {
			String[][] matrix = new String[targetBatch.size()][];
			for (int i = 0; i < targetBatch.size(); i++) {
				List<String> list = targetBatch.get(i);
				matrix[i] = new String[list.size()];
				for (int j = 0; j < list.size(); j++) {
					matrix[i][j] = list.get(j);
				}
			}
			return matrix;
		}

		public int[] delete_array() {
			int size = 0;
			String[][] related_ids = related_ids();
			for (int i = 0; i < related_ids.length; i++) {
				String[] strings = related_ids[i];
				for (int j = 0; j < strings.length; j++) {
					size++;
				}
			}
			size += module_ids.size();
			int[] delete_array = new int[size];
			for (int i = 0; i < delete_array.length; i++) {
				delete_array[i] = 0;
			}

			return delete_array;
		}

		public String[][] related_ids() {
			return toMatrix(related_ids);
		}

		public String[] link_field_names() {
			return link_field_names
					.toArray(new String[link_field_names.size()]);
		}
	}

	private static void test1() {/*
								 * SugarSession session =
								 * SugarSession.getInstance();
								 * session.loginForSandusky(); String[]
								 * select_fields = { "id" }; try { Entry_value[]
								 * entry_list = session .getSugar()
								 * .get_entry_list(session.getSessionId(),
								 * "Leads", null, null, 0, select_fields, 8000,
								 * 0).getEntry_list(); for (Entry_value
								 * entry_value : entry_list) {
								 * System.out.println(entry_value.getId()); } }
								 * catch (RemoteException e) { // TODO
								 * Auto-generated catch block
								 * e.printStackTrace(); }
								 */
	}

}
