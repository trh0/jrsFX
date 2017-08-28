package de.quinscape.jrsfx;

import de.quinscape.jrsfx.controller.StaticBase;
import de.quinscape.jrsfx.jasper.ReportClient;
import de.quinscape.jrsfx.util.ApplicationIO;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main
		extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			StaticBase.instance().initUI(primaryStage);
		}
		catch (Exception e) {
			ApplicationIO.toErrorStream(e, "MAIN");
		}
		StaticBase.instance().getDataThread().runTask(() -> {
			ReportClient c = new ReportClient(null, "superuser", "superuser");
			c.getResources(null, 0, 1000, null).forEach(System.out::println);

		});
	}

	public static void main(String[] args) throws InterruptedException {
		launch(args);
	}
}
