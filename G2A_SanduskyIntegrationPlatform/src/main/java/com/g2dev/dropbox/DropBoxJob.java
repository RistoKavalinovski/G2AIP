package com.g2dev.dropbox;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxEntry.File;
import com.dropbox.core.DbxEntry.WithChildren;
import com.dropbox.core.DbxException;
import com.g2dev.job.custom.SpreadSheet_SugarJob;

public class DropBoxJob {

	protected String listenerFolderPath;
	protected SpreadSheet_SugarJob integrationProcess;
	// hardcoded the default market is Ogden
	protected String market = "Ogden";

	public DropBoxJob(String listenerFolderPath,
			SpreadSheet_SugarJob integrationProcess) {
		setListenerFolderPath(listenerFolderPath);
		setIntegrationProcess(integrationProcess);
	}

	public DropBoxJob() {
		this(null, null);
	}

	public void checkAndPush(DbxClient client, IntegrationHistory ih) {

		try {
			WithChildren metadataWithChildren = client
					.getMetadataWithChildren(getListenerFolderPath());
			List<DbxEntry> children = metadataWithChildren.children;
			if (children != null && !children.isEmpty()) {
				for (DbxEntry entry : children) {
					// if it is a new file push it
					if (entry.isFile()) {
						// TODO
						File latestRevision = checkAndGetLatestFileRevisions(
								entry, client, ih);
						if (latestRevision != null) {
							java.io.File hardFile = new java.io.File(
									latestRevision.path + "_"
											+ System.currentTimeMillis());
							if (!hardFile.exists()) {
								createFile(hardFile);
							}
							try {
								FileOutputStream outputStream = new FileOutputStream(
										hardFile, false);
								// make a local copy of the file on dropbox
								try {
									client.getFile(latestRevision.path, null,
											outputStream);
									// push the file to sugar crm
									// outputStream.flush();
									// outputStream.close();
									integrationProcess
											.setSpreadSheetFile(hardFile);
									integrationProcess.setMarket(getMarket());
									integrationProcess.start();
								} catch (IOException e) {
									e.printStackTrace();
								}
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		} catch (DbxException e) {
			e.printStackTrace();
		}

	}

	protected static void createFile(java.io.File hardFile) {
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

	protected static void createParentDir(java.io.File file) {
		if (file.getParentFile() != null && !file.getParentFile().exists()) {
			createParentDir(file.getParentFile());
		}
		file.mkdir();
	}

	protected static File checkAndGetLatestFileRevisions(DbxEntry entry,
			DbxClient client, IntegrationHistory ih) {
		List<File> revisions;
		try {
			revisions = client.getRevisions(entry.path);

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
			return ih.isFileProcessed(f) ? null : f;
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getListenerFolderPath() {
		return listenerFolderPath;
	}

	public void setListenerFolderPath(String folderPath) {
		this.listenerFolderPath = folderPath;
	}

	public SpreadSheet_SugarJob getIntegrationProcess() {
		return integrationProcess;
	}

	public void setIntegrationProcess(SpreadSheet_SugarJob integrationProcess) {
		this.integrationProcess = integrationProcess;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public static void main(String[] args) {
		IntegrationHistory ih = new IntegrationHistory();
		DbxRequestConfig config = new DbxRequestConfig("g2devSandusky/1.0",
				Locale.getDefault().toString(), CustomHttpRequestor.Instance);
		String accessToken = "0BAIn3Acle8AAAAAAAAADD79wT4T385duLjVAq38BqzzOVfG1utyR2cRkb975yXX";
		DbxClient client = new DbxClient(config, accessToken);
		try {
			WithChildren metadataWithChildren = client
					.getMetadataWithChildren("/");
			printChildren(metadataWithChildren.children, client);
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void printChildren(List<DbxEntry> children, DbxClient client) {
		for (DbxEntry dbxEntry : children) {
			System.out.println(dbxEntry.path);
			try {
				WithChildren metadataWithChildren = client
						.getMetadataWithChildren(dbxEntry.path);
				if (metadataWithChildren.children != null
						&& !metadataWithChildren.children.isEmpty()) {
					printChildren(metadataWithChildren.children, client);
				}
			} catch (DbxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
