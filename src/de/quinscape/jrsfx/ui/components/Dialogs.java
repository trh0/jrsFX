package de.quinscape.jrsfx.ui.components;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import de.quinscape.jrsfx.config.Configuration;
import de.quinscape.jrsfx.controller.StaticBase;
import de.quinscape.jrsfx.ui.Images;
import de.quinscape.jrsfx.util.ApplicationIO;
import de.quinscape.jrsfx.util.Messages;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * 
 * @author trh0 - TKoll
 *
 */
public class Dialogs {
	private Dialogs() {}

	//@formatter:off
	public static final ButtonType TypeOk = 
			new ButtonType(Messages.appBundle().getString("alert.button.okay"), ButtonData.OK_DONE);
	public static final ButtonType TypeApply = 
			new ButtonType(Messages.appBundle().getString("alert.button.apply"), ButtonData.APPLY);
	public static final ButtonType TypeCancel = 
			new ButtonType(Messages.appBundle().getString("alert.button.cancel"), ButtonData.CANCEL_CLOSE);
	public static final ButtonType TypeClose = 
			new ButtonType(Messages.appBundle().getString("alert.button.close"), ButtonData.CANCEL_CLOSE);
	public static final ButtonType TypeYes = 
			new ButtonType(Messages.appBundle().getString("alert.button.yes"), ButtonData.YES);
	public static final ButtonType TypeNo = 
			new ButtonType(Messages.appBundle().getString("alert.button.no"),ButtonData.NO);
	public static final ButtonType TypeContinue = 
			new ButtonType(Messages.appBundle().getString("alert.button.continue"), ButtonData.NEXT_FORWARD);
	public static final ButtonType TypeNext = 
			new ButtonType(Messages.appBundle().getString("alert.button.next"), ButtonData.NEXT_FORWARD);
	public static final ButtonType TypeBack = 
			new ButtonType(Messages.appBundle().getString("alert.button.back"), ButtonData.BACK_PREVIOUS);
	//@formatter:on
	/**
	 * @author trh0 - TKoll
	 *
	 * @param fromPath
	 *            RootPath to select folders from.
	 * @return A Dialog containing a fileBrowser.
	 * @see {@link #filePathBrowser(Dialog, String)}
	 */
	public static Dialog<File> openFileDialog(String fromPath) {
		Dialog<File> dialog = new Dialog<>();
		dialog.setResizable(true);
		dialog.getDialogPane().setContent(filePathBrowser(dialog, fromPath));
		dialog.getDialogPane().getButtonTypes().addAll(TypeOk, TypeCancel);

		return dialog;
	}

	/**
	 * 
	 * @author trh0 - TKoll
	 * 
	 * @param dialog
	 *            The dialog the browser should be added to.
	 * @param fromPath
	 *            The rootPath to build the browser from. <br>
	 *            Will be
	 *            <code>FileSystems::getDefault()::getRootDirectories()</code>
	 *            if null.
	 * @return A {@link VBox} containing a {@link TreeView} containing all
	 *         {@link File}s and folders wrapped into {@link TreeItem}s found
	 *         recursively from the given path.<br>
	 */
	public static VBox filePathBrowser(Dialog<File> dialog, String fromPath) {
		/**
		 * Custom ChangeListener impl. for this scenario.
		 * 
		 * @author trh0 - TKoll
		 *
		 */
		class DaListener
				implements ChangeListener<TreeItem<String>> {
			private File data = null;

			@Override
			public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue,
					TreeItem<String> newValue) {
				this.data = FileTreeItem.class.cast(observable.getValue()).getFile();
			}

		}
		DaListener daListener = new DaListener();
		dialog.setResultConverter(buttonType -> {
			if (buttonType == TypeOk)
				return daListener.data;
			else
				return null;
		});
		TreeView<String> treeView = null;
		String host = null;
		File rootFile = null;
		List<Path> rootDirectories = new ArrayList<>();
		if (fromPath != null) {
			rootFile = new File(fromPath);
			host = rootFile.getName();
			rootDirectories.add(rootFile.toPath());
		}
		else {
			try {
				host = InetAddress.getLocalHost().getHostName();
			}
			catch (UnknownHostException e) {
				ApplicationIO.toErrorStream(e);
			}
			FileSystems.getDefault().getRootDirectories().forEach(rootDirectories::add);
		}
		TreeItem<String> rootNode = new TreeItem<>(host, Images.HOST.getImageView(16, 16));
		for (Path name : rootDirectories) {
			FileTreeItem treeNode = new FileTreeItem(new File(name.toUri()), false);
			rootNode.getChildren().add(treeNode);
		}
		rootNode.setExpanded(true);
		treeView = new TreeView<>(rootNode);
		treeView.getSelectionModel().selectedItemProperty().addListener(daListener);
		VBox v = new VBox();
		v.setPadding(new Insets(10, 10, 10, 10));
		v.setSpacing(10);
		v.getChildren().add(treeView);
		VBox.setVgrow(treeView, Priority.ALWAYS);
		return v;
	}

	public static Dialog<ButtonType> getOpenJRSDialog() {
		Dialog<ButtonType> dialog = new Dialog<>();
		dialog.setResizable(true);
		dialog.getDialogPane().setContent(getJRSBox(dialog));
		dialog.getDialogPane().getButtonTypes().addAll(TypeOk, TypeCancel);

		return dialog;
	}

	private static GridPane getJRSBox(Dialog<ButtonType> dialog) {
		Configuration cfg = StaticBase.instance().getConfig();
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(20));
		gridPane.setHgap(25);
		gridPane.setVgap(15);

		Label l1 = new Label("JRS Host");
		Label l2 = new Label("JRS URL");
		Label l3 = new Label("JRS User");
		Label l4 = new Label("JRS Orga");
		Label l5 = new Label("JRS Password");
		JFXTextField hostField = new JFXTextField(cfg.getProperty("jrs.host"));
		JFXTextField urlField = new JFXTextField(cfg.getProperty("jrs.url"));
		JFXTextField usrField = new JFXTextField(cfg.getProperty("jrs.admin.user"));
		JFXTextField orgafield = new JFXTextField(cfg.getProperty("jrs.admin.orga"));
		JFXPasswordField pwField = new JFXPasswordField();

		gridPane.add(l1, 0, 0);
		gridPane.add(hostField, 1, 0);
		gridPane.add(l2, 0, 1);
		gridPane.add(urlField, 1, 1);
		gridPane.add(l3, 0, 2);
		gridPane.add(usrField, 1, 2);
		gridPane.add(l4, 0, 3);
		gridPane.add(orgafield, 1, 3);
		gridPane.add(l5, 0, 4);
		gridPane.add(pwField, 1, 4);

		dialog.setResultConverter(buttonType -> {
			if (buttonType == TypeOk) {
				cfg.setProperty("jrs.host", hostField.getText());
				cfg.setProperty("jrs.url", urlField.getText());
				cfg.setProperty("jrs.admin.user", usrField.getText());
				String orga = orgafield.getText();
				if (orga.isEmpty()) {
					cfg.remove("jrs.admin.orga");
				}
				else {
					cfg.setProperty("jrs.admin.orga", orga);
				}
				StaticBase.instance().reloadConfiguration(cfg);
				String pw = pwField.getText();
				if (pw != null && !pw.isEmpty())
					cfg.setProperty("", pwField.getText());
				return TypeOk;
			}
			else
				return TypeCancel;
		});

		return gridPane;
	}

}
