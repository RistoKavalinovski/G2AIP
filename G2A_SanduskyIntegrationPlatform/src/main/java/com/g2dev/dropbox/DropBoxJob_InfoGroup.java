package com.g2dev.dropbox;

import java.util.Locale;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxRequestConfig;
import com.g2dev.job.custom.processors.SpreadSheetJobInfoGroup;

public class DropBoxJob_InfoGroup extends DropBoxJob {
	public static final String FOLDER_PATH_INFO_GROUP_OGDEN = "/Softlayer Integration Platform/Sandusky/SanduskyTest/InfoGroup/Ogden";
	public static final String FOLDER_PATH_INFO_GROUP_TANDEM = "/Softlayer Integration Platform/Sandusky/SanduskyTest/InfoGroup/Tandem";

	public DropBoxJob_InfoGroup() {
		super();
	}

	public static void main(String[] args) {
		IntegrationHistory ih = new IntegrationHistory();
		DbxRequestConfig config = new DbxRequestConfig("g2devSandusky/1.0",
				Locale.getDefault().toString(), CustomHttpRequestor.Instance);
		String accessToken = "0BAIn3Acle8AAAAAAAAADD79wT4T385duLjVAq38BqzzOVfG1utyR2cRkb975yXX";
		// test(config, accessToken);
		DropBoxJob_InfoGroup dbjSNG = new DropBoxJob_InfoGroup();
		while (true) {
			System.out.println("InfoGroup data check initiated "
					+ System.currentTimeMillis());
			DbxClient client = new DbxClient(config, accessToken);
			// market Ogden
			dbjSNG.setIntegrationProcess(new SpreadSheetJobInfoGroup());
			dbjSNG.setListenerFolderPath(FOLDER_PATH_INFO_GROUP_OGDEN);
			dbjSNG.setMarket("Ogden");
			dbjSNG.checkAndPush(client, ih);
			// market Tandem
			dbjSNG.setIntegrationProcess(new SpreadSheetJobInfoGroup());
			dbjSNG.setListenerFolderPath(FOLDER_PATH_INFO_GROUP_TANDEM);
			dbjSNG.setMarket("Tandem");
			dbjSNG.checkAndPush(client, ih);

			// sleep for 5 minutes and check again
			try {
				System.out.println("InfoGroup data check completed "
						+ System.currentTimeMillis());
				Thread.sleep(300000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
}
