package de.quinscape.jrsfx.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class Configuration
		extends Properties {
	private static final long serialVersionUID = -654941593246990751L;
	public static final String DEFAULT_CFG = "config/cfg.properties";
	public static final String XML = "xml";

	/**
	 * @param locationAndName
	 *            Classpath relative path to the properties.
	 * @param format
	 *            Case ignored, either XML or null for default .properties
	 * @throws InvalidPropertiesFormatException
	 *             If the specified <code>format</code> does not match the
	 *             actual one.
	 * @throws IOException
	 *             If the file could not be loaded.
	 */
	public Configuration(String locationAndName, String format) throws IOException {
		InputStream is = Configuration.class.getClassLoader().getResourceAsStream((locationAndName == null)
				? DEFAULT_CFG : locationAndName);
		if (format != null && format.toLowerCase().contains(XML) && is != null) {
			this.loadFromXML(is);
		}
		else if (is != null) {
			this.load(is);
		}
		else {
			throw new IOException("File not found: " + locationAndName);
		}
		if (is != null) {
			is.close();
		}
		is = Configuration.class.getClassLoader().getResourceAsStream(DEFAULT_CFG);
		this.defaults = new Properties();
		this.defaults.load(is);
		if (is != null) {
			is.close();
		}
	}

}
