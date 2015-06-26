package com.g2dev.dropbox;

import java.util.ArrayList;
import java.util.List;

import com.dropbox.core.DbxEntry.File;

public class IntegrationHistory {

	private List<File> processedFiles = new ArrayList<File>();

	public boolean isFileProcessed(File f) {
		return processedFiles.contains(f);
	}

	public void fileProcessed(File f) {
		processedFiles.add(f);
	}

}
