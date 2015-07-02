package de.charite.compbio.attributedb.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.charite.compbio.attributedb.db.DatabaseConnection;

public class DatabaseSettings {

	public static void setOptions(Options options) {

		// options
		OptionBuilder.withLongOpt("host");
		OptionBuilder.withDescription("host name");
		OptionBuilder.hasArg();
		options.addOption(OptionBuilder.create("H"));

		OptionBuilder.withLongOpt("database");
		OptionBuilder.withDescription("Database name.");
		OptionBuilder.isRequired();
		OptionBuilder.hasArg();
		options.addOption(OptionBuilder.create("D"));

		OptionBuilder.withLongOpt("username");
		OptionBuilder.withDescription("database username. Default: no username");
		OptionBuilder.hasArg();
		options.addOption(OptionBuilder.create("U"));

		OptionBuilder.withLongOpt("password");
		OptionBuilder.withDescription("database username password. Default: no password");
		OptionBuilder.hasArg();
		options.addOption(OptionBuilder.create("W"));

		OptionBuilder.withLongOpt("port");
		OptionBuilder.withDescription("Database port (postgres). Default: 5432");
		OptionBuilder.hasArg();
		options.addOption(OptionBuilder.create("P"));

		OptionBuilder.withLongOpt("sqlite");
		OptionBuilder.withDescription("Use sqLite3 database. Only -D flag is needed. (path to database)");
		options.addOption(OptionBuilder.create());

		OptionBuilder.withLongOpt("h2");
		OptionBuilder.withDescription("Use h2 database. Only -D flag is needed. (path to database)");
		options.addOption(OptionBuilder.create());

	}

	/**
	 * @param cmd
	 * @throws ParseException
	 */
	public static void parseOptions(CommandLine cmd) throws ParseException {

		DatabaseConnection.setDATABASE(cmd.getOptionValue("D"));
		if (cmd.hasOption("sqlite"))
			DatabaseConnection.setSQLITE(true);
		else if (cmd.hasOption("h2"))
			DatabaseConnection.setH2(true);
		else
			DatabaseConnection.setHOST(cmd.getOptionValue("H"));

		if (cmd.hasOption("U"))
			DatabaseConnection.setUSER(cmd.getOptionValue("U"));
		if (cmd.hasOption("W"))
			DatabaseConnection.setPASSWORD(cmd.getOptionValue("W"));

		if (cmd.hasOption("P"))
			DatabaseConnection.setPORT(Integer.parseInt(cmd.getOptionValue("P")));

	}

}
