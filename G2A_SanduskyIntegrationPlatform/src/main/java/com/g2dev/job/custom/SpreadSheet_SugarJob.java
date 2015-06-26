package com.g2dev.job.custom;

import java.io.File;

import com.g2dev.input.InputSpreadSheet;
import com.g2dev.input.ext.InputCsv;
import com.g2dev.map.IntegrationMap;
import com.g2dev.output.ext.SugarOutputProcessorCSV;

public abstract class SpreadSheet_SugarJob {

	protected boolean firstRowHeader;

	protected String moduleName;

	protected String market;

	protected File spreadSheetFile;

	protected InputSpreadSheet inputSpreadSheet;

	public SpreadSheet_SugarJob(String moduleName) {
		setModuleName(moduleName);
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public File getSpreadSheetFile() {
		return spreadSheetFile;
	}

	public void setSpreadSheetFile(File spreadSheetFile) {
		this.spreadSheetFile = spreadSheetFile;
	}

	public boolean isFirstRowHeader() {
		return firstRowHeader;
	}

	public void setFirstRowHeader(boolean firstRowHeader) {
		this.firstRowHeader = firstRowHeader;
	}

	public IntegrationMap buildMap(File mapFile) {
		IntegrationMap im = new IntegrationMap();
		im.loadFromFile(mapFile, moduleName);
		return im;
	}

	public abstract void start();

	protected void defaultStart(File mapFile, NewRowListener listener,
			boolean pushExistingRecords) {
		defaultStart(null, mapFile, listener, pushExistingRecords);
	}

	protected void defaultStart(String query, File mapFile,
			NewRowListener listener, boolean pushExistingRecords) {
		if (inputSpreadSheet == null) {
			inputSpreadSheet = new InputCsv();
		}
		SugarOutputProcessorCSV integrator = new SugarOutputProcessorCSV(
				moduleName, buildMap(mapFile), listener);
		integrator.setRemoveDuplicates(pushExistingRecords);
		if (firstRowHeader) {
			inputSpreadSheet.setOffset(1);
		}
		inputSpreadSheet.parse(getSpreadSheetFile(), integrator);
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

}
