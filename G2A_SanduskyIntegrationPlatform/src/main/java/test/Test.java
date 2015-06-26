package test;

import java.util.List;

import com.sugarcrm.www.sugarcrm.rest.v4.api.ISugarBean;
import com.sugarcrm.www.sugarcrm.rest.v4.api.ISugarRESTSession;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarClient;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarCredentials;
import com.sugarcrm.www.sugarcrm.rest.v4.impl.SugarBean;

public class Test {

	public static void main(String[] args) {
		testLogin();
	}

	public static void testLogin() {
		SugarClient client = new SugarClient(
				"https://sanduskydev.sugarondemand.com");
		try {
			// "G2Dev.Sandusky", "G2@nalyt1c$2015", "7.5.1.0"
			ISugarRESTSession session = client.getSugarSession(
					"G2Dev.Sandusky", "G2@nalyt1c$2015");
			System.out.println(session);
			// assertNotNull(session);
			// assertNotNull(session.getSessionID());
			// User user = session.getUser();
			// assertNotNull(user);
			// assertEquals(user.getUserName(), "G2Dev.Sandusky");
			// assertEquals(user.getUserId(), "1");
			// assertEquals(user.getModuleName(), "Users");

			String[] selectFields = { "id", "oldid_c" };

			// System.out.println(client.getBean(session, "Accounts",
			// "2b4fc3b9-c322-dfb6-2a97-54f878d698cf"));
			// "accounts.market_c='Ogden'"
			
			client.getEntriesCount(session, "Accounts", null, 0);
			
//			List<SugarBean> entryList = client.getEntryList(session,
//					"Accounts", null, null, 0, selectFields, 3000, 0);
//			System.out.println("size: " + entryList.size());
//			for (SugarBean sugarBean : entryList) {
//				System.out.println(/*
//									 * sugarBean.get("name") + " | " +
//									 */sugarBean.get("id") + " | "
//						+ sugarBean.get("oldid_c"));
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void testGetBean() {
		SugarClient client = new SugarClient(
				"http://sugar658-sugartest.rhcloud.com");
		try {
			ISugarRESTSession session = client
					.getSugarSession(new SugarCredentials("admin", "admin"));
			ISugarBean bean = client.getBean(session, "Opportunities",
					"9b4b7e72-6459-a199-25f5-50b7492a2777");
			// assertEquals("Opportunities", bean.getModuleName());
			// assertEquals("Prospecting", bean.get("sales_stage"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
