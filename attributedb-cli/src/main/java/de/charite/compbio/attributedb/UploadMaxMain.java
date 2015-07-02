package de.charite.compbio.attributedb;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.ParseException;

import de.charite.compbio.attributedb.cli.UploadSettings;
import de.charite.compbio.attributedb.db.DatabaseConnection;
import de.charite.compbio.attributedb.io.ScoreReader;
import de.charite.compbio.attributedb.io.ScoreReaderBuilder;
import de.charite.compbio.attributedb.model.MaxScoreIterator;
import de.charite.compbio.attributedb.model.score.Attribute;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class UploadMaxMain extends UploadMain {

	public static void main(String[] args) throws SQLException, ParseException, IOException {
		UploadSettings.parseArgs(args);
		
		List<ScoreReader> readers = new ArrayList<ScoreReader>();
		for (String file : UploadSettings.FILES) {
			ScoreReader reader = new ScoreReaderBuilder().setFileType(UploadSettings.FILE_TYPE)
					.setAttributeType(attributeType).setScoreColumn(UploadSettings.SCORE_COLUMN)
					.setFiles(Arrays.<String> asList(file)).create();

			readers.add(reader);
		}

		MaxScoreIterator iterator;
		if (UploadSettings.POSITION_FILE != null)
			iterator = new MaxScoreIterator(readers, UploadSettings.POSITION_FILE);
		else
			iterator = new MaxScoreIterator(readers);
		
		iterator.setUploadZeros(UploadSettings.UPLOAD_ZERO);

		Connection con = DatabaseConnection.getConnection();
		int i = 0;
		try {
			con.setAutoCommit(true);
			// AttributeType. get ID
			setAttributeType(con);

			PreparedStatement ps = con.prepareStatement(Attribute.INSERT_STATEMENT);
			Attribute maxScore;
			while (iterator.hasNext()) {

				maxScore = iterator.next();
				maxScore.setPrepareStatement(ps);
				ps.addBatch();

				i++;
				// write if necessary
				if (i % (positions == null || positions.isEmpty() ? 100000000 : positions.size() / 10) == 0) {
					ps.executeBatch();
					System.out.println(i + " positions uploaded!");
				}

			}
			ps.executeBatch();
			System.out.println(i + " scores uploaded!");
			System.out.println("Upload complete");
		} catch (Exception e) {
			e.printStackTrace();
			con.rollback();
			con.close();
			System.exit(1);
		}
		con.close();
		System.exit(0);
	}

}
