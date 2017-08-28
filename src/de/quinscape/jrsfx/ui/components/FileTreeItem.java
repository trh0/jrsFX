package de.quinscape.jrsfx.ui.components;

import java.io.File;

import de.quinscape.jrsfx.controller.StaticBase;
import de.quinscape.jrsfx.ui.Images;
import de.quinscape.jrsfx.util.ApplicationIO;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

/**
 * A {@link javafx.scene.control.TreeItem} containing path values.
 * 
 * @author trh0 - TKoll
 *
 */
public class FileTreeItem
		extends TreeItem<String> {

	// this stores the full path to the file or directory
	private String fullPath;
	private File file;
	public static final Number size = 8;

	public String getFullPath() {
		return this.fullPath;
	}

	private boolean wasExpanded = false;

	public File getFile() {
		return this.file;
	}

	private boolean isDirectory;

	public boolean isDirectory() {
		return this.isDirectory;
	}

	public FileTreeItem(File aFile, boolean pragmaOnce) {
		super(aFile.toPath().toString());
		this.file = aFile;
		init(file, pragmaOnce);
		addHandlers();
	}

	public FileTreeItem(File file) {
		super(file.toPath().toString());
		this.file = file;
		init(file, true);
		addHandlers();
	}

	/**
	 * Init 1 - Building the TreeItem and searching for children.
	 * 
	 * @author trh0 - TKoll
	 *
	 * @param file
	 *            Path to aquire children from.
	 * @param once
	 *            NOT recursively, if TRUE.
	 */
	private void init(File file, boolean once) {
		this.fullPath = file.toPath().toString();

		// test if this is a directory and set the icon
		if (file.isDirectory()) {
			this.isDirectory = true;
			this.setGraphic(Images.FOLDER_COLLAPSED.getImageView(size, size));
		}
		else {
			this.isDirectory = false;
			this.setGraphic(Images.FILE_ICON.getImageView(size, size));
			// if you want different icons for different file types this is
			// where you'd do it
		}
		// set the value
		if (!fullPath.endsWith(File.separator)) {
			// set the value (which is what is displayed in the tree)
			String value = file.toString();
			int indexOf = value.lastIndexOf(File.separator);
			if (indexOf > 0) {
				this.setValue(value.substring(indexOf + 1));
			}
			else {
				this.setValue(value);
			}
		}
		if (this.isDirectory && file.canRead() && !once) {
			for (File f : file.listFiles()) {
				this.getChildren().add(new FileTreeItem(f));
			}
		}

	}

	/**
	 * Init 2 - Adding EventHandlers to the TreeItem.
	 * 
	 * @author trh0 - TKoll
	 *
	 */
	private void addHandlers() {
		this.addEventHandler(TreeItem.branchExpandedEvent(), e -> {
			StaticBase.instance().getUiThread().runTask(() -> {
				FileTreeItem source = FileTreeItem.class.cast(e.getTreeItem());
				if (source.isDirectory() && source.isExpanded()) {
					ImageView iv = (ImageView) source.getGraphic();
					iv.setImage(Images.FOLDER_EXPANDED.getImage(size, size));
				}
				ObservableList<TreeItem<String>> children = source.getChildren();
				synchronized (children) {

					if (!children.isEmpty() && !source.wasExpanded) {
						source.wasExpanded = true;
						try {
							children.forEach(child -> {
								FileTreeItem it = (FileTreeItem) child;
								File f = it.getFile();
								if (f.isDirectory() && f.canRead()) {
									File[] fls = f.listFiles();
									if (fls != null) {
										for (File fl : fls) {

											FileTreeItem treeNode = new FileTreeItem(fl);

											ObservableList<TreeItem<String>> childsChilds = child.getChildren();
											synchronized (childsChilds) {
												childsChilds.add(treeNode);
											}
										}
									}
								}
							});
						}
						catch (Exception ex) {
							ApplicationIO.toErrorStream(ex);
						}
					}
				}

			});
		});

		this.addEventHandler(TreeItem.branchCollapsedEvent(), e -> {
			StaticBase.instance().getUiThread().runTask(() -> {
				FileTreeItem source = FileTreeItem.class.cast(e.getTreeItem());
				if (source.isDirectory() && !source.isExpanded()) {
					ImageView iv = (ImageView) source.getGraphic();
					iv.setImage(Images.FOLDER_COLLAPSED.getImage(size, size));
				}
			});
		});
	}

}
