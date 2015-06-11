package de.charite.compbio.attributedb;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.cli.ParseException;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class Main {

	public static void main(String[] args) throws ParseException, SQLException, IOException {

		if (args.length == 0) {
			// No arguments, print top level help and exit.
			printTopLevelHelp();
			System.exit(1);
		}
		String[] newArgs = new String[args.length - 1];
		for (int i = 0; i < newArgs.length; i++) {
			newArgs[i] = args[i + 1];
		}
		if (args[0].equals("upload"))
			UploadMain.main(newArgs);
		else if (args[0].equals("upload-max"))
			UploadMaxMain.main(newArgs);
		else if (args[0].equals("list-attributes"))
			ListAttributesMain.main(newArgs);
		else if (args[0].equals("download"))
			DownloadMain.main(newArgs);
		else {
			printTopLevelHelp();
			System.err.println("unrecognized command " + args[0]);
		}

		System.exit(0);
	}

	/**
	 * Print top level help (without any command).
	 */
	private static void printTopLevelHelp() {
		System.err.println("Program: attributedb");
		System.err.println("Contact: Max Schubach <max.schubach@charite.de>");
		System.err.println("");
		System.err.println("Usage: java -jar attributedb.jar <command> [options]");
		System.err.println("");
		System.err.println("Command: upload                     Upload a score into the Database");
		System.err.println("         upload-max		            Upload the maximum score between different files.");
		System.err.println("         download		            Download scores from the database or annotate VCF-files.");
		System.err.println("         list-attributes            List all available scores in the database");
	}

}
