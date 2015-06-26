package com.g2dev.connect.ext;

import java.io.File;
import java.util.List;

import com.g2dev.connect.Connector;
import com.g2dev.connect.DbField;
import com.g2dev.connect.DbObject;
import com.g2dev.connect.Schema;
import com.g2dev.input.ext.InputCsv;
import com.g2dev.input.ext.Parser;

public class CSVConnector extends Connector {

	private InputCsv inputCsv;

	public CSVConnector(InputCsv inputCsv) {
		this.setInputCsv(inputCsv);
	}

	@Override
	public Schema getSchema() {
		if (super.getSchema() == null) {
			generateSchema();
		}
		return super.getSchema();
	}

	public void generateSchema() {
		Schema schema = new Schema();
		DbObject object = new DbObject(schema);
		object.setName("CSV_File");
		String[] parseOneLine = inputCsv
				.parseOneLine(new File(getCsvFilePath()));
		if (parseOneLine != null && parseOneLine.length > 0) {
			for (int i = 0; i < parseOneLine.length; i++) {
				DbField field = new DbField(object);
				field.setIndex(i);
				field.setName(isFirstRowColumnName() ? parseOneLine[i] : i + "");
				object.getFields().add(field);
			}
		}
		schema.getObjects().add(object);
		setSchema(schema);

	}

	public static void main(String[] args) {
		CSVConnector connector = new CSVConnector(new InputCsv());
		connector
				.setCsvFilePath("D:\\Sandysky data\\Sandusky_Extracts\\MSGEXP01_Sandusky.txt");
		// connector.setFirstRowColumnName(true);
		Schema schema2 = connector.getSchema();
		List<DbObject> objects = schema2.getObjects();
		for (DbObject dbObject : objects) {
			System.out.println(dbObject.getName());
			List<DbField> fields = dbObject.getFields();
			for (DbField field : fields) {
				System.out.println("\t" + field.getName());
			}
		}
	}

	public InputCsv getInputCsv() {
		return inputCsv;
	}

	public void setInputCsv(InputCsv inputCsv) {
		this.inputCsv = inputCsv;
	}

}
