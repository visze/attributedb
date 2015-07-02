package de.charite.compbio.attributedb.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import de.charite.compbio.attributedb.model.score.Attribute;
import de.charite.compbio.attributedb.model.score.ChromosomeType;
import de.charite.compbio.attributedb.model.score.Position;

public abstract class AScoreIterator implements IScoreIterator {

	private static Set<Long> positions;
	protected Attribute score;
	private boolean uploadZeros;

	public AScoreIterator() {
	}

	public AScoreIterator(String positions) throws IOException {
		loadPositions(positions);
	}

	@Override
	public boolean hasNext() {
		if (this.score == null)
			score = nextScore();
		return this.score != null;
	}

	@Override
	public Attribute next() {
		Attribute score = this.score;
		this.score = nextScore();
		return score;
	}

	protected boolean containsPosition(Attribute score) {
		if (positions == null)
			return true;
		return positions.contains(score.getDatabasePosition());
	}

	private void loadPositions(String pOSITION_FILE) throws IOException {
		if (positions != null)
			return;
		positions = new HashSet<Long>();
		File f = new File(pOSITION_FILE);
		FileReader fin = new FileReader(f);
		BufferedReader reader = new BufferedReader(fin);
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] split = line.split("\t");
			Position pos = new Position(ChromosomeType.fromString(split[0]), Integer.parseInt(split[1]));
			positions.add(pos.getDatabasePosition());
		}
		reader.close();
	}

	public void setUploadZeros(boolean uploadZeros) {
		this.uploadZeros = uploadZeros;
	}

	public boolean isUploadZeros() {
		return this.uploadZeros;
	}

	protected void initScore() {
		this.score = nextScore();
	}

}
