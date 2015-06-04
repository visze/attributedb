package de.charite.compbio.attributedb.io;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public enum FileType {

	TSV("tsv"), WIG("wig");

	private String name;

	private FileType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

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
