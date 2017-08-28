package de.quinscape.jrsfx.jasper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.DateTime;

import de.quinscape.jrsfx.data.DateTimeAdapter;

@XmlRootElement(
		name = "resourceLookup")
@XmlAccessorType(XmlAccessType.FIELD)
public class RepositoryResource {

	public RepositoryResource(DateTime creationDate, DateTime updateDate, String description, String label,
			int permissionMask, String uri, String type) {
		super();
		this.creationDate = creationDate;
		this.updateDate = updateDate;
		this.description = description;
		this.label = label;
		this.permissionMask = permissionMask;
		this.uri = uri;
		this.resourceType = type;
	}

	/**
	 * Jaxb Constructor
	 */
	@SuppressWarnings("unused")
	private RepositoryResource() {
		super();
		this.creationDate = null;
		this.updateDate = null;
		this.description = null;
		this.label = null;
		this.permissionMask = -1;
		this.uri = null;
		this.resourceType = null;
	}

	public static final String REPORT_UNIT = "reportUnit";
	public static final String FILE = "file";
	public static final String DASHBOARD = "dashboard";
	public static final String FOLDER = "folder";
	@XmlElement
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private final DateTime creationDate;
	@XmlElement
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private final DateTime updateDate;
	@XmlElement
	private final String description;
	@XmlElement
	private final String label;
	@XmlElement
	private final int permissionMask;
	@XmlElement
	private final String uri;
	@XmlElement
	private final String resourceType;

	public DateTime getCreationDate() {
		return creationDate;
	}

	public DateTime getUpdateDate() {
		return updateDate;
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}

	public int getPermissionMask() {
		return permissionMask;
	}

	public String getUri() {
		return uri;
	}

	public String getResourceType() {
		return resourceType;
	}

	@Override
	public String toString() {
		return "RepositoryResource [creationDate=" + creationDate + ", updateDate=" + updateDate + ", description="
				+ description + ", label=" + label + ", permissionMask=" + permissionMask + ", uri=" + uri
				+ ", resourceType=" + resourceType + "]";
	}
}
