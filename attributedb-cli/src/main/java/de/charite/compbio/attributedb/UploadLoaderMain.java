package de.charite.compbio.attributedb;

import java.io.IOException;
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
public class UploadLoaderMain {

	private static AttributeType attributeType;

	public static void main(String[] args) throws SQLException, ParseException, IOException {
		UploadSettings.parseArgs(args);

		attributeType = UploadSettings.ATTRIBUTE_TYPE;

		Connection con = DatabaseConnection.getConnection();
		int i = 0;
		try {
			con.setAutoCommit(false);
			// AttributeType. get ID
			PreparedStatement ps = con.prepareStatement(AttributeType.INSERT_STATEMENT);
			ps.setString(1, attributeType.getName());
			ps.setString(2, attributeType.getDescription());
			ResultSet rs = ps.executeQuery();
			rs.next();
			attributeType.setId(rs.getInt(1));

			ScoreReader reader = new ScoreReaderBuilder().setFileType(UploadSettings.FILE_TYPE)
					.setAttributeType(attributeType).setFiles(UploadSettings.FILES).create();

			ps = con.prepareStatement(Attribute.INSERT_STATEMENT);
			Attribute score = null;
			Attribute scoreBefore = null;
			while (reader.hasNext()) {
				scoreBefore = score;
				score = reader.next();
				
				if (scoreBefore != null && scoreBefore.getChr() != score.getChr())
					ps.executeBatch();

				score.setPrepareStatement(ps);
				ps.addBatch();

				i++;
				// if (i % 1000000 == 0) {
				// ps.executeLargeBatch();
				// System.out.println(i + " positions uploaded!");
				// ps = con.prepareStatement(Attribute.INSERT_STATEMENT);
				// }
			}
			ps.executeBatch();
			con.commit();
			System.out.println(i + " scores uploaded!");
			System.out.println("Upload complete");
		} catch (Exception e) {
			e.printStackTrace();
			con.rollback();
			System.exit(1);
		}
		con.close();
		System.exit(0);
	}

}
