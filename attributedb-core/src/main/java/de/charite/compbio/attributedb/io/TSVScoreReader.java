package de.charite.compbio.attributedb.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Splitter;

import de.charite.compbio.attributedb.model.score.Attribute;
import de.charite.compbio.attributedb.model.score.AttributeType;
import de.charite.compbio.attributedb.model.score.ChromosomeType;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class TSVScoreReader extends ScoreReader {

	public TSVScoreReader(List<String> files, AttributeType type) throws IOException {
		super(files, type);
	}

	@Override
	public Attribute next() {
		if (hasNext()) {
			List<String> split = new ArrayList<>();
			for (String string : Splitter.on('\t').trimResults().omitEmptyStrings().split(getNextLine())) {
				split.add(string);
			}
			Attribute attribute = new Attribute(ChromosomeType.fromString(split.get(0)),
					Integer.parseInt(split.get(1)), getType(), Double.parseDouble(split.get(2)));
			setNextLine(null);
			return attribute;
		}
		return null;
	}
}
