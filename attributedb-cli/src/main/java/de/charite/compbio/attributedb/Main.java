package de.charite.compbio.attributedb;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.ConfigurationException;

/**
 * The attributeDB command-line program. You can upload, download or list scores that are stored in a database.
 * 
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class Main {

	/**
	 * Main method for the CLI program. Decides between the different commands (upload, upload-max, list-attributes,
	 * download) and runs the sub-command. See README.md for more explanation.
	 * 
	 * @param args
	 *            Command-line arguments
	 * @throws ParseException
	 * @throws SQLException
	 * @throws IOException
	 * @throws ConfigurationException 
	 */
	public static void main(String[] args) throws ParseException, SQLException, IOException, ConfigurationException {

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
		else if (args[0].equals("vcf"))
			CreateVCFMain.main(newArgs);
		else if (args[0].equals("download"))
			DownloadMain.main(newArgs);
		else if (args[0].equals("annotate-vcf"))
			AnnotateFromVCFMain.main(newArgs);
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
		System.err.println("         vcf			            Create a VCF with the files.");
		System.err.println("         annotate-vcf			    Annotate from a VCF file.");
		System.err.println("         list-attributes            List all available scores in the database");
	}

}
