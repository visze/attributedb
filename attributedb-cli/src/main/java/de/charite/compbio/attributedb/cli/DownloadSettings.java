package de.charite.compbio.attributedb.cli;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.charite.compbio.attributedb.model.score.AttributeType;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class DownloadSettings extends DatabaseSettings {

	public static List<String> VCF_FILES;
	public static List<String> POSITIONS;
	public static List<AttributeType> ATTRIBUTE_TYPES;

	public static void parseArgs(String[] args) throws ParseException {

		// create Options object
		Options options = new Options();

		setOptions(options);

		CommandLineParser parser = new GnuParser();
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
			formatter.printHelp("AttributeDB - Download settings", options);
			System.exit(0);
		}

	}

	public static void setOptions(Options options) {
		HelpSettings.setOptions(options);
		DatabaseSettings.setOptions(options);
		// options

		OptionBuilder.withLongOpt("file");
		OptionBuilder.withDescription("file or files to upload");
		OptionBuilder.hasArgs();
		options.addOption(OptionBuilder.create("f"));

		OptionBuilder.withLongOpt("name");
		OptionBuilder.withDescription("Name(s) of the score(s).");
		OptionBuilder.isRequired();
		OptionBuilder.hasArgs();
		options.addOption(OptionBuilder.create("n"));

		OptionBuilder.withLongOpt("position");
		OptionBuilder.hasArgs();
		OptionBuilder.withDescription("Position to get score(s). Format: chr1:12123123");
		options.addOption(OptionBuilder.create("p"));

	}

	public static void parseOptions(CommandLine cmd) throws ParseException  {
		HelpSettings.parseOptions(cmd);
		DatabaseSettings.parseOptions(cmd);
		ATTRIBUTE_TYPES = new ArrayList<AttributeType>();
		
		if (cmd.hasOption("f") && cmd.hasOption("p"))
			throw new ParseException("You cannot combine option \"position\" and \"file\"!");
		
		VCF_FILES = new ArrayList<String>();
		if (cmd.hasOption("f"))
			for (String file : cmd.getOptionValues("f")) {
				VCF_FILES.add(file);
			}
		
		POSITIONS = new ArrayList<String>();
		if (cmd.hasOption("p"))
			for (String pos : cmd.getOptionValues("p")) {
				POSITIONS.add(pos);
			}
		
		ATTRIBUTE_TYPES = new ArrayList<AttributeType>();
		if (cmd.hasOption("n"))
			for (String name : cmd.getOptionValues("n")) {
				ATTRIBUTE_TYPES.add(new AttributeType(name, null));
			}
	}

}
