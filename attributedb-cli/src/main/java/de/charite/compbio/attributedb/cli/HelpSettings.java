package de.charite.compbio.attributedb.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class HelpSettings {

	public static void setOptions(Options options) {
		OptionBuilder.withLongOpt("help");
		OptionBuilder.withDescription("Print this help message");
		options.addOption(OptionBuilder.create("h"));

	}

	public static void parseOptions(CommandLine cmd) throws ParseException {
		if (cmd.hasOption("h")) {
			throw new MissingOptionException("Please Insert an argument");
		}

	}

}
