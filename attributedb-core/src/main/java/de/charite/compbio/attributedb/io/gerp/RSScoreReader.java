package de.charite.compbio.attributedb.io.gerp;

import java.io.IOException;
import java.util.List;

import de.charite.compbio.attributedb.model.score.AttributeType;

/**
 * Reader of the GERP++ RS score. This is a really specific reader. It only works with the <a
 * href="http://mendel.stanford.edu/SidowLab/downloads/gerp/hg19.GERP_elements.tar.gz">elements file</a> of the gerp
 * website.
 * 
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 * @see <a
 *      href="http://mendel.stanford.edu/SidowLab/downloads/gerp/Readme.txt">http://mendel.stanford.edu/SidowLab/downloads/gerp/Readme.txt</a>
 * @see <a
 *      href="http://mendel.stanford.edu/SidowLab/downloads/gerp/">http://mendel.stanford.edu/SidowLab/downloads/gerp/</a>
 *
 */
public class RSScoreReader extends GERPElementsFileReader {

	/**
	 * Constructor. Sets the position of the scor in the split array to 3.
	 * 
	 * @param files {@link List} with paths to the files (but only one element-file on the GERP website) 
	 * @param type The {@link AttributeType} of the score you upload (something like gerprsscore).
	 * @throws IOException
	 */
	public RSScoreReader(List<String> files, AttributeType type) throws IOException {
		super(files, type);
		setSplitPosition(3);
	}

}
