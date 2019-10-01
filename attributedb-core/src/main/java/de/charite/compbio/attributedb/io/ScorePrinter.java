package de.charite.compbio.attributedb.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import de.charite.compbio.attributedb.model.score.Attribute;
import de.charite.compbio.attributedb.model.score.AttributeType;

public class ScorePrinter implements Closeable {

	private final CSVFormat format = CSVFormat.newFormat('\t').withQuote(null).withRecordSeparator("\r\n")
			.withIgnoreSurroundingSpaces(true);
	private CSVPrinter printer;

	public ScorePrinter() {
	}

	public CSVPrinter getPrinter() throws IOException {
		if (this.printer == null)
			this.printer = new CSVPrinter(System.out, this.format);
		return this.printer;
	}
	
	public void writeHeader(boolean useID, List<AttributeType>... types) throws IOException {
		List<String> header = new ArrayList<>();
		header.add("CHR");
		header.add("POSITION");
		if (useID) {
			header.add("ID");
		}
		for (List<AttributeType> ts : types)
			for (AttributeType type : ts) {
				header.add(type.getName());
			}
		getPrinter().printRecord(header);

	}

	public void writeHeader(List<AttributeType>... types) throws IOException {
		List<String> header = new ArrayList<>();
		header.add("CHR");
		header.add("POSITION");
		for (List<AttributeType> ts : types)
			for (AttributeType type : ts) {
				header.add(type.getName());
			}
		getPrinter().printRecord(header);

	}
	
	public void writeScoresWithId(List<Attribute> scores) throws IOException {
		List<Object> columns = new ArrayList<>();
		boolean first = true;
		for (Attribute score : scores) {
			if (first) {
				first = false;
				columns.add(score.getChr().getName());
				columns.add(score.getPosition());
				columns.add(score.getId().get());
						}
			columns.add(score.getValue());
		}
		getPrinter().printRecord(columns);

	}

	public void writeScores(List<Attribute> scores) throws IOException {
		List<Object> columns = new ArrayList<>();
		boolean first = true;
		for (Attribute score : scores) {
			if (first) {
				first = false;
				columns.add(score.getChr().getName());
				columns.add(score.getPosition());
				if (score.getId().isPresent()) {
					columns.add(score.getId().get());
				}
			}
			columns.add(score.getValue());
		}
		getPrinter().printRecord(columns);

	}

	@Override
	public void close() throws IOException {
		getPrinter().close();
		
	}
}
