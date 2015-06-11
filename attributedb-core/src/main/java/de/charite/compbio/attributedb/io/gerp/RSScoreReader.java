package de.charite.compbio.attributedb.io.gerp;

import java.io.IOException;
import java.util.List;

import de.charite.compbio.attributedb.model.score.AttributeType;

public class RSScoreReader extends GERPElementsFileReader{

	public RSScoreReader(List<String> files, AttributeType type) throws IOException {
		super(files, type);
		setSplitPosition(3);
	}

}
