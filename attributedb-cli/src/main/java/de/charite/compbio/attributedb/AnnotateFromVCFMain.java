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

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

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
	private static VCFFileReader parser;

	public static void main(String[] args) throws IOException, CompressorException {
		AnnotateFromVCFSettings.parseArgs(args);

		types = new ArrayList<>();

		parser = new VCFFileReader(new File(AnnotateFromVCFSettings.ANNOTATION_VCF_FILE), true);
		
		Collection<VCFInfoHeaderLine> infoHeaders = parser.getFileHeader().getInfoHeaderLines();
		for (VCFInfoHeaderLine vcfInfoHeaderLine : infoHeaders) {
			AttributeType type = new AttributeType(vcfInfoHeaderLine.getID(), vcfInfoHeaderLine.getDescription());
			if (type.getName().equals("SF")) //workaround
				continue;
			if (AnnotateFromVCFSettings.ATTRIBUTE_TYPES.isEmpty() || contains(AnnotateFromVCFSettings.ATTRIBUTE_TYPES,type))
				types.add(type);
		}
		

		ScorePrinter printer = new ScorePrinter();
		printer.writeHeader(types);

		if (!AnnotateFromVCFSettings.POSITIONS.isEmpty()) {
			for (String positionString : AnnotateFromVCFSettings.POSITIONS) {
				String[] split = positionString.split(":");

				List<Attribute> scores = getScores(ChromosomeType.fromString(split[0]), Integer.parseInt(split[1]));
				printer.writeScores(scores);
			}
		} else if (!AnnotateFromVCFSettings.POSITIONS_FILES.isEmpty()) {
			
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
						List<Attribute> scores = getScores(ChromosomeType.fromString(split[0]), Integer.parseInt(split[1]));
						printer.writeScores(scores);
					}
					br.close();
					
				}

		} else { // VCF
			for (String file : AnnotateFromVCFSettings.VCF_FILES) {
				VCFFileReader fr = new VCFFileReader(new File(file));
				for (VariantContext vc : fr) {
					List<Attribute> scores = getScores(ChromosomeType.fromString(vc.getContig()), vc.getStart());
					printer.writeScores(scores);
				}
				fr.close();
			}
		}
		printer.close();
		System.exit(0);
	}

	private static boolean contains(List<AttributeType> aTTRIBUTE_TYPES, AttributeType type) {
		for (AttributeType attributeType : aTTRIBUTE_TYPES) {
			if (attributeType.getName().equals(type.getName()))
				return true;
		}
		return false;
	}

	protected static List<Attribute> getScores(ChromosomeType chr, int position) {
		List<Attribute> scores = new ArrayList<>();
		CloseableIterator<VariantContext> vcI = parser.query(chr.getName(), position, position);
		if (vcI.hasNext()) {
			VariantContext vc = vcI.next();
			for (AttributeType type : types) {

				Attribute score = new Attribute(chr, position, type,
						vc.getAttributeAsDouble(type.getName(), Double.NaN));
				scores.add(score);
			}
		} else {
			for (AttributeType type : types) {
				Attribute score = new Attribute(chr, position, type, Double.NaN);
				scores.add(score);
			}
		}
		return scores;
	}

}
