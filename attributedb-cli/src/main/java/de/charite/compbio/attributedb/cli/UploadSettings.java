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

import de.charite.compbio.attributedb.io.FileType;
import de.charite.compbio.attributedb.model.score.AttributeType;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class UploadSettings extends DatabaseSettings {

	public static List<String> FILES;
	public static FileType FILE_TYPE;
	public static AttributeType ATTRIBUTE_TYPE;
	public static boolean UPLOAD_ZERO;

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
			formatter.printHelp("AttributeDB - Upload settings", options);
			System.exit(0);
		}

	}

	public static void setOptions(Options options) {
		HelpSettings.setOptions(options);
		DatabaseSettings.setOptions(options);
		// options

		OptionBuilder.withLongOpt("file");
		OptionBuilder.withDescription("file or files to upload");
		OptionBuilder.isRequired();
		OptionBuilder.hasArgs();
		options.addOption(OptionBuilder.create("f"));

		OptionBuilder.withLongOpt("type");
		OptionBuilder.withDescription("file type. default tsv (chr\\tpos\\tscore)");
		OptionBuilder.hasArg();
		options.addOption(OptionBuilder.create("t"));

		OptionBuilder.withLongOpt("name");
		OptionBuilder.withDescription("Name of the score. Must be unique in the database");
		OptionBuilder.isRequired();
		OptionBuilder.hasArg();
		options.addOption(OptionBuilder.create("n"));

		OptionBuilder.withLongOpt("description");
		OptionBuilder.hasArg();
		OptionBuilder.isRequired();
		OptionBuilder.withDescription("A detailed description of the database.");
		options.addOption(OptionBuilder.create("d"));

		OptionBuilder.withLongOpt("no-zero-upload");
		OptionBuilder
				.withDescription("If option is provided no scores with 0 are uploaded. Recommended if score is present for every genome position.");
		options.addOption(OptionBuilder.create());

	}

	public static void parseOptions(CommandLine cmd) throws ParseException {

		HelpSettings.parseOptions(cmd);
		DatabaseSettings.parseOptions(cmd);

		FILES = new ArrayList<String>();
		for (String option : cmd.getOptionValues("f")) {
			FILES.add(option);
		}
		ATTRIBUTE_TYPE = new AttributeType(cmd.getOptionValue("n"), cmd.getOptionValue("d"));

		if (cmd.hasOption("t"))
			FILE_TYPE = FileType.fromString(cmd.getOptionValue("t"));
		else
			FILE_TYPE = FileType.TSV;
		
		UPLOAD_ZERO = !cmd.hasOption("no-zero-upload"); 

	}

}
