package de.charite.compbio.attributedb.io;

import java.io.IOException;
import java.util.List;

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

	public ScoreReader create() throws IOException {

		switch (fileType) {
		case WIG:
			return new WigFileReader(files, attributeType);
		case TSV:
			return new TSVScoreReader(files, attributeType);

		default:
			return new TSVScoreReader(files, attributeType);
		}
	}

}