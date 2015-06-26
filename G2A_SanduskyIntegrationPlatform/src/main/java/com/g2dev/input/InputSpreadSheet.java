package com.g2dev.input;

import java.io.File;

import com.g2dev.output.Output;

public abstract class InputSpreadSheet extends Input {

	protected int batchSize = 30;
	protected String encoding = "UTF8";
	protected char separator = '\t';
	protected int offset = 0;
	protected boolean forceInput;

	public abstract String[] parseOneLine(File file);

	public void parse(File file, Output integrator) {
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public char getSeparator() {
		return separator;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setForceInput(boolean b) {
		this.forceInput = b;

	}

}
