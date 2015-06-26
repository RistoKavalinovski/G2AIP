package com.g2dev.dropbox;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxEntry.File;
import com.dropbox.core.DbxEntry.WithChildren;
import com.g2dev.job.custom.processors.SpreadSheetJobSNG;

public class DropBoxJob_SNG {

	public static final String FOLDER_PATH_SNG = "/Softlayer Integration Platform/Sandusky/SanduskyProduction/Ogden/Vision Data";

	public void checkAndPush(DbxClient client, IntegrationHistory ih) {

		try {
			WithChildren metadataWithChildren = client
					.getMetadataWithChildren(FOLDER_PATH_SNG);
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
									SpreadSheetJobSNG scip = new SpreadSheetJobSNG();
									scip.setMarket("Ogden");
									scip.setSpreadSheetFile(hardFile);
									scip.start();
									client.delete(latestRevision.path);
									System.out.println("File Deleted "
											+ latestRevision.path);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
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

	public static void main(String[] args) {
		IntegrationHistory ih = new IntegrationHistory();
		DbxRequestConfig config = new DbxRequestConfig("g2devSandusky/1.0",
				Locale.getDefault().toString(), CustomHttpRequestor.Instance);
		String accessToken = "0BAIn3Acle8AAAAAAAAADD79wT4T385duLjVAq38BqzzOVfG1utyR2cRkb975yXX";
		// test(config, accessToken);
		DropBoxJob_SNG dbjSNG = new DropBoxJob_SNG();
		while (true) {
			System.out.println("SNG data check initiated "
					+ System.currentTimeMillis());
			DbxClient client = new DbxClient(config, accessToken);
			dbjSNG.checkAndPush(client, ih);

			// sleep for 5 minutes and check again
			try {
				System.out.println("SNG data check completed "
						+ System.currentTimeMillis());
				Thread.sleep(300000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private static void test(DbxRequestConfig config, String accessToken) {
		DbxClient client = new DbxClient(config, accessToken);
		try {
			WithChildren metadataWithChildren = client
					.getMetadataWithChildren("/");
			printFolder("", client, metadataWithChildren);
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void printFolder(String tab, DbxClient client,
			WithChildren metadataWithChildren) {
		for (DbxEntry s : metadataWithChildren.children) {
			if (s.name.equalsIgnoreCase("SNG")) {
				System.out.println(s.path);
			}
			// System.out.println(tab + s.name);
			if (s.isFolder()) {
				try {
					printFolder(tab + "\t", client,
							client.getMetadataWithChildren(s.path));
				} catch (DbxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
