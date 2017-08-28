package de.quinscape.jrsfx.controller;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.quinscape.jrsfx.data.Mapping;
import de.quinscape.jrsfx.ui.ILoadableFxmlController;
import de.quinscape.jrsfx.util.ApplicationIO;
import de.quinscape.jrsfx.util.Messages;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class UIController
		implements IController {

	public static final Byte BASE_VIEW_IDX = 1;

	private FXMLLoader loader;
	private Stage primaryStage;
	private Scene mainScene;
	private double xOff = 0;
	private double yOff = 0;

	public Scene getMainScene() {
		return this.mainScene;
	}

	private final Map<Byte, Object> controllers;

	protected UIController(Stage primaryStage) {
		super();
		this.primaryStage = primaryStage;
		this.controllers = new ConcurrentHashMap<>();
		bootup();
	}

	protected Mapping<Parent, Object> loadFxml(String nameAndPath, Class<?> rootClass) throws Exception {
		Object c = null;
		if (rootClass != null) {
			c = rootClass.newInstance();
		}
		String fxmlLocation = null;
		if (c instanceof ILoadableFxmlController) {
			fxmlLocation = ((ILoadableFxmlController) c).getFxmlLocation();
		}
		else if (nameAndPath != null && !nameAndPath.isEmpty()) {
			fxmlLocation = nameAndPath;
		}
		else {
			throw new IllegalArgumentException("No loadable resource provided for rootClas " + rootClass);
		}

		URL tar;
		tar = UIController.class.getClassLoader().getResource(fxmlLocation);
		if (tar != null) {
			loader.setLocation(tar);
			loader.setResources(Messages.appBundle());
			loader.setRoot(c);
			loader.setController(c);
			loader.load();
			return new Mapping<>(loader.getRoot(), loader.getController());

		}
		else {
			throw new IllegalArgumentException("Not found " + nameAndPath);
		}
	}

	protected Mapping<Parent, Object> loadFxml(String nameAndPath, String classForName) throws Exception {
		return loadFxml(nameAndPath, Class.forName(classForName));
	}

	@Override
	public void bootup() {
		this.loader = new FXMLLoader();
		try {
			Mapping<Parent, Object> mapping = loadFxml(null, StaticBase.instance().getConfig().getProperty(
					"ui.baseview.controller.class"));
			Parent viewbase = mapping.getKey();
			controllers.put(BASE_VIEW_IDX, mapping.getValue());
			mainScene = new Scene(viewbase);

			mainScene.setOnMousePressed((MouseEvent e) -> {
				xOff = e.getSceneX();
				yOff = e.getSceneY();
			});
			mainScene.setOnMouseDragged((MouseEvent e) -> {
				primaryStage.setX(e.getScreenX() - xOff);
				primaryStage.setY(e.getScreenY() - yOff);
			});

			primaryStage.setScene(mainScene);
			primaryStage.initStyle(StageStyle.UNIFIED);
			primaryStage.show();
		}
		catch (Exception e) {
			ApplicationIO.toErrorStream(e, "on loading baseView");
		}
	}

	@Override
	public void terminate() {}

	public Object getController(Byte idx) {
		return this.controllers.get(idx);
	}

}
