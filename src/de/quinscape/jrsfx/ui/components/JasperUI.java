package de.quinscape.jrsfx.ui.components;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.quinscape.jrsfx.controller.StaticBase;
import de.quinscape.jrsfx.jasper.RepoTreeResource;
import de.quinscape.jrsfx.ui.Images;
import javafx.scene.control.TreeItem;

public class JasperUI {
	private JasperUI() {}

	public static TreeItem<String> repositoryTreeItem(List<RepoTreeResource> resources) {
		TreeItem<String> rootItem = new TreeItem<>(StaticBase.instance().getConfig().getProperty("jrs.url"), Images.HOST
				.getImageView(16, 16));
		Pattern pattern = Pattern.compile("\\/[^/\\s]+(?=\\/)");
		if (resources != null)
			resources.forEach(r -> {
				String uri = r.getUri();
				String group = null;
				Matcher m = pattern.matcher(uri);
				TreeItem<String> start = rootItem;
				while (m.find()) {
					group = m.group().replaceAll("\\/", "");
					TreeItem<String> match = null;
					for (TreeItem<String> ch : start.getChildren()) {
						if (ch.getValue().equals(group)) {
							match = ch;
							break;
						}
					}
					if (match == null) {
						match = new TreeItem<>(group, Images.FOLDER_COLLAPSED.getImageView(16, 16));
						start.getChildren().add(match);
					}
					start = match;
				}
				if (!r.getResourceType().equals(RepoTreeResource.FOLDER)) {
					start.getChildren().add(new TreeItem<>(uri.substring(uri.lastIndexOf('/') + 1), Images.FILE_ICON
							.getImageView(16, 16)));
				}

			});

		return rootItem;
	}
}
