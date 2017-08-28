package de.quinscape.jrsfx.ui.components;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import de.quinscape.jrsfx.ui.Images;
import de.quinscape.jrsfx.util.ApplicationIO;
import de.quinscape.jrsfx.util.Messages;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
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
		VBox b = filePathBrowser(dialog, fromPath);
		dialog.getDialogPane().setContent(b);
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

}
