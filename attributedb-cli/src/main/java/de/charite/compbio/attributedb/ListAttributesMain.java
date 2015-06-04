package de.charite.compbio.attributedb;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.cli.ParseException;

import de.charite.compbio.attributedb.cli.ListAttributesSetting;
import de.charite.compbio.attributedb.db.AttributeTypeListBuilder;
import de.charite.compbio.attributedb.db.DatabaseConnection;
import de.charite.compbio.attributedb.model.score.AttributeType;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class ListAttributesMain {

	public static void main(String[] args) throws ParseException, SQLException {
		ListAttributesSetting.parseArgs(args);

		Connection con = DatabaseConnection.getConnection();
		try {

			List<AttributeType> types = new AttributeTypeListBuilder().setConnection(con)
					.setNameILike(ListAttributesSetting.NAME_ILIKE).setNameLike(ListAttributesSetting.NAME_LIKE)
					.create();
			
			String format = "%s\t%-20s\t%s%n";
			System.out.printf(format, "ID", "NAME", "DESCRIPTION");
			format = "%d\t%-20s\t%s%n";
			for (AttributeType attributeType : types) {
				System.out.printf(format, attributeType.getId(), attributeType.getName(), attributeType.getDescription());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		con.close();
		System.exit(0);
	}

}
