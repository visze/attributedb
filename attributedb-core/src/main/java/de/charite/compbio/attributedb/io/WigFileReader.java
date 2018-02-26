package de.charite.compbio.attributedb.io;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
	private boolean variableStep;
	private int position;
	private String chr;
	private int end;
	private int span;

	@Override
	public boolean hasNext() {
		if (getLinesIterator().isPresent()) {

			if (!getNextLine().isPresent() && getLinesIterator().get().hasNext()) {
				setNextLine(Optional.of(getLinesIterator().get().next()));
				checkHeader();
				return hasNext();
			}
		}

		if (getNextLine().isPresent()) {
			return true;
		}

		if (getFileIterator().isPresent() && getFileIterator().get().hasNext()) {
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
		Pattern pFixed = Pattern.compile("^#?fixedStep\\schrom=(chr([0-9]+|X|Y|M))\\sstart=(\\d+)\\sstep=(\\d+)$");
		Pattern pVariable = Pattern.compile("^#?variableStep\\schrom=(chr([0-9]+|X|Y|M))\\sspan=(\\d+)$");
		Matcher mFixed = pFixed.matcher(getNextLine().get());
		Matcher mVariable = pVariable.matcher(getNextLine().get());
		if (mFixed.matches()) {
			fixedStep = true;
			this.chr = mFixed.group(2);
			this.position = Integer.parseInt(mFixed.group(3));
			this.end = Integer.parseInt(mFixed.group(4));
			setNextLine(Optional.empty());
		} else if (mVariable.matches()) {
			variableStep = true;
			this.chr = mVariable.group(2);
			this.position = Integer.MIN_VALUE;
			this.span = Integer.parseInt(mVariable.group(3));
			setNextLine(Optional.empty());
		} else {
			pFixed = Pattern.compile("^#?bedGraph.*(chr([0-9]+|X|Y|M)):(\\d+)-(\\d+)$");
			mFixed = pFixed.matcher(getNextLine().get());
			if (mFixed.matches()) {
				fixedStep = false;
				this.chr = mFixed.group(2);
				this.position = Integer.parseInt(mFixed.group(3));
				this.end = this.position + Integer.parseInt(mFixed.group(4));
				setNextLine(Optional.empty());
			}
		}
	}

	@Override
	public Attribute next() {
		if (hasNext()) {
			double value;
			if (fixedStep) {
				value = Double.parseDouble(getNextLine().get());
				Attribute attribute = new Attribute(ChromosomeType.fromString(this.chr), this.position, getType(),
						value);
				this.position += this.end;
				setNextLine(Optional.empty());
				return attribute;
			} else if (variableStep) {
				String[] split = getNextLine().get().split("\t");
				value = Double.parseDouble(split[1]);
				if (this.position == Integer.MIN_VALUE) {
					this.position = Integer.parseInt(split[0]);
					this.end = this.position + this.span;
				}
				if (this.end > this.position) {
					Attribute attribute = new Attribute(ChromosomeType.fromString(this.chr), this.position, getType(),
							value);
					this.position++;
					return attribute;
				} else {
					this.position = Integer.MIN_VALUE;
					setNextLine(Optional.empty());
				}
			} else {
				String[] split = getNextLine().get().split("\t");
				value = Double.parseDouble(split[3]);
				end = Integer.parseInt(split[2]);
				if (this.end > this.position) {
					Attribute attribute = new Attribute(ChromosomeType.fromString(this.chr), this.position, getType(),
							value);
					this.position++;
					return attribute;
				} else {
					setNextLine(Optional.empty());
				}
			}

		}
		return null;

	}
}
