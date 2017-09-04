package de.quinscape.jrsfx.ui.components;

import de.quinscape.jrsfx.util.ApplicationIO;
import javafx.scene.control.Tab;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

/**
 * 
 * @author TKoll
 *
 */
public class CodeEditor
		extends Tab {
	final WebView webview = new WebView();

	private String editingCode;

	private final String editingTemplate;
	private final VBox box;

	private String applyEditingTemplate() {
		return editingTemplate.replace("${code}", editingCode);
	}

	public void setCode(String newCode) {
		this.editingCode = newCode;
		webview.getEngine().loadContent(applyEditingTemplate());
	}

	public String getCodeAndSnapshot() {
		this.editingCode = (String) webview.getEngine().executeScript("editor.getValue();");
		return editingCode;
	}

	public void revertEdits() {
		setCode(editingCode);
	}

	public Region getPane() {
		return this.box;

	}

	/**
	 * 
	 * @param editingCode
	 * @param title
	 */
	public CodeEditor(String editingCode, String title) {
		this.editingTemplate = getTemplate();
		this.editingCode = editingCode;
		this.box = new VBox();
		webview.getEngine().loadContent(applyEditingTemplate());
		box.setPrefWidth(VBox.USE_COMPUTED_SIZE);
		box.setPrefHeight(Integer.MAX_VALUE);
		box.getChildren().add(webview);
		VBox.setVgrow(webview, Priority.ALWAYS);
		webview.prefWidthProperty().bind(box.widthProperty());
		webview.prefHeightProperty().bind(box.heightProperty());
		this.setText(title);
		this.setContent(box);
	}

	private String getTemplate() {
		try {
			return "<!doctype html>" + "<html>" + "<head>" + "  <style>" + ApplicationIO.fromClasspath(
					"codemirror/codemirror_min.css") + "</style>" + "  <script>" + ApplicationIO.fromClasspath(
							"codemirror/codemirror_min.js") + "</script>" + "  <script>" + ApplicationIO.fromClasspath(
									"codemirror/clike_min.js") + "</script>" + "</head>" + "<body>"
					+ "<form><textarea id=\"code\" name=\"code\">\n" + "${code}" + "</textarea></form>" + "<script>"
					+ "  var editor = CodeMirror.fromTextArea(document.getElementById(\"code\"), {"
					+ "    lineNumbers: true," + "    matchBrackets: true," + "    mode: \"text/x-java\"" + "  });"
					+ "</script>" + "</body>" + "</html>";
		}
		catch (Exception e) {
			ApplicationIO.toErrorStream(e);
			return "<!doctype html>" + "<html>" + "<head>"
					+ "  <link rel=\"stylesheet\" href=\"http://codemirror.net/lib/codemirror.css\">"
					+ "  <script src=\"http://codemirror.net/lib/codemirror.js\"></script>"
					+ "  <script src=\"http://codemirror.net/mode/clike/clike.js\"></script>" + "</head>" + "<body>"
					+ "<form><textarea id=\"code\" name=\"code\">\n" + "${code}" + "</textarea></form>" + "<script>"
					+ "  var editor = CodeMirror.fromTextArea(document.getElementById(\"code\"), {"
					+ "    lineNumbers: true," + "    matchBrackets: true," + "    mode: \"text/x-java\"" + "  });"
					+ "</script>" + "</body>" + "</html>";
		}
	}
}