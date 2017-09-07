package de.quinscape.jrsfx.ui.components;

import de.quinscape.jrsfx.util.ApplicationIO;
import de.quinscape.jrsfx.util.Messages;
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
	private Mode mode;

	private String applyEditingTemplate() {
		return editingTemplate.replace("${code}", editingCode).replace("${mode}", mode.mode());
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

	public enum Mode {
		C("text/x-csrc", "codemirror/clike_min.js"),
		CPP("text/x-c++src", "codemirror/clike_min.js"),
		KOTLIN("text/x-kotlin", "codemirror/clike_min.js"),
		SCALA("text/scala", "codemirror/clike_min.js"),
		JAVA("text/x-java", "codemirror/clike_min.js"),
		XML("application/xml", "codemirror/xml.js"),
		HTML("text/html", "codemirror/xml.js"),
		SQL("text/x-sql", "codemirror/sql.js"),
		CSS("text/css", "codemirror/css.js"),
		JS("text/javascript", "codemirror/js.js"),
		JSON("application/json", "codemirror/js.js"),
		TYPESCRIPT("application/typescript", "codemirror/js.js"),
		RAW("text/plain", "codemirror/clike_min.js");
		private final String mode;
		private final String jsSource;

		private Mode(String s, String jsSource) {
			this.mode = s;
			this.jsSource = jsSource;
		}

		public String mode() {
			return this.mode;
		}

		public String source() {
			return this.jsSource;
		}
	}

	/**
	 * 
	 * @param editingCode
	 * @param title
	 */
	public CodeEditor(String editingCode, String title, Mode mode) {
		this.mode = (mode == null) ? Mode.XML : mode;
		this.editingTemplate = getTemplate();
		this.editingCode = (this.mode == Mode.XML) ? Messages.formatXML(editingCode) : editingCode;
		this.box = new VBox();
		webview.getEngine().loadContent(applyEditingTemplate());
		box.setPrefWidth(VBox.USE_COMPUTED_SIZE);
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
									this.mode.source()) + "</script>" + "</head>" + "<body>"
					+ "<textarea id=\"code\" name=\"code\">\n" + "${code}" + "</textarea>" + "<script>"
					+ "  var editor = CodeMirror.fromTextArea(document.getElementById(\"code\"), {"
					+ "    lineNumbers: true," + "    matchBrackets: true," + "    mode: \"${mode}\"" + "  });"
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
					+ "    lineNumbers: true," + "    matchBrackets: true," + "    mode: \"${mode}\"" + "  });"
					+ "</script>" + "</body>" + "</html>";
		}
	}
}