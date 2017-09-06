package de.quinscape.jrsfx.ui.components;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.quinscape.jrsfx.controller.StaticBase;
import de.quinscape.jrsfx.jasper.JRSRestClient;
import de.quinscape.jrsfx.jasper.RepoTreeResource;
import de.quinscape.jrsfx.jasper.ResourceMediaType;
import de.quinscape.jrsfx.ui.Images;
import de.quinscape.jrsfx.util.ApplicationIO;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

public class JasperUI {
	private JasperUI() {}

	public static RepoResourceTreeItem repositoryTreeItem(List<RepoTreeResource> resources, DoubleProperty progress) {
		RepoResourceTreeItem rootItem = new RepoResourceTreeItem(StaticBase.instance().getConfig().getProperty(
				"jrs.url"), Images.HOST.getImageView(16, 16));
		Pattern pattern = Pattern.compile("\\/[^/\\s]+(?=\\/)");
		double len = resources.size();
		final AtomicInteger now = new AtomicInteger(1);
		if (resources != null)
			resources.forEach(r -> {
				String uri = r.getUri();
				String group = null;
				Matcher m = pattern.matcher(uri);
				RepoResourceTreeItem start = rootItem;
				while (m.find()) {
					group = m.group().replaceAll("\\/", "");
					RepoResourceTreeItem match = null;
					for (TreeItem<String> ch : start.getChildren()) {
						if (ch.getValue().equals(group)) {
							match = (RepoResourceTreeItem) ch;
							break;
						}
					}
					if (match == null) {
						match = new RepoResourceTreeItem(group, Images.FOLDER_COLLAPSED.getImageView(16, 16));
						start.getChildren().add(match);
					}
					start = match;
				}
				if (!r.getResourceType().equals(ResourceMediaType.FOLDER_TYPE)) {
					start.getChildren().add(new RepoResourceTreeItem(uri.substring(uri.lastIndexOf('/') + 1), r,
							Images.FILE_ICON.getImageView(16, 16)));
				}
				Platform.runLater(() -> progress.setValue(now.doubleValue() / len));
				now.incrementAndGet();
			});

		return rootItem;
	}

	public static Tab getReportIFrame(JRSRestClient client, String uri, String title) {
		Tab result = new Tab(title);
		VBox box = new VBox();
		WebView webView = new WebView();
		box.getChildren().add(webView);
		VBox.setVgrow(webView, Priority.ALWAYS);
		result.setContent(box);

		return result;

	}

	public static Tab getReportRaw(JRSRestClient client, String uri, String title) {
		Tab result = new Tab(title);
		VBox box = new VBox();
		WebView webView = new WebView();
		box.setPrefWidth(VBox.USE_COMPUTED_SIZE);
		StaticBase.instance().getUiThread().runTask(() -> {
			String html = client.getReportHtml(uri, null);
			Platform.runLater(() -> webView.getEngine().loadContent(html));
		});
		box.getChildren().add(webView);
		VBox.setVgrow(webView, Priority.ALWAYS);
		result.setContent(box);

		return result;

	}

	public static Tab getReportVisualizeJS(String uri, String title) {
		Tab result = new Tab(title);
		VBox box = new VBox();
		WebView webView = new WebView();
		box.setPrefWidth(VBox.USE_COMPUTED_SIZE);
		StaticBase sb = StaticBase.instance();
		sb.getUiThread().runTask(() -> {
			String sbHtml = null;
			String script = null;
			try {
				script = ApplicationIO.fromClasspath("visualize/visualizeReport.js");
				sbHtml = ApplicationIO.fromClasspath("visualize/visualize.html");
			}
			catch (Exception e) {
				ApplicationIO.toErrorStream(e);
			}

			script = script.replaceAll(Pattern.quote("${jrs}"), JRSRestClient.SERVER_BASE_URL);

			String jrsUser = sb.getConfig().getProperty("jrs.admin.user");
			String jrsPw = sb.getConfig().getProperty("jrs.admin.pw");
			String jrsOrga = sb.getConfig().getProperty("jrs.admin.orga");
			jrsOrga = (jrsOrga != null) ? ",orga :\"" + jrsOrga + "\"" : "";

			script = script.replaceAll(Pattern.quote("${name}"), jrsUser);
			script = script.replaceAll(Pattern.quote("${pass}"), jrsPw);
			script = script.replaceAll(Pattern.quote("${orga}"), jrsOrga);
			script = script.replaceAll(Pattern.quote("${uri}"), uri);

			final String html = sbHtml.replace("${jrs}", JRSRestClient.SERVER_BASE_URL).replace("${script}", script);
			System.out.println(html);
			Platform.runLater(() -> webView.getEngine().loadContent(html));

		});
		box.getChildren().add(webView);
		VBox.setVgrow(webView, Priority.ALWAYS);
		result.setContent(box);

		return result;
	}

}
