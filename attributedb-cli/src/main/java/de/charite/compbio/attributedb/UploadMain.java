package de.charite.compbio.attributedb;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import org.apache.commons.cli.ParseException;

import de.charite.compbio.attributedb.cli.UploadSettings;
import de.charite.compbio.attributedb.db.DatabaseConnection;
import de.charite.compbio.attributedb.io.ScoreReader;
import de.charite.compbio.attributedb.io.ScoreReaderBuilder;
import de.charite.compbio.attributedb.model.ScoreIterator;
import de.charite.compbio.attributedb.model.score.Attribute;
import de.charite.compbio.attributedb.model.score.AttributeType;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class UploadMain {

	protected static AttributeType attributeType;
	protected static Set<Long> positions;

	/**
	 * @param args
	 * @throws SQLException
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void main(String[] args) throws SQLException, ParseException, IOException {
		UploadSettings.parseArgs(args);

		ScoreIterator iterator;

		ScoreReader reader = new ScoreReaderBuilder().setFileType(UploadSettings.FILE_TYPE)
				.setAttributeType(attributeType).setScoreColumn(UploadSettings.SCORE_COLUMN)
				.setFiles(UploadSettings.FILES).create();

		if (UploadSettings.POSITION_FILE != null)
			iterator = new ScoreIterator(reader, UploadSettings.POSITION_FILE);
		else
			iterator = new ScoreIterator(reader);

		Connection con = DatabaseConnection.getConnection();
		int i = 0;
		try {
			con.setAutoCommit(true);
			// AttributeType. get ID
			setAttributeType(con);

			PreparedStatement ps = con.prepareStatement(Attribute.INSERT_STATEMENT);
			Attribute score = null;
			while (iterator.hasNext()) {

				score = iterator.next();

				score.setPrepareStatement(ps);
				ps.addBatch();

				i++;
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
			// con.rollback();
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
		ps.executeUpdate();
		ps = con.prepareStatement(AttributeType.SELECT_NAME_STATEMENT);
		ps.setString(1, attributeType.getName());
		ResultSet rs = ps.executeQuery();
		rs.next();
		attributeType.setId(rs.getInt(1));
	}

}
