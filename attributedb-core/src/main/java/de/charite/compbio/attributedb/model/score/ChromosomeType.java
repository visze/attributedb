package de.charite.compbio.attributedb.model.score;


public enum ChromosomeType {

	CHR1("chr1", "1",1), CHR2("chr2", "2",12), CHR3("chr3", "3",16), CHR4("chr4", "4",17), CHR5("chr5", "5",18), CHR6("chr6", "6",19), CHR7(
			"chr7", "7",20), CHR8("chr8", "8",21), CHR9("chr9", "9",22), CHR10("chr10", "10",2), CHR11("chr11", "11",3), CHR12(
			"chr12", "12",4), CHR13("chr13", "13",5), CHR14("chr14", "14",6), CHR15("chr15", "15",7), CHR16("chr16", "16",8), CHR17(
			"chr17", "17",9), CHR18("chr18", "18",10), CHR19("chr19", "19",11), CHR20("chr20", "20",13), CHR21("chr21", "21",14), CHR22(
			"chr22", "22",15), CHRX("chrX", "X",24), CHRY("chrY", "Y",25), CHRM("chrM", "M",23);

	private String name;
	private String shortName;
	private int order;

	private ChromosomeType(String name, String shortName, int order) {
		this.name = name;
		this.shortName = shortName;
		this.order = order;
	}

	public String getName() {
		return this.name;
	}

	public String getShortName() {
		return this.shortName;
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
	
	public int getOrder() {
		return order;
	}

}
