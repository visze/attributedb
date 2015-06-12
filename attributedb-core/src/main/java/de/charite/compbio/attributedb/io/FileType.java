package de.charite.compbio.attributedb.io;

/**
 * Different file formats that are implemented to upload into the database.
 * 
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public enum FileType {

	/**
	 * TSV files (tab separated). Format is Chromsosome, position, score. No Header!
	 */
	TSV("tsv"),
	/**
	 * Wig file format with fixedLength.
	 */
	WIG("wig"),
	/**
	 * File format of the GERP++ RS score in the elements file of the website. Uses the first column.
	 */
	GERP_RS_SCORE("gerprs");

	private String name;

	private FileType(String name) {
		this.name = name;
	}

	/**
	 * Getter of the name field
	 * 
	 * @return Returns a #{@link String} with the name of the enum.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * get the correct {@link FileType} enum of the given {@link String}.
	 * 
	 * @param text
	 *            name of the enum
	 * @return The corresponding ENUM of the given {@link String}. Returns null if not found. 
	 */
	public static FileType fromString(String text) {
		if (text != null) {
			for (FileType b : FileType.values()) {
				if (text.equalsIgnoreCase(b.getName())) {
					return b;
				}
			}
		}
		return null;
	}

}
