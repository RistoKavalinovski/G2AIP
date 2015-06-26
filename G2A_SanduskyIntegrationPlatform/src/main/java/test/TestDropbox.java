package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

public class TestDropbox {

	public static void main(String[] args) {
		// Get your app key and secret from the Dropbox developers website.
		final String APP_KEY = "8p85yntgyqebt1i";
		final String APP_SECRET = "h0as0myljf5i8et";

		DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

		DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
				Locale.getDefault().toString());
		DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
		String authorizeUrl = webAuth.start();
		System.out.println("1. Go to: " + authorizeUrl);
		System.out
				.println("2. Click \"Allow\" (you might have to log in first)");
		System.out.println("3. Copy the authorization code.");
		try {
			String code = new BufferedReader(new InputStreamReader(System.in))
					.readLine().trim();
			System.out.println("code: " + code);
			DbxAuthFinish authFinish;
			try {
				authFinish = webAuth.finish(code);
				String accessToken = authFinish.accessToken;
				DbxClient client = new DbxClient(config, accessToken);
				System.out.println("Linked account: "
						+ client.getAccountInfo().displayName);

				DbxEntry.WithChildren listing = client
						.getMetadataWithChildren("/");
				System.out.println("Files in the root path:");
				iterateChildren(listing.children, client);
				test(client);
			} catch (DbxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void iterateChildren(List<DbxEntry> children,
			DbxClient client) {

		for (DbxEntry entry : children) {
			System.out.println(entry.name);
			System.out.println(entry.path);
			if (entry.isFolder()) {
				try {
					WithChildren metadataWithChildren = client
							.getMetadataWithChildren(entry.path);
					iterateChildren(metadataWithChildren.children, client);
				} catch (DbxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static void test(DbxClient client) {
		
		try {
			List<File> revisions = client
					.getRevisions("/Softlayer Integration Platform/Sandusky/SanduskyDev/MSGEXP01_Sandusky.txt");
			for (File file : revisions) {
				System.out.println(file.lastModified);
			}
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
