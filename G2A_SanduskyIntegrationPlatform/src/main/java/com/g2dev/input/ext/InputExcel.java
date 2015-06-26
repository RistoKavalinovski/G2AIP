package com.g2dev.input.ext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.g2dev.input.InputSpreadSheet;
import com.g2dev.output.Output;

public class InputExcel extends InputSpreadSheet {

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
		try {

			XSSFWorkbook wb;
			try {
				wb = new XSSFWorkbook((file));

				// HSSFWorkbook wb = new HSSFWorkbook(fs);
				XSSFSheet sheet = wb.getSheetAt(0);
				// String str;

				try {
					int c = 0;
					List<String[]> batch = new ArrayList<String[]>();
					String[] row = null;
					for (int i = offset; i < sheet.getPhysicalNumberOfRows(); i++) {
						row = hssToRow(sheet.getRow(i));

						batch.add(row);
						c++;
						if (c == getBatchSize()) {
							integrator.process(batch);
							c = 0;
							batch = new ArrayList<String[]>();
						}
					}
					if (!batch.isEmpty()) {
						integrator.process(batch);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (InvalidFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

	}

	private DecimalFormat df = new DecimalFormat("0.#");

	private String[] hssToRow(XSSFRow xssfRow) {
		String[] sRow = new String[xssfRow.getPhysicalNumberOfCells() + 100];
		for (int i = 0; i < sRow.length; i++) {
			// sRow[i] = xssfRow.getCell(i).getStringCellValue();
			XSSFCell cell = xssfRow.getCell(i);
			String value = "";
			if (cell != null) {

				if (XSSFCell.CELL_TYPE_NUMERIC == cell.getCellType()) {

					value = df.format(cell.getNumericCellValue());
				} else {
					value = cell.toString();
				}
			}
			if (value == null) {
				value = "";
			}
			sRow[i] = value;
			// sRow[i] = cell != null ? cell.toString() : "";
		}
		return sRow;
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
