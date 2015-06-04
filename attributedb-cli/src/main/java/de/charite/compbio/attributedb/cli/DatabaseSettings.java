package de.charite.compbio.attributedb.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import de.charite.compbio.attributedb.db.DatabaseConnection;

public class DatabaseSettings {

	public static void setOptions(Options options) {

		// options
		OptionBuilder.withLongOpt("host");
		OptionBuilder.withDescription("host name");
		OptionBuilder.isRequired();
		OptionBuilder.hasArg();
		options.addOption(OptionBuilder.create("H"));

		OptionBuilder.withLongOpt("database");
		OptionBuilder.withDescription("database name");
		OptionBuilder.isRequired();
		OptionBuilder.hasArg();
		options.addOption(OptionBuilder.create("D"));

		OptionBuilder.withLongOpt("username");
		OptionBuilder.withDescription("database username");
		OptionBuilder.isRequired();
		OptionBuilder.hasArg();
		options.addOption(OptionBuilder.create("U"));

		OptionBuilder.withLongOpt("password");
		OptionBuilder.withDescription("database username password");
		OptionBuilder.isRequired();
		OptionBuilder.hasArg();
		options.addOption(OptionBuilder.create("W"));

		OptionBuilder.withLongOpt("port");
		OptionBuilder.withDescription("database port. Default 5432");
		OptionBuilder.hasArg();
		options.addOption(OptionBuilder.create("P"));

	}

	public static void parseOptions(CommandLine cmd) {

		DatabaseConnection.setHOST(cmd.getOptionValue("H"));
		DatabaseConnection.setDATABASE(cmd.getOptionValue("D"));
		DatabaseConnection.setUSER(cmd.getOptionValue("U"));
		DatabaseConnection.setPASSWORD(cmd.getOptionValue("W"));

		if (cmd.hasOption("P"))
			DatabaseConnection.setPORT(Integer.parseInt(cmd.getOptionValue("P")));

	}

}
