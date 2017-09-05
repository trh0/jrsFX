package de.quinscape.jrsfx.ui.components;

import de.quinscape.jrsfx.controller.StaticBase;
import de.quinscape.jrsfx.jasper.RepoTreeResource;
import de.quinscape.jrsfx.jasper.ResourceMediaType;
import de.quinscape.jrsfx.ui.Images;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

/**
 * A {@link javafx.scene.control.TreeItem} containing path values.
 * 
 * @author trh0 - TKoll <br>
 * 
 * 
 */
public class RepoResourceTreeItem
		extends TreeItem<String> {
	// TODO Change to suit the applications needs!
	// this stores the full path to the file or directory
	private final RepoTreeResource details;
	public static final Number imgSize = 8;
	private final String overrideType;

	public RepoTreeResource getDetails() {
		return this.details;
	}

	private boolean wasExpanded = false;

	public RepoResourceTreeItem(String value, RepoTreeResource resource, String overrideType) {
		super(value);
		this.details = resource;
		this.overrideType = overrideType;
		init(resource, overrideType);
		addHandlers();
	}

	public RepoResourceTreeItem(String value, RepoTreeResource resource) {
		super(value);
		this.overrideType = resource.getResourceType();
		this.details = resource;
		init(resource, overrideType);
		addHandlers();
	}

	public RepoResourceTreeItem(String value, javafx.scene.Node graphic) {
		super(value, graphic);
		this.overrideType = ResourceMediaType.FOLDER_TYPE;
		this.details = null;
		init(null, overrideType);
		addHandlers();
	}

	public RepoResourceTreeItem(String value, RepoTreeResource resource, javafx.scene.Node graphic) {
		super(value, graphic);
		this.overrideType = resource.getResourceType();
		this.details = resource;
		init(resource, overrideType);
		addHandlers();
	}

	public RepoResourceTreeItem(String value, RepoTreeResource resource, String overrideType,
			javafx.scene.Node graphic) {
		super(value, graphic);
		this.details = resource;
		this.overrideType = overrideType;
		init(resource, overrideType);
		addHandlers();
	}

	/**
	 * Init 1 - Building the TreeItem and searching for children.
	 * 
	 * @author trh0 - TKoll
	 *
	 * @param resource
	 * @param type
	 */
	private void init(RepoTreeResource resource, String type) {
		if (resource != null) {
			if (ResourceMediaType.FOLDER_TYPE.equals(resource.getResourceType())) {
				this.setGraphic(Images.FOLDER_COLLAPSED.getImageView(imgSize, imgSize));
			}
			else {
				this.setGraphic(Images.FILE_ICON.getImageView(imgSize, imgSize));
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
				RepoResourceTreeItem source = RepoResourceTreeItem.class.cast(e.getTreeItem());
				if (ResourceMediaType.FOLDER_TYPE.equals(overrideType)) {
					ImageView iv = (ImageView) source.getGraphic();
					iv.setImage(Images.FOLDER_EXPANDED.getImage(imgSize, imgSize));
				}

			});
		});

		this.addEventHandler(TreeItem.branchCollapsedEvent(), e -> {
			StaticBase.instance().getUiThread().runTask(() -> {
				RepoResourceTreeItem source = RepoResourceTreeItem.class.cast(e.getTreeItem());
				if (true) {
					ImageView iv = (ImageView) source.getGraphic();
					iv.setImage(Images.FOLDER_COLLAPSED.getImage(imgSize, imgSize));
				}
			});
		});
	}

}
