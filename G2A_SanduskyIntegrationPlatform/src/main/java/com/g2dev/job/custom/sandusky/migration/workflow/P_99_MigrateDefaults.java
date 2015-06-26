package com.g2dev.job.custom.sandusky.migration.workflow;


public class P_99_MigrateDefaults extends MigrationProcess {

	public P_99_MigrateDefaults(String moduleName) {
		super(moduleName);
	}

	/**
	 * 
	 * @param modules CREATE OLDID FIELD BEFORE RUNNING THIS
	 */
	public static void main(String[] modules) {

		// TODO CREATE OLD ID FIELD BEFORE RUNNING THE JOB
		
		for (String module : modules) {
			new P_99_MigrateDefaults(module).start();
		}
		// String[] modules = { "Schedulers" };
		// for (String string : modules) {
		// new P_99_MigrateDefaults(string).buildMap(string);
		// }

	}

	@Override
	public void start() {
		defaultStart();

	}

}
