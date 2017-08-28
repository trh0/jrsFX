package de.quinscape.jrsfx.ui;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.JFXTreeView;

import de.quinscape.jrsfx.controller.StaticBase;
import de.quinscape.jrsfx.data.Mapping;
import de.quinscape.jrsfx.util.ApplicationIO;
import de.quinscape.jrsfx.util.Messages;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class UI_Example
		extends AnchorPane
		implements ILoadableFxmlController {

	private final String FXML_LOCATION = "fxml/Base.fxml";
	private final String STYLESHEET = "fxml/application.css";
	public static final Byte IDX = 1;

	@Override
	public String getFxmlLocation() {
		return this.FXML_LOCATION;
	}

	@Override
	public String getCss() {
		return this.STYLESHEET;
	}

	public UI_Example() {

	}

	@FXML
	private BorderPane contentBorderPane;
	@FXML
	private ResourceBundle resources;
	@FXML
	private URL location;
	@FXML
	private VBox keysVbox;
	@FXML
	private HBox keysHbox;
	@FXML
	private HBox bttnHbox;
	@FXML
	private VBox textContainer;
	@FXML
	private TextArea textArea1;
	@FXML
	private ComboBox<String> comboEscapeMethod;
	@FXML
	private JFXTreeView<String> keysTreeView;
	@FXML
	private TextField searchTextField;
	@FXML
	private JFXToggleButton bttnViewRaw;
	@FXML
	private JFXToggleButton bttnEnableEscape;
	@FXML
	private JFXButton exitButton;
	@FXML
	private JFXButton prefsButton;
	@FXML
	private JFXButton saveButton;
	@FXML
	private JFXButton openButton;
	@FXML
	private JFXButton addKeyBttn;
	@FXML
	private JFXButton searchKeyBttn;
	private DropShadow redDropShadow;
	private DropShadow greenDropShadow;

	@FXML
	void searchKeyBttnAction(ActionEvent event) {

	}

	@FXML
	void addKeyBttnAction(ActionEvent event) {

	}

	@FXML
	void toogleEscapeBttn(ActionEvent event) {
		if (this.bttnEnableEscape.isSelected()) {
			this.bttnEnableEscape.setText(Messages.appBundle().getString("button.on"));
		}
		else {
			this.bttnEnableEscape.setText(Messages.appBundle().getString("button.off"));
		}
	}

	@FXML
	void chooseEscapeMethod(ActionEvent event) {
		System.out.println(comboEscapeMethod.getValue());
	}

	@FXML
	void toogleViewRaw(ActionEvent event) {
		if (this.bttnViewRaw.isSelected()) {
			this.bttnViewRaw.setText(Messages.appBundle().getString("button.on"));
		}
		else {
			this.bttnViewRaw.setText(Messages.appBundle().getString("button.off"));
		}
	}

	@FXML
	void addKey(ActionEvent event) {
		String key = this.searchTextField.getText();
		if (key != null) {
			this.animationFor(this.searchTextField, 0, 0.5, 0).play();

		}
		else {
			this.animationFor(this.searchTextField, 0.5, 0, 0).play();

		}
	}

	@FXML
	void searchKey(ActionEvent event) {
		String key = this.searchTextField.getText();
		if (key != null) {
			this.animationFor(this.searchTextField, 0, 0.5, 0).play();

		}
		else {
			this.animationFor(this.searchTextField, 0.5, 0, 0).play();

		}

	}

	@FXML
	void open(MouseEvent event) {
		System.out.println(event.getButton());
		Optional<File> result = Dialogs.openFileDialog(null).showAndWait();

		if (result.isPresent())
			System.out.println(result.get());
	}

	@FXML
	void save(ActionEvent event) {

	}

	@FXML
	void preferences(ActionEvent event) {

	}

	@FXML
	void exit(ActionEvent event) {
		try {
			StaticBase.instance().terminate();
		}
		catch (InterruptedException e) {
			ApplicationIO.toErrorStream(e);
		}
		Platform.exit();

	}

	@FXML
	void initialize() {
		Mapping<String, ResourceBundle> m = Messages.loadBundle(
				"C:\\Users\\rokta\\Documents\\mssgEditor\\assets\\testfiles\\default_global_template.properties");
		this.keysTreeView.setRoot(Messages.bundleToTreeView(m.getValue(), m.getKey(), "."));
		this.comboEscapeMethod.setItems(FXCollections.observableArrayList(StaticBase.instance().getConfig()
				.getProperty("escaping.supported").split(",")));
		this.keysTreeView.getSelectionModel().selectedItemProperty().addListener((ChangeListener<TreeItem<String>>) (ob,
				ov, nv) -> {
			StaticBase.instance().getUiThread().runTask(() -> {
				String theKey = "";
				TreeItem<String> chld = keysTreeView.getSelectionModel().getSelectedItem();
				while (chld != this.keysTreeView.getRoot()) {
					theKey = chld.getValue() + (theKey != null && !theKey.isEmpty() ? "." + theKey : "");
					chld = chld.getParent();
				}
				this.setSelectedKey(theKey);
			});
		});
		this.redDropShadow = new DropShadow(10, 0, 0, new Color(1, 0, 0, 0.5));
		this.greenDropShadow = new DropShadow(10, 0, 0, new Color(0, 1, 0, 0.5));

	}

	private Animation animationFor(Control region, double r, double g, double b) {
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

	private void setSelectedKey(String key) {}
}
