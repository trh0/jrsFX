package de.quinscape.jrsfx.ui;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTreeView;

import de.quinscape.jrsfx.controller.StaticBase;
import de.quinscape.jrsfx.jasper.JRSRestClient;
import de.quinscape.jrsfx.jasper.RepoTreeResource;
import de.quinscape.jrsfx.jasper.ResourceMediaType;
import de.quinscape.jrsfx.ui.components.CodeEditor;
import de.quinscape.jrsfx.ui.components.Dialogs;
import de.quinscape.jrsfx.ui.components.JasperUI;
import de.quinscape.jrsfx.ui.components.RepoResourceTreeItem;
import de.quinscape.jrsfx.util.ApplicationIO;
import de.quinscape.jrsfx.util.Messages;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
	private JRSRestClient jrsClient;

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
		Optional<ButtonType> ok = Dialogs.getOpenJRSDialog().showAndWait();

		if ((ok.get()) == Dialogs.TypeOk) {
			if (this.accTreeView.getRoot() == null) {
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
					RepoResourceTreeItem root = JasperUI.repositoryTreeItem(jrsClient.getResources(null, 0, 1500, null),
							prop);
					Platform.runLater(() -> {
						this.accTreeView.setRoot(root);
						MultipleSelectionModel<TreeItem<String>> sm = accTreeView.getSelectionModel();
						sm.selectedItemProperty().addListener((o, ov, nv) -> {
							String uri = "";
							RepoResourceTreeItem chld = (RepoResourceTreeItem) sm.getSelectedItem();
							RepoTreeResource details = (chld != null) ? chld.getDetails() : null;
							if (details == null) {
								while (chld != this.accTreeView.getRoot()) {
									uri = chld.getValue() + (uri != null && !uri.isEmpty() ? "/" + uri : "");
									chld = (RepoResourceTreeItem) chld.getParent();
								}
								uri = "/" + uri;
							}
							else {
								uri = details.getUri();
							}
							this.handleSelectResource(uri, nv.getValue(), details);
						});
						this.accTitledPane.setText("JRS Repository");
						progressBar.setStyle("-fx-accent:green;");
						animation.play();
					});
				});
			}
			else {
				StaticBase.instance().getDataThread().scheduleTask(() -> {
					Platform.runLater(() -> this.accTreeView.setRoot(null));
				}, 1000);
			}
		}
	}

	private void handleSelectResource(String uri, String resourceName, RepoTreeResource details) {
		this.sidebarVBox.getChildren().clear();
		JFXButton viewDetailsButton = new JFXButton(Messages.appBundle().getString("button.viewdetails.label"));
		viewDetailsButton.setOnAction((e) -> {

			StaticBase.instance().getUiThread().runTask(() -> {
				if (uri.endsWith(".png") || uri.endsWith(".jpg")) {
					Image img = jrsClient.getResourceImage(uri, false);
					Platform.runLater(() -> {
						Tab t = new Tab(resourceName);
						VBox box = new VBox();
						ImageView v = new ImageView(img);
						box.getChildren().add(v);
						VBox.setVgrow(v, Priority.ALWAYS);
						t.setContent(box);
						this.tabPane.getTabs().add(t);
					});
				}
				else {
					final StringBuilder s = new StringBuilder();
					s.append(jrsClient.getResourceDetail(uri, true));
					Platform.runLater(() -> {
						CodeEditor ce = null;
						if (details != null) {
							if (uri.endsWith(".css")) {
								ce = new CodeEditor(s.toString(), resourceName, CodeEditor.Mode.CSS);
							}
							else if (uri.endsWith(".js")) {
								ce = new CodeEditor(s.toString(), resourceName, CodeEditor.Mode.JS);
							}
							else if (uri.endsWith(".json")) {
								ce = new CodeEditor(s.toString(), resourceName, CodeEditor.Mode.JSON);
							}
							else if (uri.endsWith(".js") || uri.endsWith(".svg")) {
								ce = new CodeEditor(s.toString(), resourceName, CodeEditor.Mode.HTML);
							}
							else if (uri.endsWith(".properties")) {
								ce = new CodeEditor(s.toString(), resourceName, CodeEditor.Mode.RAW);
							}

						}
						if (ce == null && s.toString().contains("<?xml"))
							ce = new CodeEditor(s.toString(), resourceName, CodeEditor.Mode.XML);
						else if (ce == null) {
							ce = new CodeEditor(s.toString(), resourceName, CodeEditor.Mode.RAW);
						}
						ce.getPane().prefWidthProperty().bind(tabPane.widthProperty());
						ce.getPane().prefHeightProperty().bind(tabPane.heightProperty());
						this.tabPane.getTabs().add(ce);
					});
				}

			});
		});
		this.sidebarVBox.getChildren().add(viewDetailsButton);
		if (details != null && details.getResourceType().equals(ResourceMediaType.REPORT_UNIT_TYPE))

		{
			JFXButton viewReport1 = new JFXButton(Messages.appBundle().getString("button.viewreport.raw.label"));
			JFXButton viewReport2 = new JFXButton(Messages.appBundle().getString("button.viewreport.iframe.label"));
			JFXButton viewReport3 = new JFXButton(Messages.appBundle().getString("button.viewreport.vizjs.label"));
			this.sidebarVBox.getChildren().addAll(viewReport1, viewReport2, viewReport3);
			viewReport1.setOnAction((e) -> {
				Tab t = JasperUI.getReportRaw(jrsClient, uri, resourceName);
				this.tabPane.getTabs().add(t);
			});
			viewReport2.setOnAction((e) -> {
				Tab t = JasperUI.getReportIFrame(uri, resourceName);
				this.tabPane.getTabs().add(t);
			});
			viewReport3.setOnAction((e) -> {
				Tab t = JasperUI.getReportVisualizeJS(uri, resourceName);
				this.tabPane.getTabs().add(t);
			});
		}
		this.sidebarVBox.getChildren().forEach(chld -> VBox.setVgrow(chld, Priority.ALWAYS));
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
		jrsClient = new JRSRestClient(StaticBase.instance().getConfig().getProperty("jrs.admin.orga"), StaticBase
				.instance().getConfig().getProperty("jrs.admin.user"), StaticBase.instance().getConfig().getProperty(
						"jrs.admin.pw"));
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
