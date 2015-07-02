package de.charite.compbio.attributedb.model;

import java.io.IOException;

import de.charite.compbio.attributedb.io.ScoreReader;
import de.charite.compbio.attributedb.model.score.Attribute;

public class ScoreIterator extends AScoreIterator {
	
	private ScoreReader reader;
	
	public ScoreIterator(ScoreReader reader) {
		super();
		this.reader = reader;
		initScore();
	}
	
	public ScoreIterator(ScoreReader reader, String positions) throws IOException {
		super(positions);
		this.reader = reader;
		initScore();
	}
	

	public Attribute nextScore() {
		Attribute score, output = null;
		while (getReader().hasNext()) {
			score = getReader().next();
			if (score == null || !containsPosition(score))
				continue; // can be zero in wig files or GERP elements
							// because of steps or intervals
			if (!isUploadZeros() && score.getValue() == 0.0)
				continue;// do not upload 0 if set
			output = score;
			break;
		}
		return output;
	}
	
	public ScoreReader getReader() {
		return reader;
	}
	
	public void setReader(ScoreReader reader) {
		this.reader = reader;
	}

	
}
