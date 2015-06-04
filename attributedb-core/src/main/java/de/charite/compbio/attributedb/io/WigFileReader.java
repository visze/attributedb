package de.charite.compbio.attributedb.io;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.charite.compbio.attributedb.model.score.Attribute;
import de.charite.compbio.attributedb.model.score.AttributeType;
import de.charite.compbio.attributedb.model.score.ChromosomeType;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class WigFileReader extends ScoreReader {

	public WigFileReader(List<String> files, AttributeType type) throws IOException {
		super(files, type);
	}

	private int position;
	private String chr;

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

		if (getLinesIterator() != null && getLinesIterator().hasNext()) {
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
		Pattern p = Pattern.compile("^fixedStep\\schrom=(chr([0-9]+|X|Y|M))\\sstart=(\\d+)\\sstep=(\\d+)$");
		Matcher m = p.matcher(getNextLine());
		if (m.matches()) {
			chr = m.group(2);
			position = Integer.parseInt(m.group(3));
			setNextLine(null);
		}
	}

	@Override
	public Attribute next() {
		if (hasNext()) {
			Attribute attribute = new Attribute(ChromosomeType.fromString(chr), position, getType(),
					Double.parseDouble(getNextLine()));
			this.position++;
			setNextLine(null);
			;
			return attribute;
		}
		return null;

	}

}
