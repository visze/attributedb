package de.charite.compbio.attributedb.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;

import de.charite.compbio.attributedb.model.score.Attribute;
import de.charite.compbio.attributedb.model.score.AttributeType;
import de.charite.compbio.attributedb.model.score.ChromosomeType;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class BEDScoreReader extends ScoreReader {

	private int scoreColumn;
	private int position = 0;

	public BEDScoreReader(List<String> files, AttributeType type, int column) throws IOException {
		super(files, type);
		this.scoreColumn = column;
	}

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
		Pattern p = Pattern.compile("^#.*$");
		Matcher m = p.matcher(getNextLine());
		if (m.matches())
			setNextLine(null);
	}

	@Override
	public Attribute next() {
		if (hasNext()) {
			List<String> split = new ArrayList<>();
			for (String string : Splitter.on('\t').trimResults().omitEmptyStrings().split(getNextLine())) {
				split.add(string);
			}
			ChromosomeType chr = ChromosomeType.fromString(split.get(0));
			int start = Integer.parseInt(split.get(1))+1;
			int end = Integer.parseInt(split.get(2))+1;
			double value = Double.parseDouble(split.get(getScoreColumn()-1));
			if (end > start + this.position) {
				Attribute attribute = new Attribute(chr, start + this.position, getType(), value);
				this.position++;
				return attribute;
			} else {
				setNextLine(null);
				this.position = 0;
				if (hasNext())
					return next();
			}
		}
		return null;
	}

	private int getScoreColumn() {
		if (scoreColumn < 4)
			return 4;
		return scoreColumn;
	}
}
