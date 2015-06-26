package com.g2dev.job.custom.ogden.migration.workflow;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import com.g2dev.job.custom.ogden.migration.OgdenSugarSession;
import com.g2dev.job.custom.ogden.migration.sugar.New_note_attachment;
import com.g2dev.job.custom.ogden.migration.sugar.New_return_note_attachment;
import com.g2dev.job.custom.ogden.migration.sugar.SugarsoapPortType;
import com.g2dev.sugar.connect.SugarSession;
import com.sugarcrm.www.sugarcrm.rest.SugarRestSession;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarApiException;
import com.sugarcrm.www.sugarcrm.rest.v4.api.SugarClient;
import com.sugarcrm.www.sugarcrm.rest.v4.impl.SugarBean;

public class MigrateNoteAttachments {

	private String market = "Ogden";

	private List<String> getNotesIds() {
		List<String> noteIds = new ArrayList<String>();
		SugarRestSession session = SugarRestSession.getInstance();
		SugarClient client = session.getSanduskyClient();
		int entriesCount = 0;
		try {
			entriesCount = client.getEntriesCount(session.getSession(),
					"Meetings", null, 0);
		} catch (SugarApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int offset = 0;
		int batchSize = 2000;
		String selectFields[] = { "id", "market_c" };
		while (offset < entriesCount) {
			try {
				List<SugarBean> entryList = client.getEntryList(
						session.getSession(), "Notes", null, null, offset,
						selectFields, batchSize, 0);
				for (SugarBean sugarBean : entryList) {
					String marketValue = sugarBean.get("market_c").replaceAll(
							"\"", "");
					if (marketValue.equalsIgnoreCase(market)) {
						String id = sugarBean.get("id").replaceAll("\"", "");
						if (id != null && !id.isEmpty()) {
							noteIds.add(id);
						}

					}
				}
			} catch (SugarApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			offset += batchSize;
		}
		return noteIds;
	}

	public static void main(String[] args) {
		new MigrateNoteAttachments().start();
	}

	public void start() {
		List<String> notesIds = getNotesIds();
		OgdenSugarSession ogdenSession = OgdenSugarSession.getInstance();
		ogdenSession.loginForOgden();
		SugarsoapPortType ogdenClient = ogdenSession.getSugar();
		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		com.sugarcrm.www.sugarcrm.SugarsoapPortType client = session.getSugar();
		for (int i = 0; i < notesIds.size(); i++) {
			String id = notesIds.get(i);
			try {
				New_return_note_attachment get_note_attachment = ogdenClient
						.get_note_attachment(ogdenSession.getSessionId(), id);
				if (get_note_attachment != null) {
					New_note_attachment note_attachment = get_note_attachment
							.getNote_attachment();
					if (note_attachment != null) {
						com.sugarcrm.www.sugarcrm.New_note_attachment nnAttachment = new com.sugarcrm.www.sugarcrm.New_note_attachment(
								note_attachment.getId(),
								note_attachment.getFilename(),
								note_attachment.getFile(),
								note_attachment.getRelated_module_id(),
								note_attachment.getRelated_module_name());
						try {
							client.set_note_attachment(session.getSessionId(),
									nnAttachment);
							System.out.println(i + " " + notesIds.size());
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}

}
