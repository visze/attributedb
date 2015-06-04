package de.charite.compbio.attributedb.model.score;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class Attribute extends Position {

	public static final String INSERT_STATEMENT = "INSERT INTO attribute "
			+ "(position, attribute_type_id, value) VALUES (?,?,?)";
	private AttributeType type;
	private double value;

	public Attribute(ChromosomeType chr, int position, AttributeType type, double value) {
		super(chr, position);
		this.type = type;
		this.value = value;
	}

	public void setPrepareStatement(PreparedStatement ps) throws SQLException {
		ps.setLong(1, getDatabasePosition());
		ps.setInt(2, getType().getId());
		ps.setDouble(3, getValue());
	}

	public AttributeType getType() {
		return type;
	}

	public double getValue() {
		return value;
	}

}
