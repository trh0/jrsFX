package de.quinscape.jrsfx.ui;

import java.io.IOException;
import java.io.InputStream;

import de.quinscape.jrsfx.util.ApplicationIO;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Application's images in an cnum.
 * 
 * @author trh0 - TKoll
 *
 */
public enum Images {

	add("add.png"),
	check_empty("check-empty.png"),
	check("check.png"),
	close("close.png"),
	copy("copy.png"),
	cut("cut.png"),
	delete("delete.png"),
	edit("edit.png"),
	filter("filter.png"),
	folder_filled_closed("folder-filled-closed.png"),
	folder_filled_open("folder-filled-open.png"),
	folder_line_closed("folder-line-closed.png"),
	folder_line_open("folder-line-open.png"),
	hide("hide.png"),
	new_file_filled("new-file-filled.png"),
	new_file_shape("new-file-shape.png"),
	pref_closed("pref-closed.png"),
	pref_open("pref-open.png"),
	redo("redo.png"),
	refresh("refresh.png"),
	remove("remove.png"),
	save("save.png"),
	show("show.png"),
	sort_az("sort-az.png"),
	sort_za("sort-za.png"),
	text_filled("text-filled.png"),
	text_shape("text-shape.png"),
	undo("undo.png"),
	zoom_in("zoom-in.png"),
	zoom_out("zoom-out.png"),
	FILE_ICON("icon/file.png"),
	FOLDER_COLLAPSED("icon/folder_closed.png"),
	FOLDER_EXPANDED("icon/folder_open.png"),
	HOST("icon/root.png"),;

	private final String location;

	/**
	 * 
	 * @author trh0 - TKoll
	 *
	 * @param width
	 *            The width the image will be loaded with.
	 * @param height
	 *            The height the image will be loaded with.
	 * @return The {@link javafx.scene.image.Image} loaded from enum's String.
	 */
	public Image getImage(Number width, Number height) {
		Image image = null;
		try (InputStream is = Images.class.getClassLoader().getResourceAsStream("img/" + this.location)) {
			if (width != null && height != null) {
				image = new Image(is, width.doubleValue(), height.doubleValue(), true, true);
			}
			else {
				image = new Image(is);
			}
		}
		catch (IOException e) {
			ApplicationIO.toErrorStream(e);
		}
		return image;
	}

	public Image getImage() {
		return this.getImage(null, null);
	}

	/**
	 * 
	 * @author trh0 - TKoll
	 *
	 * @param width
	 *            The width the image will be loaded with.
	 * @param height
	 *            The height the image will be loaded with.
	 * @return The {@link javafx.scene.image.Image} saved in the assets wrapped
	 *         into a {@link javafx.scene.image.ImageView}.
	 */
	public ImageView getImageView(Number width, Number height) {
		ImageView iv = new ImageView();
		iv.setImage(this.getImage(width, height));
		iv.setPreserveRatio(true);
		return iv;
	}

	/**
	 * 
	 * @author trh0 - TKoll
	 *
	 * @param fitHeight
	 *            Will be the value for
	 *            {@link ImageView#setFitHeight(double)}<br>
	 *            {@link ImageView#setPreserveRatio(boolean)} will be true.
	 * @return
	 */
	public ImageView getImageView(Number fitHeight) {
		ImageView iv = new ImageView();
		iv.setImage(this.getImage());
		iv.setPreserveRatio(true);
		iv.setFitHeight(fitHeight.doubleValue());
		return iv;
	}

	private Images(String locaiton) {
		this.location = locaiton;

	}

}
