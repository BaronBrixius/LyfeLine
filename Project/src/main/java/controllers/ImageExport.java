package controllers;

import database.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import utils.DateUtil;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageExport {
	public CheckBox cbName;
	public CheckBox cbRange;
	public CheckBox cbLogo;
	public CheckBox cbCreator;
	public Button btnExport;
	public RadioButton rdbtnPng;
	public RadioButton rdbtnJpeg;
	public ToggleGroup formatChoice;
	public ImageView imageView;

	private Timeline activeTimeline;
	private WritableImage originalImage;
	private WritableImage temp;
	private File filechooser;
	private String format;

	public void initialize() {
	}

	// Executes on startup (when export button is pressed when viewing a timeline)
	public void setUp(WritableImage image, Timeline activeTimeline) {
		this.activeTimeline = activeTimeline;
		originalImage = image;
		imageView.setImage(this.originalImage);
	}

	// Executes when "Export" button is pressed in the pop-up
	public void export(ActionEvent actionEvent) throws IOException {
		saveImage();

	}

	public void saveImage() throws IOException {
		String FinalFormat = "PNG";
		if (!rdbtnPng.isSelected())
			FinalFormat = "JPEG";
		BufferedImage finalBuffer = SwingFXUtils.fromFXImage(temp, null);
		ImageIO.write(finalBuffer, FinalFormat, fileChooser());
	}
	// execute when the checkbox is clicked

	public void cbNameClicked(ActionEvent actionEvent) {
		burnIn();
	}

	public void cbRangeClicked(ActionEvent actionEvent) {
		burnIn();
	}

	public void cbCreatorClicked(ActionEvent actionEvent) {
		burnIn();
	}

	public void cbLogoClicked(ActionEvent actionEvent) {
		burnIn();

	}


	private void burnIn() {
		 temp = originalImage;

		if (cbName.isSelected()) {
			temp = burnName(temp);
		}
		if (cbRange.isSelected()) {
			temp = burnRange(temp);
		}
		if (cbCreator.isSelected()) {
			temp = burnCreator(temp);
		}
		if (cbLogo.isSelected()) {
			try {
				temp = burnLogo(temp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		imageView.setImage(temp);

	}

	private WritableImage burnName(WritableImage img) {
		String text = activeTimeline.getName();
		BufferedImage originalBuffer = SwingFXUtils.fromFXImage(img, null);
		int defaultFont = (int) img.getHeight() / 30;
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
		return imageBurned;

	}

	private WritableImage burnRange(WritableImage img) {
		String text = DateUtil.ddmmyyToString(activeTimeline);

		BufferedImage originalBuffer = SwingFXUtils.fromFXImage(img, null);
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
		return imageBurned;

	}

	private WritableImage burnLogo(WritableImage img) throws IOException {

		BufferedImage originalBuffer = SwingFXUtils.fromFXImage(img, null);
		// Logo settings
		// File logo = new File("../resources/Logo.png");
		File logo = new File("src/main/resources/Logo.png");
		BufferedImage logoBuffer = resize(ImageIO.read(logo));
		// determine image type and handle correct transparency
		int imageType = "png".equalsIgnoreCase("png") ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
		BufferedImage burned = new BufferedImage(originalBuffer.getWidth(), originalBuffer.getHeight(), imageType);

		// initializes necessary graphic properties
		Graphics2D w = (Graphics2D) burned.getGraphics();
		w.drawImage(originalBuffer, 0, 0, null);
		AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
		w.setComposite(alphaChannel);

		// calculates the coordinate where the String is painted
		int yPlacement = (burned.getHeight()) - ((burned.getHeight() / 6));
		// int yPlacement = (burned.getHeight() - burned.getHeight() / 30);
		int xPlacement = (burned.getWidth() / 100);

		// add text watermark to the image
		w.drawImage(logoBuffer, xPlacement, yPlacement, null);
		WritableImage imageBurned = SwingFXUtils.toFXImage(burned, null);
		w.dispose();
		return imageBurned;

	}

	private WritableImage burnCreator(WritableImage img) {

		String text = "Made with LyfeLine by: " + GUIManager.loggedInUser.getUserName();

		BufferedImage originalBuffer = SwingFXUtils.fromFXImage(img, null);
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
		return imageBurned;

	}

	// at the moment resizes the logo to harcoded 100x100, seems to work well and
	// the logo watermark should be reasonably small.
	private BufferedImage resize(BufferedImage img) {
		int width = 104;
		int height = 40;
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resized;
	}

	public File fileChooser() throws IOException {
		FileChooser fileChooser = new FileChooser();
		 format = ".png";
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
		 return filechooser =  fileChooser.showSaveDialog(GUIManager.mainStage);


	}

}
