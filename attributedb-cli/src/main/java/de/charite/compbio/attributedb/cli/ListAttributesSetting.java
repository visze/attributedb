package de.charite.compbio.attributedb.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ListAttributesSetting extends DatabaseSettings {

	public static String NAME_LIKE = "%";
	public static String NAME_ILIKE = "%";

	public static void parseArgs(String[] args) {

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
			formatter.printHelp("AttributeDB - List attributes", options);
			System.exit(0);
		}

	}

	public static void setOptions(Options options) {
		OptionBuilder.withLongOpt("name-like");
		OptionBuilder.withDescription("Attribute type name LIKE (SQL Regex). Default %");
		OptionBuilder.hasArg();
		options.addOption(OptionBuilder.create("l"));

		OptionBuilder.withLongOpt("name-ilike");
		OptionBuilder.withDescription("Attribute type name ILIKE (SQL Regex). Default %");
		OptionBuilder.hasArg();
		options.addOption(OptionBuilder.create("i"));

		DatabaseSettings.setOptions(options);
		HelpSettings.setOptions(options);

	}

	public static void parseOptions(CommandLine cmd)  throws ParseException  {
		HelpSettings.parseOptions(cmd);
		
		if (cmd.hasOption("l"))
			NAME_LIKE = cmd.getOptionValue("l").trim();
		if (cmd.hasOption("i"))
			NAME_ILIKE = cmd.getOptionValue("i").trim();
		DatabaseSettings.parseOptions(cmd);
		
	}

}
