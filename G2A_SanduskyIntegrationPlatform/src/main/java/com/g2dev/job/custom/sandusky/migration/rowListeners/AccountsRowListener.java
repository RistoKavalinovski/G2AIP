package com.g2dev.job.custom.sandusky.migration.rowListeners;

import java.util.ArrayList;
import java.util.List;

import com.g2dev.job.custom.AccountDedupe;
import com.g2dev.job.custom.NewRowListener;
import com.sugarcrm.www.sugarcrm.Name_value;

public class AccountsRowListener implements NewRowListener {

	private AccountDedupe accountDedupe;
	private Name_value marketNameValue;
	private List<String> ids = new ArrayList<String>();

	public AccountsRowListener(AccountDedupe accountDedupe) {
		super();
		this.accountDedupe = accountDedupe;
	}

	public List<Name_value> newRowEvent(List<Name_value> currentRow) {
		Name_value oldId = new Name_value("oldid_c", "");
		Name_value newId = new Name_value("new_with_id", "");
		for (Name_value name_value : currentRow) {
			if (name_value.getName().equalsIgnoreCase("id")) {
				oldId.setValue(name_value.getValue());
				if (ids.contains(name_value.getValue())) {
					newId.setValue("0");
				} else {
					ids.add(name_value.getValue());
					newId.setValue("1");
				}
			}
			if (name_value.getName().equalsIgnoreCase("deleted")) {
				name_value.setValue("0");
			}
		}
		List<Name_value> custom = new ArrayList<Name_value>();
		custom.add(oldId);
		custom.add(newId);
		custom.add(getMarketNameValue());
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
	}

	public AccountDedupe getAccountDedupe() {
		return accountDedupe;
	}

	public void setAccountDedupe(AccountDedupe accountDedupe) {
		this.accountDedupe = accountDedupe;
	}

	public Name_value getMarketNameValue() {
		if (marketNameValue == null) {
			marketNameValue = new Name_value("market_c", "Sandusky");

		}
		return marketNameValue;
	}

	public void setMarket(Name_value market) {
		this.marketNameValue = market;
	}

	public void afterInsertBatch(List<List<Name_value>> targetBatch) {
		// TODO Auto-generated method stub

	}
	
	public void beforeProcessBatch(List<String[]> sourceBatch){}

	public List<List<Name_value>> trimRows(List<List<Name_value>> targetBatch) {
		// TODO Auto-generated method stub
		return null;
	}

}
