package de.charite.compbio.attributedb;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.Options;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.variantcontext.writer.VariantContextWriterBuilder;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import de.charite.compbio.attributedb.cli.CreateVCFSettings;
import de.charite.compbio.attributedb.io.FileType;
import de.charite.compbio.attributedb.io.ScoreReader;
import de.charite.compbio.attributedb.io.ScoreReaderBuilder;
import de.charite.compbio.attributedb.model.IScoreIterator;
import de.charite.compbio.attributedb.model.MaxScoreIterator;
import de.charite.compbio.attributedb.model.ScoreIterator;
import de.charite.compbio.attributedb.model.score.Attribute;
import de.charite.compbio.attributedb.model.score.AttributeType;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class CreateVCFMain {

	private static List<IScoreIterator> iterators;
	private static List<Attribute> scores;
	private static List<AttributeType> types;

	/**
	 * @param args
	 * @throws SQLException
	 * @throws ParseException
	 * @throws IOException
	 * @throws ConfigurationException
	 */
	public static void main(String[] args) throws IOException, ConfigurationException {

		CreateVCFSettings.parseArgs(args);

		iterators = new ArrayList<IScoreIterator>();
		types = new ArrayList<AttributeType>();
		for (PropertiesConfiguration config : CreateVCFSettings.CONFIGS) {
			AttributeType type = new AttributeType(config.getString("name"), config.getString("description"));
			types.add(type);
			FileType ft = FileType.fromString(config.getString("type"));
			List<Object> filesObject = config.getList("file");
			List<String> files = new ArrayList<String>();
			for (Object object : filesObject) {
				files.add((String) object);
			}
			boolean uploadZeros = !config.containsKey("no-zero-upload");
			int column = config.getInt("column", 4);

			if (config.getString("method").equals("upload")) {
				ScoreReader reader = new ScoreReaderBuilder().setFileType(ft).setAttributeType(type)
						.setScoreColumn(column).setFiles(files).create();
				ScoreIterator iterator;

				if (CreateVCFSettings.POSITION_FILE != null)
					iterator = new ScoreIterator(reader, CreateVCFSettings.POSITION_FILE);
				else
					iterator = new ScoreIterator(reader);

				iterator.setUploadZeros(uploadZeros);

				iterators.add(iterator);

			} else if (config.getString("method").equals("upload-max")) {

				List<ScoreReader> readers = new ArrayList<ScoreReader>();

				for (String file : files) {
					ScoreReader reader = new ScoreReaderBuilder().setScoreColumn(column).setFileType(ft)
							.setAttributeType(type).setFiles(Arrays.asList(file)).create();

					readers.add(reader);
				}
				MaxScoreIterator iterator;
				if (CreateVCFSettings.POSITION_FILE != null)
					iterator = new MaxScoreIterator(readers, CreateVCFSettings.POSITION_FILE);
				else
					iterator = new MaxScoreIterator(readers);

				iterator.setUploadZeros(uploadZeros);

				iterators.add(iterator);
			}

		}

		initScores();

		VCFHeader header = createHeader();
		
		VariantContextWriterBuilder vcWBuilder = new VariantContextWriterBuilder();
				
		if (CreateVCFSettings.OUTPUT != null) {
			vcWBuilder = vcWBuilder.unsetOption(Options.INDEX_ON_THE_FLY);
			vcWBuilder = vcWBuilder.setOutputFile(new File(CreateVCFSettings.OUTPUT));
		} else {
			OutputStream out = System.out;
			vcWBuilder = vcWBuilder.unsetOption(Options.INDEX_ON_THE_FLY);
			vcWBuilder = vcWBuilder.setOutputVCFStream(out);
		}
		
		

		VariantContextWriter writer = vcWBuilder.build();
		writer.writeHeader(header);

		List<Attribute> nextScores = new ArrayList<Attribute>();
		int i = 0;
		while (hasNext()) {
			nextScores = nextScores();
			Allele ref = Allele.create("N", true);
			VariantContextBuilder vcBuilder = new VariantContextBuilder().alleles(Arrays.asList(ref)).noGenotypes();
			for (int j = 0; j < types.size(); j++) {
				if (nextScores.get(j) != null) {
					Attribute at = nextScores.get(j);
					vcBuilder = vcBuilder.attribute(types.get(j).getName(), at.getValue()).chr(at.getChr().getName())
							.start(at.getPosition()).stop(at.getPosition());
				}
			}
			VariantContext vc = vcBuilder.make();
			writer.add(vc);
			i++;
		}
		writer.close();
		System.exit(0);
	}

	private static VCFHeader createHeader() {
		VCFHeader output = new VCFHeader();
		// VCFFormatHeaderLine format = new VCFFormatHeaderLine("test", VCFHeaderVersion.VCF4_2);
		for (AttributeType type : types) {
			VCFInfoHeaderLine info = new VCFInfoHeaderLine(type.getName(), VCFHeaderLineCount.R,
					VCFHeaderLineType.Float, type.getDescription());
			output.addMetaDataLine(info);
		}
		return output;
	}

	private static List<Attribute> nextScores() {
		List<Attribute> output = new ArrayList<Attribute>(iterators.size());
		List<Integer> index = new ArrayList<Integer>(iterators.size());
		// iterate over all readers and find the next and the max score;
		for (int i = 0; i < iterators.size(); i++) {

			Attribute score = scores.get(i);

			if (score == null) {
				output.add(null);
				continue;
			}
			if (output.isEmpty()) {
				output.add(score);
				index.add(i);
			} else if (smaller(output, score)) {
				output = new ArrayList<Attribute>(iterators.size());
				for (int j = 0; j < i; j++) {
					output.add(null);
				}
				output.add(score);
				index = new ArrayList<>(iterators.size());
				index.add(i);
			} else if (same(output, score)) {
				output.add(score);
				index.add(i); // same position
			} else {
				output.add(null);
			}
		}

		// get next scores for actual position.
		for (Integer i : index) {
			if (iterators.get(i).hasNext())
				scores.set(i, iterators.get(i).next());
			else
				scores.set(i, null);
		}

		return output;
	}

	private static boolean same(List<Attribute> output, Attribute score) {
		for (Attribute attribute : output) {
			if (attribute == null)
				continue;
			if (!(score.getChr() == attribute.getChr() && score.getPosition() == attribute.getPosition()))
				return false;
		}
		return true;
	}

	private static boolean smaller(List<Attribute> output, Attribute score) {
		for (Attribute attribute : output) {
			if (attribute == null)
				continue;
			if (score.getChr().getOrder() < attribute.getChr().getOrder())
				return true;
			if (score.getChr() == attribute.getChr() && score.getPosition() < attribute.getPosition())
				return true;
		}
		return false;
	}

	private static void initScores() {
		scores = new ArrayList<Attribute>();
		for (IScoreIterator iScoreIterator : iterators) {
			scores.add(iScoreIterator.next());
		}

	}

	private static boolean hasNext() {
		for (Attribute score : scores) {
			if (score != null)
				return true;
		}
		return false;
	}
}
