package de.charite.compbio.attributedb.model.score;

import java.util.Optional;

/**
 * Position in a genome with chromosome and position.
 * 
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
/**
 * @author max
 *
 */
public class Position {

	/**
	 * Chromosome
	 */
	private ChromosomeType chr;
	/**
	 * Position in the chromosome
	 */
	private int position;
	/**
	 * Id of that position
	 */
	private Optional<String> id;
	/**
	 * A shift for the database per Chromosome (ordered chr1, chr2,...chrX,chrY,chrM).
	 */
	private static final int SHIFT = 250000000;

	/**
	 * Constructor.
	 * 
	 * @param chr
	 *            The chromosome
	 * @param position
	 *            The position in the chromosome
	 */
	public Position(ChromosomeType chr, int position) {
		this.chr = chr;
		this.position = position;
		this.id = Optional.empty();
	}
	
	public Position(ChromosomeType chr, int position, String id) {
		this.chr = chr;
		this.position = position;
		this.id = Optional.of(id);
	}

	/**
	 * Encode the position (chr,pos) to a database position (pos). Uses the value {@link #SHIFT} for each chromosome.
	 * 
	 * @return The encoded chr,pos of the position in the database.
	 */
	public long getDatabasePosition() {
		long output = 0;
		for (ChromosomeType chr : ChromosomeType.values()) {
			if (chr == getChr())
				return output + this.position;
			output += SHIFT;
		}
		return -1;
	}

	/**
	 * Getter of the Chromosome.
	 * 
	 * @return The chromosome of the position.
	 */
	public ChromosomeType getChr() {
		return this.chr;
	}

	/**
	 * Getter of {@link #position}
	 * 
	 * @return The chromosomal position.
	 */
	public int getPosition() {
		return this.position;
	}
	
	public Optional<String> getId() {
		return id;
	}

}
