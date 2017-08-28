package de.quinscape.jrsfx.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.quinscape.jrsfx.data.Mapping;
import javafx.scene.control.TreeItem;

/**
 * Utility-class for handling various problems with
 * {@linkplain ResourceBundle}s.<br>
 * It also contains a Bundle for the application.
 * 
 * @author Tobias Koll - QuinScape GmbH
 */
public class Messages {

	private static ResourceBundle bundle = null;

	public static ResourceBundle appBundle() {
		if (bundle == null) {
			try {
				init();
			}
			catch (Exception e) {
				ApplicationIO.toErrorStream(e);
				bundle = new ResourceBundle() {
					@Override
					protected Object handleGetObject(String key) {
						return null;
					}

					@Override
					public Enumeration<String> getKeys() {
						return null;
					}
				};
			}
		}
		return bundle;
	}

	private static void init() throws IOException {
		InputStream in = Messages.class.getClassLoader().getResourceAsStream("fxml/messages.properties");
		if (in != null) {
			bundle = new PropertyResourceBundle(in);
			in.close();
		}
	}

	/**
	 * @author trh0 - TKoll
	 * @param basePath
	 *            Valid path to a bundle.
	 * @return A {@linkplain de.quinscape.MessageEditor.data.Mapping} containing
	 *         the bundleBaseName and the bundle, if loadable.
	 */
	public static Mapping<String, ResourceBundle> loadBundle(String basePath) {
		ResourceBundle curBundle = null;
		String bundleBaseName = null;
		Mapping<String, ResourceBundle> mapping = null;
		if (basePath == null || basePath.isEmpty()) {
			ApplicationIO.toErrorStream("Given path is null or empty.");
			return mapping;
		}
		File f = new File(basePath);
		try (InputStream in = Files.newInputStream(f.toPath())) {
			curBundle = new PropertyResourceBundle(in);
			bundleBaseName = curBundle.getBaseBundleName();
			bundleBaseName = (bundleBaseName == null) ? f.getName().replaceAll(
					"(?![a-zA-Z]+)((_|-)[A-Za-z]{2}){1,2}(?=\\.properties)", "") : bundleBaseName;
		}
		catch (Exception e) {
			ApplicationIO.toErrorStream("Error reading ResourceBundle. File exists? " + f.exists() + ", can read file? "
					+ f.canRead(), e);
			return mapping;
		}
		mapping = new Mapping<>(bundleBaseName, curBundle);
		return mapping;
	}

	/**
	 * Using regex for recognizing the bundle keys and putting them into a
	 * {@linkplain TreeItem}
	 * 
	 * @author trh0 - TKoll
	 *
	 * @param theBundle
	 *            A bundle to display the keys in a TreeView from.
	 * @param rootName
	 *            The bundleBaseName (will be used to name the root object). May
	 *            be null or empty.
	 * @param sepChar
	 *            The seperator_char used to seperate bundleKeys.
	 * @return A {@linkplain TreeItem} containing the bundle-key-hierarchiy.<br>
	 */
	public static TreeItem<String> bundleToTreeView(ResourceBundle theBundle, String rootName, String sepChar) {
		if (theBundle == null) {
			ApplicationIO.toErrorStream("No bundle provided to generate TreeItem-structure.");
			return null;
		}
		else if (sepChar == null || sepChar.isEmpty()) {
			sepChar = ".";
			ApplicationIO.toErrorStream("No seperator char provided - using default '.'.");
		}
		if (rootName == null || rootName.isEmpty()) {
			rootName = theBundle.getBaseBundleName();
			rootName = (rootName == null) ? "---" : rootName;
		}

		TreeItem<String> root = new TreeItem<>(rootName);
		TreeItem<String> tmp = null;
		String keyPattern = "[a-zA-Z%s0-9]+((?=%s)|(?!%s))";
		String cons = "\\+\\-\\_\\.\\*";
		sepChar = "\\" + sepChar;
		cons = cons.replace(sepChar, "");
		keyPattern = String.format(keyPattern, cons, sepChar, sepChar);
		Pattern p = Pattern.compile(keyPattern);
		sepChar = sepChar.replace("\\", "");
		Set<String> keys = theBundle.keySet();

		Matcher m;

		for (String k : keys) {
			m = p.matcher(k);
			tmp = root;
			while (m.find()) {
				String match = m.group();
				TreeItem<String> itm = chldForvalue(match, tmp);
				if (itm == null) {
					itm = new TreeItem<String>(match);
					tmp.getChildren().add(itm);
				}
				tmp = itm;
			}
		}
		return root;
	}

	/**
	 * Finds a {@linkplain TreeItem} that is a child of the proviced
	 * <code>itm</code> with a certain <code>value</code>ue.
	 * 
	 * @author trh0 - TKoll
	 *
	 * @param value
	 *            The value to be contained by the itm.
	 * @param itm
	 *            The itm to look in.
	 * @return Either null if not found or a param is null, or the
	 *         {@linkplain TreeItem}
	 */
	public static TreeItem<String> chldForvalue(String value, TreeItem<String> itm) {
		TreeItem<String> res = null;
		if (itm != null && value != null) {
			res = (itm.getValue().equals(value)) ? itm : res;
			if (res == null) {
				for (TreeItem<String> it : itm.getChildren()) {
					if (value.equals(it.getValue())) {
						res = it;
						break;
					}
				}
			}
		}
		return res;
	}
}
