package com.g2dev.job.custom.ogden.migration.workflow;

import java.rmi.RemoteException;
import java.util.List;

import com.g2dev.job.custom.ogden.migration.OgdenSugarSession;
import com.g2dev.job.custom.ogden.migration.sugar.Document_revision;
import com.g2dev.job.custom.ogden.migration.sugar.New_return_document_revision;
import com.g2dev.job.custom.ogden.migration.sugar.SugarsoapPortType;
import com.g2dev.sugar.connect.SugarSession;

public class MigrateDocumentFiles {

	public static void main(String[] args) {
		new MigrateDocumentFiles().start();
	}

	public void start() {
		SugarSession session = SugarSession.getInstance();
		session.loginForSandusky();
		OgdenSugarSession ogdenSession = OgdenSugarSession.getInstance();
		ogdenSession.loginForOgden();
		SugarsoapPortType ogdenClient = ogdenSession.getSugar();
		List<String> moduleIds = SugarSession.getIDs("DocumentRevisions", null);
		for (int i = 0; i < moduleIds.size(); i++) {
			String id = moduleIds.get(i);
			try {
				New_return_document_revision get_document_revision = ogdenClient
						.get_document_revision(ogdenSession.getSessionId(), id);
				if (get_document_revision != null) {
					Document_revision document_revision = get_document_revision
							.getDocument_revision();
					if (document_revision != null) {
						com.sugarcrm.www.sugarcrm.Document_revision nDRV = new com.sugarcrm.www.sugarcrm.Document_revision(
								document_revision.getId(),
								document_revision.getDocument_name(),
								document_revision.getRevision(),
								document_revision.getFilename(),
								document_revision.getFile());
						try {
							session.getSugar().set_document_revision(
									session.getSessionId(), nDRV);
							System.out.println(i + " / " + moduleIds.size());
							System.out.println(id);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
