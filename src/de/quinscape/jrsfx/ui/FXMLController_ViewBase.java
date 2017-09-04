package de.quinscape.jrsfx.ui;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeView;

import de.quinscape.jrsfx.controller.StaticBase;
import de.quinscape.jrsfx.jasper.JasperReportsRestClient;
import de.quinscape.jrsfx.ui.components.JasperUI;
import de.quinscape.jrsfx.util.ApplicationIO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class FXMLController_ViewBase
		extends AnchorPane
		implements ILoadableFxmlController {

	private final String FXML_LOCATION = "fxml/ViewBase.fxml";
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

	@FXML // fx:id="buttomListView"
	private ListView<?> buttomListView;

	@FXML // fx:id="tabPane"
	private TabPane tabPane;

	@FXML // fx:id="tab"
	private Tab tab;

	@FXML // fx:id="tabAnchorPane"
	private AnchorPane tabAnchorPane;

	@FXML // fx:id="footerSplitPane"
	private SplitPane footerSplitPane;

	@FXML // fx:id="progressBar"
	private ProgressBar progressBar;

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
		JasperReportsRestClient c = new JasperReportsRestClient(null, StaticBase.instance().getConfig().getProperty("jrs.admin.user"),
				StaticBase.instance().getConfig().getProperty("jrs.admin.pw"));
		StaticBase.instance().getUiThread().runTask(() -> {
			TreeItem<String> root = JasperUI.repositoryTreeItem(c.getResources(null, 0, 5000, null));
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
						this.handleSelectResource("/" + uri);
					});
				});
				this.accTitledPane.setText("JRS Repository");
			});
		});
	}

	private void handleSelectResource(String uri) {
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
		assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert borderPane != null : "fx:id=\"borderPane\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert contentSplitPane != null : "fx:id=\"contentSplitPane\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert horizontalSplitPane != null : "fx:id=\"horizontalSplitPane\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert accordion != null : "fx:id=\"accordion\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert accTitledPane != null : "fx:id=\"accTitledPane\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert accAnchorPane != null : "fx:id=\"accAnchorPane\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert accTreeView != null : "fx:id=\"accTreeView\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert buttomListView != null : "fx:id=\"buttomListView\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert tabPane != null : "fx:id=\"tabPane\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert tab != null : "fx:id=\"tab\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert tabAnchorPane != null : "fx:id=\"tabAnchorPane\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert footerSplitPane != null : "fx:id=\"footerSplitPane\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert progressBar != null : "fx:id=\"progressBar\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert menuHBox != null : "fx:id=\"menuHBox\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert button1 != null : "fx:id=\"button1\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert button2 != null : "fx:id=\"button2\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert button3 != null : "fx:id=\"button3\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert button4 != null : "fx:id=\"button4\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert button5 != null : "fx:id=\"button5\" was not injected: check your FXML file 'ViewBase.fxml'.";

	}

}
