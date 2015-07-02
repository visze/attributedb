package de.charite.compbio.attributedb.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class CreateVCFSettings {

	public static List<PropertiesConfiguration> CONFIGS;
	public static String OUTPUT;
	public static String POSITION_FILE;

	public static void parseArgs(String[] args) throws IOException, ConfigurationException {

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
			formatter.printHelp("AttributeDB - CreateVCF", options);
			System.exit(0);
		}

	}

	public static void setOptions(Options options) {
		HelpSettings.setOptions(options);
		// options

		OptionBuilder.withLongOpt("property");
		OptionBuilder.withDescription("Java property file of one score");
		OptionBuilder.isRequired();
		OptionBuilder.hasArgs();
		options.addOption(OptionBuilder.create("p"));

		OptionBuilder.withLongOpt("output");
		OptionBuilder.withDescription("output of the VCF file. Default: standard output");
		OptionBuilder.hasArg();
		options.addOption(OptionBuilder.create("o"));
		
		OptionBuilder.withLongOpt("positions");
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Upload only the positions given in this file. (CHR<TAB>pos...)");
		options.addOption(OptionBuilder.create());

	}

	public static void parseOptions(CommandLine cmd) throws ParseException, IOException, ConfigurationException {

		HelpSettings.parseOptions(cmd);

		CONFIGS = new ArrayList<>();
		for (String option : cmd.getOptionValues("p")) {

			
			Properties props = new Properties();
			InputStream input = new FileInputStream(new File(option));
			props.load(input);
			PropertiesConfiguration config = new PropertiesConfiguration(option);
			
			validate(config, option);
			CONFIGS.add(config);
		}

		if (cmd.hasOption("o"))
			OUTPUT = cmd.getOptionValue("o");
		
		if (cmd.hasOption("positions"))
			POSITION_FILE = cmd.getOptionValue("positions");

	}

	private static void validate(PropertiesConfiguration props, String file) throws ParseException {
		if (!(props.containsKey("method") && props.containsKey("type") && props.containsKey("file")
				&& props.containsKey("name") && props.containsKey("description"))) {
			throw new ParseException("Wrong property file: " + file);
		}

	}
}
