package com.g2dev.job.custom;

import java.util.ArrayList;
import java.util.List;

import com.g2dev.connect.DbField;
import com.g2dev.input.Input;
import com.g2dev.job.custom.ogden.migration.sugar.Entry_value;
import com.g2dev.job.custom.ogden.migration.sugar.Get_entry_list_result_version2;
import com.g2dev.job.custom.ogden.migration.sugar.Get_entry_result_version2;
import com.g2dev.job.custom.ogden.migration.sugar.Name_value;

public abstract class SugarInput extends Input {

	private int batchSize = 500;
	private int recordsCount = 0;
	private int offset = 0;

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public int getRecordsCount() {
		return recordsCount;
	}

	public void setRecordsCount(int recordsCount) {
		this.recordsCount = recordsCount;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public static List<String[]> resultToBatch(
			com.g2dev.job.custom.sandusky.migration.sugar.Get_entry_list_result result,
			List<DbField> fields) {
		if (result.getError() == null || result.getError().getNumber() == null
				|| result.getError().getNumber().equals("0")) {
			com.g2dev.job.custom.sandusky.migration.sugar.Entry_value[] entry_list = result
					.getEntry_list();
			List<String[]> batch = new ArrayList<String[]>();
			int lenght = findLenght(fields);
			for (com.g2dev.job.custom.sandusky.migration.sugar.Entry_value entry_value : entry_list) {
				com.g2dev.job.custom.sandusky.migration.sugar.Name_value[] name_values = entry_value
						.getName_value_list();
				String[] row = new String[lenght];
				for (com.g2dev.job.custom.sandusky.migration.sugar.Name_value name_value : name_values) {
					DbField matchField = findField(fields, name_value.getName());
					if (matchField != null) {
						row[matchField.getIndex()] = name_value.getValue();
					}
				}
				batch.add(clearRow(row));
			}

			return batch;
		}
		return null;
	}

	public static List<String[]> resultToBatch(
			Get_entry_list_result_version2 result, List<DbField> fields) {
		Entry_value[] entry_list = result.getEntry_list();
		List<String[]> batch = new ArrayList<String[]>();
		int lenght = findLenght(fields);
		for (Entry_value entry_value : entry_list) {
			Name_value[] name_values = entry_value.getName_value_list();
			String[] row = new String[lenght];
			for (Name_value name_value : name_values) {
				DbField matchField = findField(fields, name_value.getName());
				if (matchField != null) {
					row[matchField.getIndex()] = name_value.getValue();
				}
			}
			batch.add(clearRow(row));
		}

		return batch;
	}

	public static List<String[]> resultToBatch(
			com.sugarcrm.www.sugarcrm.Entry_value entry_value,
			List<DbField> fields) {
		List<String[]> batch = new ArrayList<String[]>();
		int lenght = findLenght(fields);
		com.sugarcrm.www.sugarcrm.Name_value[] name_values = entry_value
				.getName_value_list();
		String[] row = new String[lenght];
		for (com.sugarcrm.www.sugarcrm.Name_value name_value : name_values) {
			DbField matchField = findField(fields, name_value.getName());
			if (matchField != null) {
				row[matchField.getIndex()] = name_value.getValue();
			}
		}
		batch.add(clearRow(row));

		return batch;
	}

	public static List<String[]> resultToBatch(
			Get_entry_result_version2 result, List<DbField> fields) {
		Entry_value[] entry_list = result.getEntry_list();
		List<String[]> batch = new ArrayList<String[]>();
		int lenght = findLenght(fields);
		for (Entry_value entry_value : entry_list) {
			Name_value[] name_values = entry_value.getName_value_list();
			String[] row = new String[lenght];
			for (Name_value name_value : name_values) {
				DbField matchField = findField(fields, name_value.getName());
				if (matchField != null) {
					row[matchField.getIndex()] = name_value.getValue();
				}
			}
			batch.add(clearRow(row));
		}

		return batch;
	}

	public static List<String[]> resultToBatch(
			com.g2dev.job.custom.tandem.migration.sugar.Get_entry_list_result_version2 result,
			List<DbField> fields) {
		com.g2dev.job.custom.tandem.migration.sugar.Entry_value[] entry_list = result
				.getEntry_list();
		List<String[]> batch = new ArrayList<String[]>();
		int lenght = findLenght(fields);
		for (com.g2dev.job.custom.tandem.migration.sugar.Entry_value entry_value : entry_list) {
			com.g2dev.job.custom.tandem.migration.sugar.Name_value[] name_values = entry_value
					.getName_value_list();
			String[] row = new String[lenght];
			for (com.g2dev.job.custom.tandem.migration.sugar.Name_value name_value : name_values) {
				DbField matchField = findField(fields, name_value.getName());
				if (matchField != null) {
					row[matchField.getIndex()] = name_value.getValue();
				}
			}
			batch.add(clearRow(row));
		}

		return batch;
	}

	private static DbField findField(List<DbField> fields, String name) {
		for (DbField dbField : fields) {
			if (dbField.getName().equals(name)) {
				return dbField;
			}
		}
		return null;
	}

	private static int findLenght(List<DbField> fields) {
		int l = 0;
		for (DbField dbField : fields) {
			if (dbField.getIndex() > l) {
				l = dbField.getIndex();
			}
		}
		return l + 1;
	}

	private static String[] clearRow(String[] row) {
		for (int i = 0; i < row.length; i++) {
			if (row[i] == null) {
				row[i] = "";
			}
		}
		return row;
	}
}
