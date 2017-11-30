package de.charite.compbio.attributedb.cli;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.charite.compbio.attributedb.model.score.AttributeType;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class AnnotateFromVCFSettings  {

	public static List<String> VCF_FILES;
	public static List<String> POSITIONS_FILES;
	public static String ANNOTATION_VCF_FILE;
	public static List<String> POSITIONS;
	public static List<AttributeType> ATTRIBUTE_TYPES;

	public static void parseArgs(String[] args) {

		// create Options object
		Options options = new Options();

		setOptions(options);

		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			if (args.length == 0) {
				throw new MissingOptionException("Please Insert an argument");
			}
			
			parseOptions(cmd);

		} catch (ParseException e) {
			System.err.print(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.setWidth(120);
			formatter.printHelp("AttributeDB - Annoate VCF settings ", options);
			System.exit(0);
		}

	}

	public static void setOptions(Options options) {
		HelpSettings.setOptions(options);
		// options

		options.addOption(Option.builder("f").longOpt("file").hasArgs().desc("VCF files to annotate").build());
		
		options.addOption(Option.builder("n").longOpt("name").hasArgs().desc("Name(s) of the score(s).").build());

		options.addOption(Option.builder("p").longOpt("position").hasArgs().desc("Position to get score(s). Format: chr1:12123123").build());
		
		options.addOption(Option.builder().longOpt("positions-file").hasArgs().desc("File with positions").build());
		
		options.addOption(Option.builder("a").longOpt("annotation-vcf").hasArgs().desc("VCF-File with annotations").build());

	}

	public static void parseOptions(CommandLine cmd) throws ParseException  {
		HelpSettings.parseOptions(cmd);
		
		if (cmd.hasOption("f") && cmd.hasOption("p"))
			throw new ParseException("You cannot combine option \"position\" and \"file\"!");
		
		ANNOTATION_VCF_FILE = cmd.getOptionValue("a");
		
		VCF_FILES = new ArrayList<>();
		if (cmd.hasOption("f"))
			for (String file : cmd.getOptionValues("f")) {
				VCF_FILES.add(file);
			}
		
		POSITIONS = new ArrayList<>();
		if (cmd.hasOption("p"))
			for (String pos : cmd.getOptionValues("p")) {
				POSITIONS.add(pos);
			}
		
		ATTRIBUTE_TYPES = new ArrayList<>();
		if (cmd.hasOption("n"))
			for (String name : cmd.getOptionValues("n")) {
				ATTRIBUTE_TYPES.add(new AttributeType(name, null));
			}
		POSITIONS_FILES = new ArrayList<>();
		if (cmd.hasOption("positions-file"))
			for (String name : cmd.getOptionValues("positions-file")) {
				POSITIONS_FILES.add(name);
			}
	}

}
