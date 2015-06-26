package com.g2dev.dropbox;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxEntry.File;
import com.dropbox.core.DbxEntry.WithChildren;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.g2dev.input.ext.InputCsv;
import com.g2dev.output.Output;

public class DropboxSanduskyUtil {

	static private final String APP_KEY = "8p85yntgyqebt1i";
	static private final String APP_SECRET = "h0as0myljf5i8et";
	static private DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
	static DbxRequestConfig config = new DbxRequestConfig("g2devSandusky/1.0",
			Locale.getDefault().toString(), CustomHttpRequestor.Instance);
	static DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config,
			appInfo);

	private static String code;
	private static String accessToken = "0BAIn3Acle8AAAAAAAAADD79wT4T385duLjVAq38BqzzOVfG1utyR2cRkb975yXX";
	private static DbxAuthFinish finish;

	private static List<IntegrationDetails> integrationDetails = new ArrayList<DropboxSanduskyUtil.IntegrationDetails>();

	private static String listenFolderPath = "/Softlayer Integration Platform/Sandusky/SanduskyDev";
	private static String listenFilePath = "/Softlayer Integration Platform/Sandusky/SanduskyDev/MSGEXP01_Sandusky.txt";

	private static String backupDir = "D:\\Sandysky data\\bckp";
	private static boolean deleteAfterIntegration;

	// static {
	// StandardHttpRequestor.DefaultReadTimeoutMillis = 80 * 1000;
	// }

	public static String askForSecToken() {
		return webAuth.start();
	}

	public static void createConnect(String code) {
		DropboxSanduskyUtil.code = code;
		try {
			finish = webAuth.finish(code);
			accessToken = finish.accessToken;
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void checkAndStartIntegration(InputCsv inputCsv, Output output) {
		DbxClient client = new DbxClient(config, accessToken);
		try {
			WithChildren metadataWithChildren = client
					.getMetadataWithChildren(listenFolderPath);
			List<DbxEntry> children = metadataWithChildren.children;
			if (children != null && !children.isEmpty()) {
				for (DbxEntry entry : children) {
					if (entry.isFile()) {
						checkFileRevisions(entry, client, inputCsv, output);
					}
				}
			}
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void checkFileRevisions(DbxEntry entry, DbxClient client,
			InputCsv inputCsv, Output output) {
		try {
			List<File> revisions = client.getRevisions(entry.path);
			File f = null;
			for (File file : revisions) {
				if (f == null) {
					f = file;
				} else {
					if (f.lastModified.before(file.lastModified)) {
						f = file;
					}
				}
			}
			if (!integrationDetails.isEmpty()) {
				for (IntegrationDetails details : integrationDetails) {
					if (details.filePath.equals(f.path)
							&& !details.lastRevisionDate.after(f.lastModified)) {
						System.out.println("Synch up to date up to date ");
						System.out.println(System.currentTimeMillis());
						System.out
								.println(new Date(System.currentTimeMillis()));
						return;
					}
				}
			}
			processIntegration(f, client, inputCsv, output);
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void processIntegration(File f, DbxClient client,
			InputCsv inputCsv, Output output) {
		try {
			java.io.File hardFile = new java.io.File(f.path + "_"
					+ System.currentTimeMillis());
			if (!hardFile.exists()) {
				createFile(hardFile);
			}
			FileOutputStream outputStream = new FileOutputStream(hardFile,
					false);
			try {
				File file = client.getFile(f.path, null, outputStream);
				inputCsv.parse(hardFile, output);
				integrationDetails.add(new IntegrationDetails(new Date(System
						.currentTimeMillis()), f.path, f.lastModified));
				if (isDeleteAfterIntegration()) {
					client.delete(f.path);
				}
				System.out.println("SUCCESS!! " + System.currentTimeMillis());
			} catch (DbxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void createFile(java.io.File hardFile) {
		if (!hardFile.getParentFile().exists()) {
			createParentDir(hardFile.getParentFile());
		}
		try {
			hardFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void createParentDir(java.io.File file) {
		if (file.getParentFile() != null && !file.getParentFile().exists()) {
			createParentDir(file.getParentFile());
		}
		file.mkdir();
	}

	public static void main(String[] args) {
		checkAndStartIntegration(null, null);
	}

	public static String getListenFolderPath() {
		return listenFolderPath;
	}

	public static void setListenFolderPath(String listenFolderPath) {
		DropboxSanduskyUtil.listenFolderPath = listenFolderPath;
	}

	public static String getListenFilePath() {
		return listenFilePath;
	}

	public static void setListenFilePath(String listenFilePath) {
		DropboxSanduskyUtil.listenFilePath = listenFilePath;
	}

	public static boolean isDeleteAfterIntegration() {
		return deleteAfterIntegration;
	}

	public static void setDeleteAfterIntegration(boolean deleteAfterIntegration) {
		DropboxSanduskyUtil.deleteAfterIntegration = deleteAfterIntegration;
	}

	public static class IntegrationDetails {
		private Date startDate;

		public IntegrationDetails(Date startDate, String filePath,
				Date lastFevisionDate) {
			super();
			this.startDate = startDate;
			this.filePath = filePath;
			this.lastRevisionDate = lastFevisionDate;
		}

		private String filePath;
		private Date lastRevisionDate;

		public Date getStartDate() {
			return startDate;
		}

		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

		public Date getLastFevisionDate() {
			return lastRevisionDate;
		}

		public void setLastFevisionDate(Date lastFevisionDate) {
			this.lastRevisionDate = lastFevisionDate;
		}
	}

}
