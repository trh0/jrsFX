package de.quinscape.jrsfx.jasper;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(
		name = "resources")
@XmlAccessorType(XmlAccessType.FIELD)
public class RepositoryResourceWrapper {
	@XmlElement(
			name = "resourceLookup")
	private final ArrayList<RepositoryResource> resources;

	public RepositoryResourceWrapper() {
		this.resources = new ArrayList<>();
	}

	public List<RepositoryResource> asList() {
		return this.resources;
	}
}
