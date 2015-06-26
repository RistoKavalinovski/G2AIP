package com.g2dev.job.custom;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.g2dev.integrator.util.StringSimilarity;
import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.Entry_value;
import com.sugarcrm.www.sugarcrm.Get_entries_count_result;
import com.sugarcrm.www.sugarcrm.Get_entry_list_result_version2;
import com.sugarcrm.www.sugarcrm.Name_value;

public class AccountDedupe {

	private List<CacheAccount> accounts;
	private int limit = 1500;

	public static class CacheAccount {
		private String id;

		public CacheAccount(String id, String name, String acctno_c) {
			super();
			this.id = id;
			this.name = name;
			this.acctno_c = acctno_c;
		}

		private String name;
		private String acctno_c;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAcctno_c() {
			return acctno_c;
		}

		public void setAcctno_c(String acctno_c) {
			this.acctno_c = acctno_c;
		}
	}

	public List<CacheAccount> getAccounts() {
		if (accounts == null) {
			buildAccounts();
		}
		return accounts;
	}

	/**
	 * 
	 * @param name
	 * @param acctno_c
	 * @param relateFieldName
	 * @return appropriate Name_value to be added to the record as parent record
	 *         relate or null if the record is unique
	 */
	public Name_value getDuplicateRelateToParentNV(String name,
			String acctno_c, String relateFieldName) {
		if (acctno_c == null) {
			acctno_c = "-1";
		}
		for (CacheAccount account : getAccounts()) {
			if (acctno_c.equals(account.getAcctno_c())
					|| StringSimilarity.similarity(name, account.getName()) > 0.6) {
				System.out.println(relateFieldName + " > " + account.getId());
				return new Name_value(relateFieldName, account.getId());
			}
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(StringSimilarity.similarity("risto kavalinovski",
				"risto kavalinovski new bussines"));
	}

	private void buildAccounts() {
		long startTime = System.currentTimeMillis();
		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		String module_name = "Accounts";
		int accountsCount = 0;
		try {
			/*
			 * Get_entries_count_result result = session.getSugar()
			 * .get_entries_count(session.getSessionId(), module_name, null, 0);
			 */
			Get_entries_count_result result = session.getSugar()
					.get_entries_count(session.getSessionId(), module_name,
							null, 0);
			/*
			 * if (result.getError() == null || result.getError().getNumber() ==
			 * null || result.getError().getNumber().equals("0")) {
			 */
			accountsCount = result.getResult_count();
			/*
			 * } else { System.err
			 * .println("ERROR GETTING ACCOUNT COUNT class AccountDedupe " +
			 * result.getError()); return; }
			 */
		} catch (RemoteException e) {
			e.printStackTrace();
			return;
		}
		int offset = 0;
		// 6833
		accounts = new ArrayList<AccountDedupe.CacheAccount>();
		while (offset < accountsCount) {
			List<CacheAccount> batch = getBatch(offset);
			if (batch != null) {
				accounts.addAll(batch);
			}
			offset += limit;
		}
		long duration = System.currentTimeMillis() - startTime;
		System.out.println("duration " + duration);
		System.out.println("accounts count " + accountsCount);
		System.out.println("accounts cached " + accounts.size());
	}

	private List<CacheAccount> getBatch(int offset) {
		SugarSession session = SugarSession.getInstance();
		String module_name = "Accounts";
		String[] select_fields = { "id", "name", "acctno_c" };
		try {
			Get_entry_list_result_version2 result = session.getSugar()
					.get_entry_list(session.getSessionId(), module_name, null,
							null, offset, select_fields, null, getLimit(), 0,
							false);
			/*
			 * Get_entry_list_result result = session.getSugar().get_entry_list(
			 * session.getSessionId(), module_name, null, null, offset,
			 * select_fields, limit, 0);
			 */
			/*
			 * if (result.getError() == null || result.getError().getNumber() ==
			 * null || result.getError().getNumber().equals("0")) {
			 */
			Entry_value[] entry_list = result.getEntry_list();
			List<CacheAccount> accountsBatch = new ArrayList<CacheAccount>(
			/* entry_list.length */);
			for (Entry_value entry : entry_list) {
				Name_value[] nameValueList = entry.getName_value_list();
				CacheAccount account = new CacheAccount(null, null, null);
				for (Name_value nameValue : nameValueList) {
					if (nameValue.getName().equalsIgnoreCase("id")) {
						account.setId(nameValue.getValue());
					} else if (nameValue.getName().equalsIgnoreCase("name")) {
						account.setName(nameValue.getValue());
					} else if (nameValue.getName().equalsIgnoreCase("acctno_c")) {
						account.setAcctno_c(nameValue.getValue());
					}
				}
				accountsBatch.add(account);
			}
			return accountsBatch;

			/* } */
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setAccounts(List<CacheAccount> accounts) {
		this.accounts = accounts;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}
