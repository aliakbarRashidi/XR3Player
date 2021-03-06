/*
 * 
 */
package application.windows;

import java.io.IOException;

import application.tools.InfoTool;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * The Class RenameWindow.
 */
public class EmotionsWindow extends BorderPane {
	
	@FXML
	private Button hate;
	
	@FXML
	private Button dislike;
	
	@FXML
	private Button neutral;
	
	@FXML
	private Button like;
	
	@FXML
	private Button love;
	
	@FXML
	private Label emotionLabel;
	
	// ----------------     
	
	/** The window */
	private Stage window = new Stage();
	
	/** The Emotion of the User */
	private Emotion emotion = Emotion.NEUTRAL;
	
	public static final Image hateImage = InfoTool.getImageFromResourcesFolder("angry.png");
	public static final Image dislikeImage = InfoTool.getImageFromResourcesFolder("dislike.png");
	public static final Image neutralImage = InfoTool.getImageFromResourcesFolder("likeFaded.png");
	public static final Image likeImage = InfoTool.getImageFromResourcesFolder("like.png");
	public static final Image loveImage = InfoTool.getImageFromResourcesFolder("love.png");
	
	/** The accepted. */
	private boolean accepted;
	
	/**
	 * Constructor
	 */
	public EmotionsWindow() {
		
		// Window
		window.setTitle("Rename Window");
		window.setWidth(216);
		window.setHeight(72);
		//window.initModality(Modality.APPLICATION_MODAL);
		window.initStyle(StageStyle.TRANSPARENT);
		window.getIcons().add(InfoTool.getImageFromResourcesFolder("icon.png"));
		window.centerOnScreen();
		window.setOnCloseRequest(WindowEvent::consume);
		window.setAlwaysOnTop(true);
		
		// ----------------------------------FXMLLoader
		FXMLLoader loader = new FXMLLoader(getClass().getResource(InfoTool.FXMLS + "EmotionsWindow.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		// ----------------------------------Scene
		window.setScene(new Scene(this, Color.TRANSPARENT));
		window.focusedProperty().addListener((observable , oldValue , newValue) -> {
			if (!newValue && window.isShowing())
				close(false);
		});
	}
	
	/**
	 * Called as soon as .fxml has been initialized
	 */
	@FXML
	private void initialize() {
		
		hate.setOnAction(a -> {
			emotion = Emotion.HATE;
			close(true);
		});
		dislike.setOnAction(a -> {
			emotion = Emotion.DISLIKE;
			close(true);
		});
		neutral.setOnAction(a -> {
			emotion = Emotion.NEUTRAL;
			close(true);
		});
		like.setOnAction(a -> {
			emotion = Emotion.LIKE;
			close(true);
		});
		love.setOnAction(a -> {
			emotion = Emotion.LOVE;
			close(true);
		});
		
		hate.setOnMouseEntered(m -> {
			emotion = Emotion.HATE;
			emotionLabel.setText(emotion.toString());
		});
		dislike.setOnMouseEntered(m -> {
			emotion = Emotion.DISLIKE;
			emotionLabel.setText(emotion.toString());
		});
		neutral.setOnMouseEntered(m -> {
			emotion = Emotion.NEUTRAL;
			emotionLabel.setText(emotion.toString());
		});
		like.setOnMouseEntered(m -> {
			emotion = Emotion.LIKE;
			emotionLabel.setText(emotion.toString());
		});
		love.setOnMouseEntered(m -> {
			emotion = Emotion.LOVE;
			emotionLabel.setText(emotion.toString());
		});
		
	}
	
	/**
	 * Show Window with the given parameters.
	 *
	 * @param n
	 *            the node
	 * 
	 */
	public void show(Node n) {
		
		// Auto Calculate the position
		Bounds bounds = n.localToScreen(n.getBoundsInLocal());
		//show(text, bounds.getMinX() + 5, bounds.getMaxY(), title);
		//System.out.println(bounds.getMinX() + " , " + getWidth() + " , " + bounds.getWidth() / 2);
		show(bounds.getMinX() - 216 / 2 + bounds.getWidth() / 2, bounds.getMaxY());
		
		//System.out.println(bounds.getMinX() + " , " + getWidth() + " , " + bounds.getWidth() / 2);
	}
	
	/**
	 * Show Window with the given parameters.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	private void show(double x , double y) {
		
		//Set once
		window.setX(x);
		window.setY(y);
		
		window.show();
		
		//Set it again
		if (x <= -1 && y <= -1)
			window.centerOnScreen();
		else {
			if (x + getWidth() > InfoTool.getScreenWidth())
				x = InfoTool.getScreenWidth() - getWidth();
			else if (x < 0)
				x = 0;
			
			if (y + getHeight() > InfoTool.getScreenHeight())
				y = InfoTool.getScreenHeight() - getHeight();
			else if (y < 0)
				y = 0;
			
			window.setX(x);
			window.setY(y);
			
			emotionLabel.setText("?");
		}
		
		//	
	}
	
	/**
	 * @return Whether or not this {@code Stage} is showing (that is, open on the user's system). The Stage might be "showing", yet the user might not
	 *         be able to see it due to the Stage being rendered behind another window or due to the Stage being positioned off the monitor.
	 * 
	 *
	 * @defaultValue false
	 */
	public ReadOnlyBooleanProperty showingProperty() {
		return window.showingProperty();
	}
	
	/**
	 * @return Whether or not this {@code Stage} is showing (that is, open on the user's system). The Stage might be "showing", yet the user might not
	 *         be able to see it due to the Stage being rendered behind another window or due to the Stage being positioned off the monitor.
	 * 
	 */
	public boolean isShowing() {
		return showingProperty().get();
	}
	
	/**
	 * @return the window
	 */
	public Stage getWindow() {
		return window;
	}
	
	/**
	 * @return the emotion
	 */
	public Emotion getEmotion() {
		return emotion;
	}
	
	/**
	 * @param emotion
	 *            the emotion to set
	 */
	public void setEmotion(Emotion emotion) {
		this.emotion = emotion;
	}
	
	/**
	 * Close the Window.
	 *
	 * @param accepted
	 *            True if accepted , False if not
	 */
	private void close(boolean accepted) {
	//	System.out.println("Emotions Window Close called with accepted := " + accepted);
		this.accepted = accepted;
		window.close();
	}
	
	/**
	 * Was accepted.
	 *
	 * @return true, if successful
	 */
	public boolean wasAccepted() {
		return accepted;
	}
	
	/**
	 * The user is passing a button and an emotion and this method sets the correct graphic based on the emotion given
	 * 
	 * @param button
	 * @param emotion
	 */
	public void giveEmotionImageToButton(Button button , Emotion emotion) {
		//Make sure it will run on JavaFX Thread
		Platform.runLater(() -> {
			if (emotion == Emotion.HATE)
				( (ImageView) button.getGraphic() ).setImage(EmotionsWindow.hateImage);
			else if (emotion == Emotion.DISLIKE)
				( (ImageView) button.getGraphic() ).setImage(EmotionsWindow.dislikeImage);
			else if (emotion == Emotion.NEUTRAL)
				( (ImageView) button.getGraphic() ).setImage(EmotionsWindow.neutralImage);
			else if (emotion == Emotion.LIKE)
				( (ImageView) button.getGraphic() ).setImage(EmotionsWindow.likeImage);
			else if (emotion == Emotion.LOVE)
				( (ImageView) button.getGraphic() ).setImage(EmotionsWindow.loveImage);
		});
	}
	
	/**
	 * This enum represents possible emotions a user may feel for a song
	 * 
	 * @author GOXR3PLUS
	 *
	 */
	public enum Emotion {
		
		HATE {
			@Override
			public String toString() {
				return "HATE";
			}
		},
		
		DISLIKE {
			@Override
			public String toString() {
				return "DISLIKE";
			}
		},
		NEUTRAL {
			@Override
			public String toString() {
				return "NEUTRAL";
			}
		},
		LIKE {
			@Override
			public String toString() {
				return "LIKE";
			}
		},
		LOVE {
			@Override
			public String toString() {
				return "LOVE";
			}
		}
	}
	
}
