package de.charite.compbio.attributedb;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.OptionalDouble;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import de.charite.compbio.attributedb.cli.AnnotateFromVCFSettings;
import de.charite.compbio.attributedb.io.ScorePrinter;
import de.charite.compbio.attributedb.model.score.Attribute;
import de.charite.compbio.attributedb.model.score.AttributeType;
import de.charite.compbio.attributedb.model.score.ChromosomeType;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class AnnotateFromVCFMain {

	private static List<AttributeType> types;
	private static List<AttributeType> originalTypes;
	private static VCFFileReader parser;

	public static void main(String[] args) throws IOException, CompressorException {
		AnnotateFromVCFSettings.parseArgs(args);

		

		parser = new VCFFileReader(new File(AnnotateFromVCFSettings.ANNOTATION_VCF_FILE), true);

		types = collectTypesFromHeader(parser);

		ScorePrinter printer = new ScorePrinter();
		

		if (!AnnotateFromVCFSettings.POSITIONS.isEmpty()) {
			printer.writeHeader(types);
			for (String positionString : AnnotateFromVCFSettings.POSITIONS) {
				String[] split = positionString.split(":");

				List<Attribute> scores = getScores(ChromosomeType.fromString(split[0]), Integer.parseInt(split[1]),
						Integer.parseInt(split[1]));
				printer.writeScores(scores);
			}
		} else if (!AnnotateFromVCFSettings.POSITIONS_FILES.isEmpty()) {
			printer.writeHeader(types);

			for (String path : AnnotateFromVCFSettings.POSITIONS_FILES) {

				InputStream fin = new FileInputStream(path);
				BufferedInputStream in = new BufferedInputStream(fin);

				CompressorInputStream gzIn = new CompressorStreamFactory().createCompressorInputStream(in);
				//
				Reader reader = new InputStreamReader(gzIn, Charset.defaultCharset());
				BufferedReader br = new BufferedReader(reader);
				String line;
				while ((line = br.readLine()) != null) {
					String[] split = line.trim().split(":");
					List<Attribute> scores = getScores(ChromosomeType.fromString(split[0]), Integer.parseInt(split[1]),
							Integer.parseInt(split[1]));
					printer.writeScores(scores);
				}
				br.close();

			}

		} else { // VCF
			originalTypes = new ArrayList<>();
			for (String file : AnnotateFromVCFSettings.VCF_FILES) {
				VCFFileReader fr = new VCFFileReader(new File(file));
				originalTypes.addAll(collectTypesFromHeader(fr));
			}
			printer.writeHeader(true, types,originalTypes);
			for (String file : AnnotateFromVCFSettings.VCF_FILES) {
				VCFFileReader fr = new VCFFileReader(new File(file));
				for (VariantContext vc : fr) {
					List<Attribute> scores;
					if (vc.isSimpleInsertion()) {
						scores = getScores(ChromosomeType.fromString(vc.getContig()), vc.getStart(), vc.getEnd() + 1, vc.getID());
					} else if (vc.isSimpleDeletion()) {
						scores = getScores(ChromosomeType.fromString(vc.getContig()), vc.getStart() + 1, vc.getEnd(), vc.getID());
					} else if (vc.isMNP()) {
						scores = getScores(ChromosomeType.fromString(vc.getContig()), vc.getStart() + 1, vc.getEnd(), vc.getID());
					} else
						scores = getScores(ChromosomeType.fromString(vc.getContig()), vc.getStart(), vc.getEnd(), vc.getID());

					List<Attribute> originalScores = getAverageScores(getAttributeScores(vc, originalTypes),
							ChromosomeType.fromString(vc.getContig()), vc.getStart(), originalTypes);
					scores.addAll(originalScores);
					printer.writeScoresWithId(scores);
				}
				fr.close();
			}
		}
		printer.close();
		System.exit(0);
	}

	private static List<AttributeType> collectTypesFromHeader(VCFFileReader parser) {
		List<AttributeType> types = new ArrayList<>();
		Collection<VCFInfoHeaderLine> infoHeaders = parser.getFileHeader().getInfoHeaderLines();
		for (VCFInfoHeaderLine vcfInfoHeaderLine : infoHeaders) {
			AttributeType type = new AttributeType(vcfInfoHeaderLine.getID(), vcfInfoHeaderLine.getDescription());
			if (type.getName().equals("SF")) // workaround
				continue;
			if (AnnotateFromVCFSettings.ATTRIBUTE_TYPES.isEmpty()
					|| contains(AnnotateFromVCFSettings.ATTRIBUTE_TYPES, type))
				types.add(type);
		}
		return types;
	}

	private static boolean contains(List<AttributeType> aTTRIBUTE_TYPES, AttributeType type) {
		for (AttributeType attributeType : aTTRIBUTE_TYPES) {
			if (attributeType.getName().equals(type.getName()))
				return true;
		}
		return false;
	}

	protected static List<Attribute> getScores(ChromosomeType chr, int start, int end) {
		List<Attribute> scores = new ArrayList<>();
		CloseableIterator<VariantContext> vcI = parser.query(chr.getName(), start, end);
		if (vcI.hasNext()) {
			ListMultimap<AttributeType, Double> combined = MultimapBuilder.hashKeys().arrayListValues().build();
			while (vcI.hasNext()) {
				VariantContext vc = vcI.next();
				combined.putAll(getAttributeScores(vc, types));
			}
			scores.addAll(getAverageScores(combined, chr, start, types));

		} else {
			for (AttributeType type : types) {
				Attribute score = new Attribute(chr, start, type, Double.NaN);
				scores.add(score);
			}
		}
		return scores;
	}
	
	protected static List<Attribute> getScores(ChromosomeType chr, int start, int end, String id) {
		List<Attribute> scores = new ArrayList<>();
		CloseableIterator<VariantContext> vcI = parser.query(chr.getName(), start, end);
		if (vcI.hasNext()) {
			ListMultimap<AttributeType, Double> combined = MultimapBuilder.hashKeys().arrayListValues().build();
			while (vcI.hasNext()) {
				VariantContext vc = vcI.next();
				combined.putAll(getAttributeScores(vc, types));
			}
			scores.addAll(getAverageScores(combined, chr, start, id, types));

		} else {
			for (AttributeType type : types) {
				Attribute score = new Attribute(chr, start, id, type, Double.NaN);
				scores.add(score);
			}
		}
		return scores;
	}

	private static ListMultimap<AttributeType, Double> getAttributeScores(VariantContext vc,
			List<AttributeType> types) {
		ListMultimap<AttributeType, Double> result = MultimapBuilder.hashKeys().arrayListValues().build();
		for (AttributeType type : types) {
			Double value = vc.getAttributeAsDouble(type.getName(), Double.NaN);
			if (!Double.isNaN(value))
				result.get(type).add(value);
		}
		return result;
	}

	private static List<Attribute> getAverageScores(ListMultimap<AttributeType, Double> results, ChromosomeType chr,
			int start, List<AttributeType> types) {
		List<Attribute> scores = new ArrayList<>();
		for (AttributeType type : types) {
			if (!results.containsKey(type))
				scores.add(new Attribute(chr, start, type, Double.NaN));
			else {
				OptionalDouble value = results.get(type).stream().mapToDouble(Double::doubleValue).average();
				scores.add(new Attribute(chr, start, type, value.getAsDouble()));
			}
		}
		return scores;
	}
	
	private static List<Attribute> getAverageScores(ListMultimap<AttributeType, Double> results, ChromosomeType chr,
			int start, String id, List<AttributeType> types) {
		List<Attribute> scores = new ArrayList<>();
		for (AttributeType type : types) {
			if (!results.containsKey(type))
				scores.add(new Attribute(chr, start, id, type, Double.NaN));
			else {
				OptionalDouble value = results.get(type).stream().mapToDouble(Double::doubleValue).average();
				scores.add(new Attribute(chr, start, id, type, value.getAsDouble()));
			}
		}
		return scores;
	}

}
