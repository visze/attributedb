package de.charite.compbio.attributedb.model.score;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class Position {

	private ChromosomeType chr;
	private int position;
	private static final int SHIFT = 250000000;

	public Position(ChromosomeType chr, int position) {
		this.chr = chr;
		this.position = position;
	}

	public long getDatabasePosition() {
		long output = 0;
		for (ChromosomeType chr : ChromosomeType.values()) {
			if (chr == getChr())
				return output + position;
			output += SHIFT;
		}
		return -1;
	}

	public ChromosomeType getChr() {
		return chr;
	}

	public int getPosition() {
		return position;
	}

}
