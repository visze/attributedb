package de.charite.compbio.attributedb.io.gerp;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.charite.compbio.attributedb.io.ScoreReader;
import de.charite.compbio.attributedb.model.score.Attribute;
import de.charite.compbio.attributedb.model.score.AttributeType;
import de.charite.compbio.attributedb.model.score.ChromosomeType;

/**
 * Abstract class to read a GERP++ element file. There are two scores avaiable in this file. First, the GERP++ RS score
 * and the GERP++ p-value
 * 
 * @see "{@link RSScoreReader}"
 * 
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public abstract class GERPElementsFileReader extends ScoreReader {

	/**
	 * 
	 * Default constructor. Sets the super constructor.
	 * 
	 * @param files
	 *            {@link List} with paths to the files (but only one element-file on the GERP website)
	 * @param type
	 *            The {@link AttributeType} of the score you upload (something like gerprsscore or gerppvalue).
	 * @throws IOException
	 */
	public GERPElementsFileReader(List<String> files, AttributeType type) throws IOException {
		super(files, type);
	}

	private String chr;
	private int position = 0;
	private int splitPosition;

	@Override
	public boolean hasNext() {
		if (getLinesIterator() != null) {

			if (getNextLine() == null && getLinesIterator().hasNext()) {
				setNextLine(getLinesIterator().next());
				checkHeader();
				return hasNext();
			}
		}

		if (getNextLine() != null) {
			return true;
		}

		if (getFileIterator() != null && getFileIterator().hasNext()) {
			try {
				setNextReader();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return hasNext();
		}
		return false;
	}

	private void checkHeader() {
		Pattern p = Pattern.compile("^hg19_(chr([0-9]+|X|Y|M))_elems.*");
		Matcher m = p.matcher(getNextLine());
		if (m.matches()) {
			this.chr = m.group(2);
			setNextLine(null);
		}
	}

	@Override
	public Attribute next() {
		if (hasNext()) {
			String[] split = getNextLine().split("\t");
			int start = Integer.parseInt(split[0]);
			int end = Integer.parseInt(split[1]);
			double value = Double.parseDouble(split[this.splitPosition]);
			if (end >= start + this.position) {
				Attribute attribute = new Attribute(ChromosomeType.fromString(this.chr), start + this.position, getType(), value);
				this.position++;
				return attribute;
			} else {
				setNextLine(null);
				this.position = 0;
			}
		}
		return null;

	}

	protected void setSplitPosition(int splitPosition) {
		this.splitPosition = splitPosition;
	}

}
