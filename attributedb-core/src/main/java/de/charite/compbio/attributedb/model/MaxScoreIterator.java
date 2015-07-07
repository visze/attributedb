package de.charite.compbio.attributedb.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.charite.compbio.attributedb.io.ScoreReader;
import de.charite.compbio.attributedb.model.score.Attribute;

public class MaxScoreIterator extends AScoreIterator {

	private List<ScoreReader> readers;
	private List<Attribute> scores;

	public MaxScoreIterator(List<ScoreReader> readers) {
		super();
		this.readers = readers;
		initScores();
		initScore();
	}

	public MaxScoreIterator(List<ScoreReader> readers, String positions) throws IOException {
		super(positions);
		this.readers = readers;
		initScores();
		initScore();
	}

	private void initScores() {
		for (ScoreReader reader : getReaders()) {
			if (reader.hasNext())
				getScores().add(reader.next());
			else
				getScores().add(null);
		}

	}

	public Attribute nextScore() {

		// get max
		Attribute maxScore, output = null;
		List<Integer> index;
		while (hasNextScore()) {
			index = new ArrayList<>();
			maxScore = null;
			// iterate over all readers and find the next and the max score;
			for (int j = 0; j < getReaders().size(); j++) {

				if (getReaders().get(j).hasNext() && getScores().get(j) == null)
					getScores().set(j, getReaders().get(j).next());

				Attribute theScore = getScores().get(j);

				if (theScore == null)
					continue;
				if (maxScore == null) {
					maxScore = theScore;
					index.add(j);
				} else if (theScore.getChr().getOrder() <= maxScore.getChr().getOrder()) {
					maxScore = theScore; // lower position
					index = new ArrayList<>();
					index.add(j);
				} else if (theScore.getChr() == maxScore.getChr() && theScore.getPosition() < maxScore.getPosition()) {
					maxScore = theScore; // lower position
					index = new ArrayList<>();
					index.add(j);
				} else if (theScore.getChr() == maxScore.getChr() && theScore.getPosition() == maxScore.getPosition()) {
					index.add(j); // same position
					if (theScore.getValue() > maxScore.getValue())
						maxScore = theScore;
				}
			}

			// get next scores for actual position.
			for (Integer j : index) {
				if (getReaders().get(j).hasNext())
					getScores().set(j, getReaders().get(j).next());
				else
					getScores().set(j, null);
			}

			if (maxScore == null || !containsPosition(maxScore))
				continue; // can be zero in wig files or GERP elements
							// because of steps or intervals
			// Upload
			if (!isUploadZeros() && maxScore.getValue() == 0.0)
				continue;// do not upload 0 if set

			output = maxScore;
			break;
		}
		return output;
	}

	private boolean hasNextScore() {
		for (ScoreReader reader : getReaders()) {
			if (reader.hasNext())
				return true;
		}
		return false;
	}

	public List<ScoreReader> getReaders() {
		if (readers == null)
			readers = new ArrayList<>();
		return readers;
	}

	public List<Attribute> getScores() {
		if (scores == null)
			scores = new ArrayList<>();
		return scores;
	}
	
}
