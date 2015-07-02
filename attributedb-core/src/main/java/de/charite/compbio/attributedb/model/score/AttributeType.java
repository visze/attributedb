package de.charite.compbio.attributedb.model.score;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class AttributeType {
	
	public static final String INSERT_STATEMENT = "INSERT INTO attribute_type "
			+ "(name, description) VALUES (?,?)";
	public static final String SELECT_NAME_STATEMENT = "SELECT id, name, description FROM attribute_type "
			+ "WHERE name = ?";
	public static final String SELECT_STATEMENT = "SELECT id, name, description FROM attribute_type";
	
	private String name;
	private String description;
	private int id;
	
	public AttributeType(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	
	public AttributeType() {
	}


	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}


	public void set(ResultSet rs) throws SQLException {
		setId(rs.getInt(1));
		setName(rs.getString(2));
		setDescription(rs.getString(3));
	}

}
