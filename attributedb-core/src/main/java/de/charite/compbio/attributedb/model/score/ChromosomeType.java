package de.charite.compbio.attributedb.model.score;


public enum ChromosomeType {

	CHR1("chr1", "1"), CHR2("chr2", "2"), CHR3("chr3", "3"), CHR4("chr4", "4"), CHR5("chr5", "5"), CHR6("chr6", "6"), CHR7(
			"chr7", "7"), CHR8("chr8", "8"), CHR9("chr9", "9"), CHR10("chr10", "10"), CHR11("chr11", "11"), CHR12(
			"chr12", "12"), CHR13("chr13", "13"), CHR14("chr14", "14"), CHR15("chr15", "15"), CHR16("chr16", "16"), CHR17(
			"chr17", "17"), CHR18("chr18", "18"), CHR19("chr19", "19"), CHR20("chr20", "20"), CHR21("chr21", "21"), CHR22(
			"chr22", "22"), CHRX("chrX", "X"), CHRY("chrY", "Y"), CHRM("chrM", "M");

	private String name;
	private String shortName;

	private ChromosomeType(String name, String shortName) {
		this.name = name;
		this.shortName = shortName;
	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}

	public static ChromosomeType fromString(String text) {
		if (text != null) {
			for (ChromosomeType b : ChromosomeType.values()) {
				if (text.equalsIgnoreCase(b.getName()) || text.equalsIgnoreCase(b.getShortName())) {
					return b;
				}
			}
		}
		return null;
	}

}
