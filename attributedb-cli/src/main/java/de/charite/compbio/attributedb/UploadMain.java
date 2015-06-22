package de.charite.compbio.attributedb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.cli.ParseException;

import de.charite.compbio.attributedb.cli.UploadSettings;
import de.charite.compbio.attributedb.db.DatabaseConnection;
import de.charite.compbio.attributedb.io.ScoreReader;
import de.charite.compbio.attributedb.io.ScoreReaderBuilder;
import de.charite.compbio.attributedb.model.score.Attribute;
import de.charite.compbio.attributedb.model.score.AttributeType;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class UploadMain {

	protected static AttributeType attributeType;

	/**
	 * @param args
	 * @throws SQLException
	 * @throws ParseException
	 */
	public static void main(String[] args) throws SQLException, ParseException {
		UploadSettings.parseArgs(args);

		Connection con = DatabaseConnection.getConnection();
		int i = 0;
		try {
			con.setAutoCommit(false);
			// AttributeType. get ID
			setAttributeType(con);

			ScoreReader reader = new ScoreReaderBuilder().setFileType(UploadSettings.FILE_TYPE)
					.setAttributeType(attributeType).setScoreColumn(UploadSettings.SCORE_COLUMN)
					.setFiles(UploadSettings.FILES).create();

			PreparedStatement ps = con.prepareStatement(Attribute.INSERT_STATEMENT);
			Attribute score = null;
			while (reader.hasNext()) {
				score = reader.next();
				if (score == null)
					continue; // can be zero in wig files or GERP elements
								// because of steps or intervals
				if (!UploadSettings.UPLOAD_ZERO && score.getValue() == 0.0)
					continue;// do not upload 0 if set

				score.setPrepareStatement(ps);
				ps.addBatch();

				i++;
				if (i % 1000000 == 0) {
					ps.executeBatch();
					System.out.println(i + " positions uploaded!");
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

	protected static void setAttributeType(Connection con) throws SQLException {

		attributeType = UploadSettings.ATTRIBUTE_TYPE;

		PreparedStatement ps = con.prepareStatement(AttributeType.INSERT_STATEMENT);
		ps.setString(1, attributeType.getName());
		ps.setString(2, attributeType.getDescription());
		ResultSet rs = ps.executeQuery();
		rs.next();
		attributeType.setId(rs.getInt(1));
	}

}
