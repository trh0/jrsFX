package de.quinscape.jrsfx.ui;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTreeView;

import de.quinscape.jrsfx.controller.StaticBase;
import de.quinscape.jrsfx.jasper.JasperReportsRestClient;
import de.quinscape.jrsfx.ui.components.CodeEditor;
import de.quinscape.jrsfx.ui.components.JasperUI;
import de.quinscape.jrsfx.util.ApplicationIO;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Control;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.util.Duration;

public class FXMLController_ViewBase
		extends AnchorPane
		implements ILoadableFxmlController {

	private final String FXML_LOCATION = "fxml/ViewBase.fxml";
	private final String STYLESHEET = "fxml/application.css";
	public static final Byte IDX = 1;
	private JasperReportsRestClient jrsClient;

	@Override
	public String getFxmlLocation() {
		return this.FXML_LOCATION;
	}

	@Override
	public String getCss() {
		return this.STYLESHEET;
	}

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="root"
	private AnchorPane root;

	@FXML // fx:id="borderPane"
	private BorderPane borderPane;

	@FXML // fx:id="contentSplitPane"
	private SplitPane contentSplitPane;

	@FXML // fx:id="horizontalSplitPane"
	private SplitPane horizontalSplitPane;

	@FXML // fx:id="accordion"
	private Accordion accordion;

	@FXML // fx:id="accTitledPane"
	private TitledPane accTitledPane;

	@FXML // fx:id="accAnchorPane"
	private AnchorPane accAnchorPane;

	@FXML // fx:id="accTreeVIew"
	private JFXTreeView<String> accTreeView;

	@FXML
	private VBox sidebarVBox;

	@FXML // fx:id="tabPane"
	private TabPane tabPane;

	@FXML // fx:id="tab"
	private Tab tab;

	@FXML // fx:id="tabAnchorPane"
	private AnchorPane tabAnchorPane;

	@FXML
	private WebView welcomeWebView;

	@FXML
	private VBox topWrapperVBox;

	@FXML // fx:id="progressBar"
	private JFXProgressBar progressBar;

	@FXML // fx:id="menuHBox"
	private HBox menuHBox;

	@FXML // fx:id="button1"
	private JFXButton button1;

	@FXML // fx:id="button2"
	private JFXButton button2;

	@FXML // fx:id="button3"
	private JFXButton button3;

	@FXML // fx:id="button4"
	private JFXButton button4;

	@FXML // fx:id="button5"
	private JFXButton button5;

	@FXML
	void connectJRS(ActionEvent event) {
		SimpleDoubleProperty prop = new SimpleDoubleProperty();
		progressBar.progressProperty().bind(prop);

		progressBar.setStyle("-fx-accent: red;");
		final Animation animation = new Transition() {
			{
				setCycleDuration(Duration.millis(750));
				setInterpolator(Interpolator.EASE_OUT);
			}

			@Override
			protected void interpolate(double frac) {
				progressBar.setStyle("-fx-accent: rgba( 0, 255, 0," + (1 - frac) + ")");
				if (frac > 0.99) {
					progressBar.progressProperty().unbind();
					progressBar.setProgress(0);
					this.stop();
				}
			}
		};

		StaticBase.instance().getUiThread().runTask(() -> {
			TreeItem<String> root = JasperUI.repositoryTreeItem(jrsClient.getResources(null, 0, 1500, null), prop);
			Platform.runLater(() -> {
				this.accTreeView.setRoot(root);
				MultipleSelectionModel<TreeItem<String>> sm = accTreeView.getSelectionModel();
				sm.selectedItemProperty().addListener((o, ov, nv) -> {
					StaticBase.instance().getUiThread().runTask(() -> {
						String uri = "";
						TreeItem<String> chld = sm.getSelectedItem();
						while (chld != this.accTreeView.getRoot()) {
							uri = chld.getValue() + (uri != null && !uri.isEmpty() ? "/" + uri : "");
							chld = chld.getParent();
						}
						this.handleSelectResource("/" + uri, nv.getValue());
					});
				});
				this.accTitledPane.setText("JRS Repository");
				progressBar.setStyle("-fx-accent:green;");
				animation.play();
			});
		});

	}

	private void handleSelectResource(String uri, String value) {
		StaticBase.instance().getDataThread().runTask(() -> {
			String s = jrsClient.getResourceDetail(uri, true);
			Platform.runLater(() -> {
				CodeEditor ce = new CodeEditor(s, value);
				ce.getPane().prefWidthProperty().bind(tabPane.widthProperty());
				ce.getPane().prefHeightProperty().bind(tabPane.heightProperty());
				this.tabPane.getTabs().add(ce);
			});
		});
		System.out.println(uri);
	}

	@FXML
	void toggleExit(ActionEvent event) {
		try {
			StaticBase.instance().terminate();
		}
		catch (InterruptedException e) {
			ApplicationIO.toErrorStream(e);
		}
	}

	@FXML
	void togglePreferences(ActionEvent event) {

	}

	@FXML
	void initialize() {
		jrsClient = new JasperReportsRestClient(null, StaticBase.instance().getConfig().getProperty("jrs.admin.user"),
				StaticBase.instance().getConfig().getProperty("jrs.admin.pw"));
		progressBar.setStyle("");

	}

	// TODO example
	public Animation animationFor(Control region, double r, double g, double b) {
		final Animation animation = new Transition() {
			{
				setCycleDuration(Duration.millis(500));
				setInterpolator(Interpolator.EASE_OUT);
			}

			@Override
			protected void interpolate(double frac) {
				Color vColor = new Color(r, g, b, 1 - frac);
				region.setBackground(new Background(new BackgroundFill(vColor, CornerRadii.EMPTY, Insets.EMPTY)));
			}
		};
		return animation;
	}
}
