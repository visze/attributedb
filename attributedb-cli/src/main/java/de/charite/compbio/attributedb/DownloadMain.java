package de.charite.compbio.attributedb;

import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.ParseException;

import de.charite.compbio.attributedb.cli.DownloadSettings;
import de.charite.compbio.attributedb.db.DatabaseConnection;
import de.charite.compbio.attributedb.io.ScorePrinter;
import de.charite.compbio.attributedb.model.score.Attribute;
import de.charite.compbio.attributedb.model.score.AttributeType;
import de.charite.compbio.attributedb.model.score.ChromosomeType;
import de.charite.compbio.attributedb.model.score.Position;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class DownloadMain {

	private static List<AttributeType> types;

	public static void main(String[] args) throws SQLException, ParseException, IOException {
		DownloadSettings.parseArgs(args);

		types = new ArrayList<AttributeType>();

		Connection con = DatabaseConnection.getConnection();
		try {
			// AttributeTypes. get ID

			if (!DownloadSettings.ATTRIBUTE_TYPES.isEmpty()) {
				PreparedStatement ps = con.prepareStatement(AttributeType.SELECT_NAME_STATEMENT);
				for (AttributeType type : DownloadSettings.ATTRIBUTE_TYPES) {
					ps.setString(1, type.getName());
					ResultSet rs = ps.executeQuery();
					if (rs.next()) {
						type.set(rs);
						types.add(type);
					}
				}

			} else {
				PreparedStatement ps = con.prepareStatement(AttributeType.SELECT_STATEMENT);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					AttributeType type = new AttributeType();
					type.set(rs);
					types.add(type);
				}

			}

			ScorePrinter printer = new ScorePrinter();
			printer.writeHeader(types);

			PreparedStatement ps = con.prepareStatement(Attribute.SELECT_POSITION_TYPEID_STATEMENT);

			// positions
			if (!DownloadSettings.POSITIONS.isEmpty()) {
				for (String positionString : DownloadSettings.POSITIONS) {
					String[] split = positionString.split(":");

					List<Attribute> scores = getScores(ps, ChromosomeType.fromString(split[0]),
							Integer.parseInt(split[1]));
					printer.writeScores(scores);
				}

			} else { // VCF
				for (String file : DownloadSettings.VCF_FILES) {
					VCFFileReader fr = new VCFFileReader(new File(file));
					for (VariantContext vc : fr) {
						List<Attribute> scores = getScores(ps, ChromosomeType.fromString(vc.getChr()), vc.getStart());
						printer.writeScores(scores);

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			con.close();
			System.exit(1);
		}
		con.close();
		System.exit(0);
	}

	protected static List<Attribute> getScores(PreparedStatement ps, ChromosomeType chr, int position)
			throws SQLException {
		List<Attribute> scores = new ArrayList<Attribute>();
		Position pos = new Position(chr, position);
		for (AttributeType type : types) {

			ps.setLong(1, pos.getDatabasePosition());
			ps.setInt(2, type.getId());
			ResultSet rs = ps.executeQuery();

			Attribute score = new Attribute(pos.getChr(), pos.getPosition(), type, Double.NaN);
			if (rs.next())
				score.set(rs);

			scores.add(score);
		}
		return scores;
	}

}
