package com.g2dev.input.ext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

import com.g2dev.input.InputSpreadSheet;
import com.g2dev.output.Output;

public class InputCsv extends InputSpreadSheet {

	// private int batchSize = 30;
	// private String encoding = "UTF8";
	// private char separator = '\t';
	// private int offset = 0;
	// private boolean forceInput;

	public String[] parseOneLine(File file) {

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), encoding));
			String str;

			try {
				String[] batch = null;
				while ((str = in.readLine()) != null) {
					batch = str.split(getSeparator() + "");
					break;
				}
				in.close();
				return batch;
			} catch (IOException e) {
				e.printStackTrace();
				try {
					in.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		}
		return null;

	}

	public void parse(File file, Output integrator) {
		integrator.setForceInput(forceInput);
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), encoding));
			CSVReader reader = new CSVReader(in, getSeparator());
			// String str;

			try {
				int c = 0;
				List<String[]> batch = new ArrayList<String[]>();
				for (int i = 0; i < offset; i++) {
					// in.readLine();
					reader.readNext();
				}
				// while ((str = in.readLine()) != null) {
				// batch.add(str.split(getSeparator()));
				String[] row = null;
				while ((row = reader.readNext()) != null) {
					batch.add(row);
					c++;
					if (c == getBatchSize()) {
						integrator.process(batch);
						c = 0;
						batch.clear();
					}
				}
				if (!batch.isEmpty()) {
					integrator.process(batch);
				}
				// in.close();
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				try {
					// in.close();
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		}

	}

	// public static void main(String[] args) {
	// Parser parser = new Parser();
	// parser.parse(new File(
	// "D:\\Sandysky data\\Sandusky_Extracts\\MSGEXP01_Sandusky.txt"),
	// new CSV_Integrator() {
	//
	// @Override
	// public void process(List<String[]> batch) {
	// System.out.println("Batch size " + batch.size());
	// for (String[] row : batch) {
	// for (String field : row) {
	// System.out.print(field + " | ");
	// }
	// System.out.println();
	// }
	//
	// }
	// });
	// }

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
