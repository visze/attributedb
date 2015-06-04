package de.charite.compbio.attributedb.model.score;

/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class AttributeType {
	
	public static final String INSERT_STATEMENT = "INSERT INTO attribute_type "
			+ "(name, description) VALUES (?,?) RETURNING id";
	
	private String name;
	private String description;
	private int id;
	
	public AttributeType(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setId(int id) {
		this.id = id;
	}

}
