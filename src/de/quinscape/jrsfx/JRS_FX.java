package de.quinscape.jrsfx;

import de.quinscape.jrsfx.controller.StaticBase;
import de.quinscape.jrsfx.util.ApplicationIO;
import javafx.application.Application;
import javafx.stage.Stage;

public class JRS_FX
		extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			StaticBase.instance().initUI(primaryStage);
		}
		catch (Exception e) {
			ApplicationIO.toErrorStream(e, "MAIN");
		}
	}

	public static void main(String[] args) throws InterruptedException {
		launch(args);
	}
}
