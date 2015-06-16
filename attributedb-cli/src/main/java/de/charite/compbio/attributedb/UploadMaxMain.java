package de.charite.compbio.attributedb;

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
import de.charite.compbio.attributedb.model.score.Attribute;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class UploadMaxMain extends UploadMain {

	private static List<ScoreReader> readers;
	private static List<Attribute> scores;

	public static void main(String[] args) throws SQLException, ParseException {
		UploadSettings.parseArgs(args);

		Connection con = DatabaseConnection.getConnection();
		int i = 0;
		try {
			con.setAutoCommit(false);
			// AttributeType. get ID
			setAttributeType(con);

			readers = new ArrayList<>();
			scores = new ArrayList<>();
			for (String file : UploadSettings.FILES) {
				ScoreReader reader = new ScoreReaderBuilder().setFileType(UploadSettings.FILE_TYPE)
						.setAttributeType(attributeType).setScoreColumn(UploadSettings.SCORE_COLUMN)
						.setFiles(Arrays.<String> asList(file)).create();
				readers.add(reader);
				if (reader.hasNext())
					scores.add(reader.next());
				else
					scores.add(null);
			}

			PreparedStatement ps = con.prepareStatement(Attribute.INSERT_STATEMENT);
			while (hasNext()) {

				// get max
				Attribute maxScore = null;
				List<Integer> index = new ArrayList<>();
				for (int j = 0; j < readers.size(); j++) {
					if (readers.get(j).hasNext() && scores.get(j) == null)
						scores.set(j, readers.get(j).next());
					Attribute theScore = scores.get(j);
					if (theScore == null)
						continue;
					if (maxScore == null) {
						maxScore = theScore;
						index = new ArrayList<>();
						index.add(j);
					} else if (theScore.getChr() == maxScore.getChr()
							&& theScore.getPosition() < maxScore.getPosition()) {
						maxScore = theScore;
						index = new ArrayList<>();
						index.add(j);
					} else if (theScore.getChr() == maxScore.getChr()
							&& theScore.getPosition() == maxScore.getPosition()) {
						index.add(j); // same position
						if (theScore.getValue() > maxScore.getValue())
							maxScore = theScore;
					}
				}

				// Upload
				if (!UploadSettings.UPLOAD_ZERO && maxScore.getValue() == 0.0)
					continue;// do not upload 0 if set
				maxScore.setPrepareStatement(ps);
				ps.addBatch();

				i++;
				// write if necessary
				if (i % 1000000 == 0) {
					ps.executeBatch();
					System.out.println(i + " positions uploaded!");
				}

				// get next scores for actual position.
				for (Integer j : index) {
					if (readers.get(j).hasNext())
						scores.set(j, readers.get(j).next());
					else
						scores.set(j, null);
				}

			}
			ps.executeBatch();
			con.commit();
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

	private static boolean hasNext() {
		for (ScoreReader scoreReader : readers) {
			if (scoreReader.hasNext())
				return true;
		}
		return false;
	}
}
