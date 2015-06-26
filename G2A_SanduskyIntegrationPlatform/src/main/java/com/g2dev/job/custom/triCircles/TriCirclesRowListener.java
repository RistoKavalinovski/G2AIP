package com.g2dev.job.custom.triCircles;

import java.util.ArrayList;
import java.util.List;

import com.g2dev.job.custom.AccountDedupe;
import com.g2dev.job.custom.NewRowListener;
import com.sugarcrm.www.sugarcrm.Name_value;

public class TriCirclesRowListener implements NewRowListener {

	private AccountDedupe accountDedupe;

	public TriCirclesRowListener(AccountDedupe accountDedupe) {
		super();
		this.accountDedupe = accountDedupe;
	}

	public List<Name_value> newRowEvent(List<Name_value> currentRow) {
		String name = "";
		String acctno_c = null;
		// TODO provide the parent relate to field name!!!
		String relateFieldName = "parent_relate_c";
		for (Name_value name_value : currentRow) {
			if (name_value.getName().equalsIgnoreCase("name")) {
				name = name_value.getValue();
			} else if (name_value.getName().equalsIgnoreCase("acctno_c")) {
				acctno_c = name_value.getValue();
			}
		}
		List<Name_value> feedback = new ArrayList<Name_value>();
		Name_value duplicateRelateToParentNV = getAccountDedupe()
				.getDuplicateRelateToParentNV(name, acctno_c, relateFieldName);
		if (duplicateRelateToParentNV != null) {
			feedback.add(duplicateRelateToParentNV);
		}
		return feedback;
	}

	public AccountDedupe getAccountDedupe() {
		return accountDedupe;
	}

	public void setAccountDedupe(AccountDedupe accountDedupe) {
		this.accountDedupe = accountDedupe;
	}

	public void afterInsertBatch(List<List<Name_value>> targetBatch) {
		// TODO Auto-generated method stub
		
	}

	public void beforeProcessBatch(List<String[]> sourceBatch) {
		// TODO Auto-generated method stub
		
	}

	public List<List<Name_value>> trimRows(List<List<Name_value>> targetBatch) {
		// TODO Auto-generated method stub
		return null;
	}

}
