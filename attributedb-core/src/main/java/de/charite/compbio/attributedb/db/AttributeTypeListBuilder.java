package de.charite.compbio.attributedb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.charite.compbio.attributedb.model.score.AttributeType;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class AttributeTypeListBuilder {

	public AttributeTypeListBuilder() {
	}

	private Connection connection;
	private String nameLike = "%";
	private String nameILike = "%";

	private final static String SELECT_SQL = "SELECT id, name, description "
			+ "FROM attribute_type WHERE name LIKE ? AND name ILIKE ?";

	public AttributeTypeListBuilder setConnection(Connection connection) {
		this.connection = connection;
		return this;
	}

	public AttributeTypeListBuilder setNameLike(String nameLike) {
		this.nameLike = nameLike;
		return this;
	}

	public AttributeTypeListBuilder setNameILike(String nameILike) {
		this.nameILike = nameILike;
		return this;
	}

	public List<AttributeType> create() throws SQLException {
		List<AttributeType> output = new ArrayList<AttributeType>();

		PreparedStatement ps = connection.prepareStatement(SELECT_SQL);
		ps.setString(1, nameLike);
		ps.setString(2, nameILike);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			AttributeType at = new AttributeType(rs.getString(2), rs.getString(3));
			at.setId(rs.getInt(1));
			output.add(at);
		}

		return output;
	}

}
