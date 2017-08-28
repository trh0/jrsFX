package de.quinscape.jrsfx.ui;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;

import de.quinscape.jrsfx.controller.StaticBase;
import de.quinscape.jrsfx.util.ApplicationIO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
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
	private AnchorPane root; // Value injected by FXMLLoader

	@FXML // fx:id="borderPane"
	private BorderPane borderPane; // Value injected by FXMLLoader

	@FXML // fx:id="contentSplitPane"
	private SplitPane contentSplitPane; // Value injected by FXMLLoader

	@FXML // fx:id="horizontalSplitPane"
	private SplitPane horizontalSplitPane; // Value injected by FXMLLoader

	@FXML // fx:id="accordion"
	private Accordion accordion; // Value injected by FXMLLoader

	@FXML // fx:id="accTitledPane"
	private TitledPane accTitledPane; // Value injected by FXMLLoader

	@FXML // fx:id="accAnchorPane"
	private AnchorPane accAnchorPane; // Value injected by FXMLLoader

	@FXML // fx:id="accListView"
	private JFXListView<?> accListView; // Value injected by FXMLLoader

	@FXML // fx:id="buttomListView"
	private ListView<?> buttomListView; // Value injected by FXMLLoader

	@FXML // fx:id="tabPane"
	private TabPane tabPane; // Value injected by FXMLLoader

	@FXML // fx:id="tab"
	private Tab tab; // Value injected by FXMLLoader

	@FXML // fx:id="tabAnchorPane"
	private AnchorPane tabAnchorPane; // Value injected by FXMLLoader

	@FXML // fx:id="footerSplitPane"
	private SplitPane footerSplitPane; // Value injected by FXMLLoader

	@FXML // fx:id="progressBar"
	private ProgressBar progressBar; // Value injected by FXMLLoader

	@FXML // fx:id="menuHBox"
	private HBox menuHBox; // Value injected by FXMLLoader

	@FXML // fx:id="button1"
	private JFXButton button1; // Value injected by FXMLLoader

	@FXML // fx:id="button2"
	private JFXButton button2; // Value injected by FXMLLoader

	@FXML // fx:id="button3"
	private JFXButton button3; // Value injected by FXMLLoader

	@FXML // fx:id="button4"
	private JFXButton button4; // Value injected by FXMLLoader

	@FXML // fx:id="button5"
	private JFXButton button5; // Value injected by FXMLLoader

	@FXML
	void connectJRS(ActionEvent event) {

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

	@FXML // This method is called by the FXMLLoader when initialization is
			// complete
	void initialize() {
		assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert borderPane != null : "fx:id=\"borderPane\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert contentSplitPane != null : "fx:id=\"contentSplitPane\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert horizontalSplitPane != null : "fx:id=\"horizontalSplitPane\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert accordion != null : "fx:id=\"accordion\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert accTitledPane != null : "fx:id=\"accTitledPane\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert accAnchorPane != null : "fx:id=\"accAnchorPane\" was not injected: check your FXML file 'ViewBase.fxml'.";
		assert accListView != null : "fx:id=\"accListView\" was not injected: check your FXML file 'ViewBase.fxml'.";
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
