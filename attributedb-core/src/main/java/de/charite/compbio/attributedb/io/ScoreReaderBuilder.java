package de.charite.compbio.attributedb.io;

import java.io.IOException;
import java.util.List;

import de.charite.compbio.attributedb.io.gerp.PValueReader;
import de.charite.compbio.attributedb.io.gerp.RSScoreReader;
import de.charite.compbio.attributedb.model.score.AttributeType;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class ScoreReaderBuilder {

	public ScoreReaderBuilder() {
	}

	private AttributeType attributeType;
	private List<String> files;
	private FileType fileType;
	private int scoreColumn;

	public ScoreReaderBuilder setFileType(FileType fileType) {
		this.fileType = fileType;
		return this;
	}

	public ScoreReaderBuilder setAttributeType(AttributeType attributeType) {
		this.attributeType = attributeType;
		return this;
	}

	public ScoreReaderBuilder setFiles(List<String> files) {
		this.files = files;
		return this;
	}
	
	public ScoreReaderBuilder setScoreColumn(int column) {
		this.scoreColumn = column;
		return this;
	}

	public ScoreReader create() throws IOException {

		switch (this.fileType) {
		case WIG:
			return new WigFileReader(this.files, this.attributeType);
		case TSV:
			return new TSVScoreReader(this.files, this.attributeType, this.scoreColumn);
		case GERP_RS_SCORE:
			return new RSScoreReader(this.files, this.attributeType);
		case GERP_RS_P_VALUE:
			return new PValueReader(this.files, this.attributeType);
		case BED:
			return new BEDScoreReader(this.files, this.attributeType, this.scoreColumn);
		default:
			return new TSVScoreReader(this.files, this.attributeType, 3);
		}
	}

}
