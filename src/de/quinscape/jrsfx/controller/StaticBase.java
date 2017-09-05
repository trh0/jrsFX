package de.quinscape.jrsfx.controller;

import java.io.IOException;

import de.quinscape.jrsfx.config.Configuration;
import de.quinscape.jrsfx.util.ApplicationIO;
import javafx.application.Platform;
import javafx.stage.Stage;

public class StaticBase
		implements IController {

	private static StaticBase instance;
	private final DataThread dataThread;

	public DataThread getDataThread() {
		return this.dataThread;
	}

	private final UIThread uiThread;

	public UIThread getUiThread() {
		return this.uiThread;
	}

	private UIController uiController;

	public UIController getUIController() {
		return this.uiController;
	}

	private Configuration config;

	public Configuration getConfig() {
		return this.config;
	}

	private StaticBase() {
		this.uiThread = new UIThread();
		this.dataThread = new DataThread();
		this.bootup();
	}

	public static StaticBase instance() {
		if (instance == null) {
			instance = new StaticBase();
		}
		return instance;
	}

	@Override
	public void bootup() {
		try {
			this.config = new Configuration(Configuration.DEFAULT_CFG, null);
		}
		catch (IOException e) {
			ApplicationIO.toErrorStream("Unable to load configuration", e);
		}
	}

	public void initUI(Stage stage) {
		this.uiController = new UIController(stage);
	}

	@Override
	public void terminate() throws InterruptedException {
		uiThread.terminate();
		dataThread.terminate();
		uiController.terminate();
		Platform.exit();
	}

}
