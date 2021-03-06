package smartcontroller.modes;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Main;
import application.presenter.treeview.FileTreeItem;
import application.tools.InfoTool;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import smartcontroller.presenter.SmartController;
import smartcontroller.services.FoldersModeService;

public class SmartControllerFoldersMode extends StackPane {
	
	//--------------------------------------------------------------
	
	@FXML
	private TreeView<String> treeView;
	
	@FXML
	private Label detailsLabel;
	
	@FXML
	private VBox indicatorVBox;
	
	@FXML
	private ProgressIndicator progressIndicator;
	
	@FXML
	private Button collapseTree;
	
	// -------------------------------------------------------------
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());
	
	// -------------------------------------------------------------
	
	private final FoldersModeService service = new FoldersModeService(this);
	
	/** A private instance of the SmartController it belongs */
	private final SmartController smartController;
	
	// -------------------------------------------------------------
	
	private final FileTreeItem root = new FileTreeItem("root");
	
	// -------------------------------------------------------------
	
	/**
	 * Constructor.
	 */
	public SmartControllerFoldersMode(SmartController smartController) {
		this.smartController = smartController;
		
		// ------------------------------------FXMLLOADER ----------------------------------------
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "SmartControllerFoldersMode.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}
		
	}
	
	/**
	 * Called as soon as .FXML is loaded from FXML Loader
	 */
	@FXML
	private void initialize() {
		
		//TreeView
		treeView.setRoot(root);
		treeView.setShowRoot(false);
		
		// Mouse Released Event
		treeView.setOnMouseReleased(this::treeViewMouseReleased);
		
		// Drag Implementation
		treeView.setOnDragDetected(event -> {
			FileTreeItem source = (FileTreeItem) treeView.getSelectionModel().getSelectedItem();
			
			//The host is not allowed
			if (source != null && source != root) {
				
				// Allow this transfer Mode
				Dragboard board = startDragAndDrop(TransferMode.LINK);
				
				// Put a String on DragBoard
				ClipboardContent content = new ClipboardContent();
				content.putFiles(Arrays.asList(new File(source.getFullPath())));
				
				board.setContent(content);
				event.consume();
			}
		});
		
		//indicatorVBox
		indicatorVBox.visibleProperty().bind(service.runningProperty());
		
		//Progress Indicator
		progressIndicator.progressProperty().bind(service.progressProperty());
		
		//collapseTree
		collapseTree.setOnAction(a -> {
			//Trick for CPU based on this question -> https://stackoverflow.com/questions/15490268/manually-expand-collapse-all-treeitems-memory-cost-javafx-2-2
			root.setExpanded(false);
			
			//Set not expanded all the children
			collapseTreeView(root, false);
			
			//Trick for CPU
			root.setExpanded(true);
		});
		
	}
	
	/**
	 * Collapses the whole TreeView
	 * 
	 * @param item
	 */
	private void collapseTreeView(TreeItem<String> item , boolean expanded) {
		if (item == null || item.isLeaf())
			return;
		
		item.setExpanded(expanded);
		item.getChildren().forEach(child -> collapseTreeView(child, expanded));
	}
	
	/**
	 * Recreates the tree from the bottom based on the SmartController 1)Settings 2)Files
	 */
	public void recreateTree() {
		
		//Clear all the children
		root.getChildren().clear();
		
		//Start the Service
		service.restart();
	}
	
	/**
	 * Used for TreeView mouse released event
	 * 
	 * @param mouseEvent
	 *            [[SuppressWarningsSpartan]]
	 */
	private void treeViewMouseReleased(MouseEvent mouseEvent) {
		//Get the selected item
		FileTreeItem source = (FileTreeItem) treeView.getSelectionModel().getSelectedItem();
		
		// host is not on the game
		if (source == null || source == root) {
			mouseEvent.consume();
			return;
		}
		
		if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 1) {
			
			// source is expanded
			if (!source.isExpanded() && source.getChildren().isEmpty()) {
				//if (source.isDirectory())
				//source.setGraphic(new ImageView(SystemRoot.openedFolderImage));
				
				//Check if the TreeItem has not children yet
				if (source.getChildren().isEmpty()) {
					
					//Main Path
					Path mainPath = Paths.get(source.getFullPath());
					
					// directory?				
					if (mainPath.toFile().isDirectory())
						try (DirectoryStream<Path> stream = Files.newDirectoryStream(mainPath)) {
							
							//Run the Stream
							stream.forEach(path -> {
								
								// File or Directory is Hidden? + Directory or Accepted File
								if (!path.toFile().isHidden() && ( path.toFile().isDirectory() || InfoTool.isAudioSupported(path.toFile().getAbsolutePath()) )) {
									FileTreeItem treeNode = new FileTreeItem(path.toString());
									source.getChildren().add(treeNode);
								}
								
							});
							
						} catch (IOException x) {
							x.printStackTrace();
						}
					
				} else {
					// if you want to implement rescanning a
					// directory
					// for
					// changes this would be the place to do it
				}
				source.setExpanded(true);
			}
			
			// if (!source.isExpanded() && source.isDirectory())
			// source.setGraphic(new ImageView(SystemRoot.folderImage))
			
		} else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
			Main.treeViewContextMenu.show(source.getFullPath(), mouseEvent.getScreenX(), mouseEvent.getScreenY());
		}
	}
	
	/**
	 * @return the smartController
	 */
	public SmartController getSmartController() {
		return smartController;
	}
	
	/**
	 * @return the root of the tree
	 */
	public FileTreeItem getRoot() {
		return root;
	}
	
	/**
	 * @return the progressIndicator
	 */
	public ProgressIndicator getProgressIndicator() {
		return progressIndicator;
	}
	
	/**
	 * @param progressIndicator
	 *            the progressIndicator to set
	 */
	public void setProgressIndicator(ProgressIndicator progressIndicator) {
		this.progressIndicator = progressIndicator;
	}
	
	/**
	 * @return the detailsLabel
	 */
	public Label getDetailsLabel() {
		return detailsLabel;
	}
	
}
