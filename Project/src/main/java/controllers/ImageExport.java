package controllers;

import database.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import utils.DateUtil;
import javax.imageio.ImageIO;

public class ImageExport {
	public ChoiceBox choiceBoxAlignment;
	public CheckBox cbName;
	public CheckBox cbRange;
	public CheckBox cbLogo;
	public Button btnExport;
	public RadioButton rdbtnPng;
	public RadioButton rdbtnJpeg;
	public ToggleGroup formatChoice;
	public ImageView imageView;
	private File filechooser;
	private WritableImage image;
	private WritableImage originalImage;
	private Timeline activeTimeline;
	private boolean logo, name, range, creator;
	private String format;

	public void initialize() {
		choiceBoxAlignment.getItems().setAll("Top Left", "Top Right", "Bottom Left", "Bottom Right");
		choiceBoxAlignment.getSelectionModel().select(0);
	}

	// Executes on startup (when export button is pressed when viewing a timeline)
	public void setUp(WritableImage image, Timeline activeTimeline) {
		this.image = image;
		this.activeTimeline = activeTimeline;
		originalImage = image;
		imageView.setImage(this.image);
	}

	// Executes when "Export" button is pressed in the pop-up
	public void export(ActionEvent actionEvent) {
		burnIn();
		// fileChooser();

	}

	public void fileChooser() {
		FileChooser fileChooser = new FileChooser();
		String format = ".png";
		if (!rdbtnPng.isSelected())
			format = ".jpeg";
		fileChooser.setInitialFileName(activeTimeline.getName().replaceAll("\\s+", "_") + format); // We will add read
																									// format from
																									// dropdown or use
																									// png
		fileChooser.getExtensionFilters().addAll( // keep all formats now, easy to add to the popup
				new FileChooser.ExtensionFilter("All Images", "*.jpg", "*.jpeg", "*.png", "*.bmp", "*.gif", "*.wbmp"),
				new FileChooser.ExtensionFilter("JPG", "*.jpg"), new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
				new FileChooser.ExtensionFilter("PNG", "*.png"), new FileChooser.ExtensionFilter("BMP", "*.bmp"),
				new FileChooser.ExtensionFilter("GIF", "*.gif"), new FileChooser.ExtensionFilter("WBMP", "*.wbmp"));

		// Show save file dialog
		filechooser = fileChooser.showSaveDialog(GUIManager.mainStage);

	}

	public void burnImage(WritableImage burnedImage) {
		this.image = burnedImage;
		imageView.setImage(this.image);
	}

	private void burnIn() {
		burnName();
		burnRange();
		burnCreator();

	}

	private void burnName() {
		String text = activeTimeline.getName();
		BufferedImage originalBuffer = SwingFXUtils.fromFXImage(this.image, null);
		int defaultFont = originalBuffer.getHeight() / 15;
		int font = defaultFont;

		// determine image type and handle correct transparency
		int imageType = "png".equalsIgnoreCase(format) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
		BufferedImage burned = new BufferedImage(originalBuffer.getWidth(), originalBuffer.getHeight(), imageType);

		// initializes necessary graphic properties
		Graphics2D w = (Graphics2D) burned.getGraphics();
		w.drawImage(originalBuffer, 0, 0, null);
		w.setColor(Color.BLACK);
		w.setFont(new Font(Font.SANS_SERIF, Font.BOLD, font));
		FontMetrics fontMetrics = w.getFontMetrics();
		Rectangle2D rect = fontMetrics.getStringBounds(text, w);

		// calculate center of the image
		int yPlacement = burned.getHeight() / 10;
		int xPlacement = (burned.getWidth() - (int) rect.getWidth()) / 2;

		// add text overlay to the image
		w.drawString(text, xPlacement, yPlacement);
		WritableImage imageBurned = SwingFXUtils.toFXImage(burned, null);
		w.dispose();
		burnImage(imageBurned);

	}

	private void burnRange() {
		String text = DateUtil.ddmmyyToString(activeTimeline);

		BufferedImage originalBuffer = SwingFXUtils.fromFXImage(this.image, null);
		int defaultFont = originalBuffer.getHeight() / 30;
		int font = defaultFont;

		// determine image type and handle correct transparency
		int imageType = "png".equalsIgnoreCase(format) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
		BufferedImage burned = new BufferedImage(originalBuffer.getWidth(), originalBuffer.getHeight(), imageType);

		// initializes necessary graphic properties
		Graphics2D w = (Graphics2D) burned.getGraphics();
		w.drawImage(originalBuffer, 0, 0, null);
		w.setColor(Color.BLACK);
		w.setFont(new Font(Font.SANS_SERIF, Font.BOLD, font));
		FontMetrics fontMetrics = w.getFontMetrics();
		Rectangle2D rect = fontMetrics.getStringBounds(text, w);

		// calculate center of the image
		int yPlacement = (burned.getHeight() - burned.getHeight() / 30) - (int) rect.getHeight();
		int xPlacement = (burned.getWidth() - (int) rect.getWidth()) / 2;

		// add text overlay to the image
		w.drawString(text, xPlacement, yPlacement);
		WritableImage imageBurned = SwingFXUtils.toFXImage(burned, null);
		w.dispose();
		burnImage(imageBurned);

	}

	private void burnLogo() {

	}

	private void burnCreator() {

		String text = "Made on LyfeLine by: " + GUIManager.loggedInUser.getUserName();

		BufferedImage originalBuffer = SwingFXUtils.fromFXImage(this.image, null);
		int defaultFont = originalBuffer.getHeight() / 30;
		int font = defaultFont;

		// determine image type and handle correct transparency
		int imageType = "png".equalsIgnoreCase(format) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
		BufferedImage burned = new BufferedImage(originalBuffer.getWidth(), originalBuffer.getHeight(), imageType);

		// initializes necessary graphic properties
		Graphics2D w = (Graphics2D) burned.getGraphics();
		w.drawImage(originalBuffer, 0, 0, null);
		w.setColor(Color.BLACK);
		w.setFont(new Font(Font.SANS_SERIF, Font.BOLD, font));
		FontMetrics fontMetrics = w.getFontMetrics();
		Rectangle2D rect = fontMetrics.getStringBounds(text, w);

		// calculate center of the image
		int yPlacement = (burned.getHeight() - burned.getHeight() / 30);
		int xPlacement = (burned.getWidth() - (int) rect.getWidth()) / 2;

		// add text overlay to the image
		w.drawString(text, xPlacement, yPlacement);
		WritableImage imageBurned = SwingFXUtils.toFXImage(burned, null);
		w.dispose();
		burnImage(imageBurned);

	}

}
