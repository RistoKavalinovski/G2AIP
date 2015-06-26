package com.g2dev.job.custom.sandusky.migration;

import java.rmi.RemoteException;
import java.util.Collection;

import com.g2dev.job.custom.SugarInput;
import com.g2dev.job.custom.sandusky.migration.sugar.Get_entries_count_result;
import com.g2dev.job.custom.sandusky.migration.sugar.Get_entry_list_result;

public class SanduskyInput extends SugarInput {

	private String moduleName;
	private Collection<String> selectFields;
	private int deleted = 0;

	public SanduskyInput(String moduleName) {
		setModuleName(moduleName);
	}

	public SanduskyInput(String moduleName, Collection<String> selectFields) {
		setModuleName(moduleName);
		setSelectFields(selectFields);
	}

	public void start(String query) {
		setOffset(0);
		SanduskySugarSession session = SanduskySugarSession.getInstance();
		if (session.loginForSandusky()) {
			try {
				Get_entries_count_result get_entries_count = session.getSugar()
						.get_entries_count(session.getSessionId(),
								getModuleName(), query, deleted);
				setRecordsCount(get_entries_count.getResult_count());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// super.setRecordsCount(recordsCount);
	}

	public static void main(String[] args) {
		try {
			Get_entry_list_result get_entry_list = SanduskySugarSession
					.getInstance().getSugar()
					.get_entry_list(null, null, null, null, 0, null, 0, 0);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Get_entry_list_result nextBatch(String query) {
		if (getOffset() < getRecordsCount()) {
			SanduskySugarSession session = SanduskySugarSession.getInstance();
			if (session.loginForSandusky()) {
				try {
					Get_entry_list_result get_entry_list = session
							.getSugar()
							.get_entry_list(
									session.getSessionId(),
									getModuleName(),
									query,
									null,
									getOffset(),
									getSelectFields()
											.toArray(
													new String[getSelectFields()
															.size()]),
									getBatchSize(), deleted);
					setOffset(getOffset() + getBatchSize());
					return get_entry_list;

				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public Collection<String> getSelectFields() {
		return selectFields;
	}

	public void setSelectFields(Collection<String> selectFields) {
		// for (String string : selectFields) {
		// System.out.print(string + " ");
		// }
		// System.out.println();
		this.selectFields = selectFields;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

}
