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

	private boolean fixedStep;
	private int position;
	private String chr;
	private int end;

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
		Pattern p = Pattern.compile("^#?fixedStep\\schrom=(chr([0-9]+|X|Y|M))\\sstart=(\\d+)\\sstep=(\\d+)$");
		Matcher m = p.matcher(getNextLine());
		if (m.matches()) {
			fixedStep = true;
			this.chr = m.group(2);
			this.position = Integer.parseInt(m.group(3));
			this.end = Integer.parseInt(m.group(4));
			setNextLine(null);
		} else {
			p = Pattern.compile("^#?bedGraph.*(chr([0-9]+|X|Y|M)):(\\d+)-(\\d+)$");
			m = p.matcher(getNextLine());
			if (m.matches()) {
				fixedStep = false;
				this.chr = m.group(2);
				this.position = Integer.parseInt(m.group(3));
				this.end = this.position + Integer.parseInt(m.group(4));
				setNextLine(null);
			}
		}
	}

	@Override
	public Attribute next() {
		if (hasNext()) {
			double value;
			if (fixedStep) {
				value = Double.parseDouble(getNextLine());
				Attribute attribute = new Attribute(ChromosomeType.fromString(this.chr), this.position, getType(),
						value);
				this.position += this.end;
				setNextLine(null);
				return attribute;
			} else {
				String[] split = getNextLine().split("\t");
				value = Double.parseDouble(split[3]);
				end = Integer.parseInt(split[2]);
				if (this.end > this.position) {
					Attribute attribute = new Attribute(ChromosomeType.fromString(this.chr), this.position, getType(),
							value);
					this.position++;
					return attribute;
				} else {
					setNextLine(null);
				}
			}

		}
		return null;

	}
}
