package controllers;

import database.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import utils.DateUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageExport {
    @FXML
    CheckBox cbName;
    @FXML
    CheckBox cbRange;
    @FXML
    CheckBox cbCreator;
    @FXML
    CheckBox cbLogo;
    @FXML
    ImageView imageView;
    private Timeline activeTimeline;
    private WritableImage originalImage;
    private WritableImage previewImage;

    // Executes on startup (when export button is pressed when viewing a timeline)
    void setUp(WritableImage image, Timeline activeTimeline) {
        this.activeTimeline = activeTimeline;
        originalImage = image;
        burnIn();                     //defaults to having LyfeLine logo watermarked, can be toggled by user
    }

    // Executes when "Export" button is pressed in the pop-up
    @FXML
    void saveImage() throws IOException {
        BufferedImage finalBuffer = SwingFXUtils.fromFXImage(previewImage, null);
        File outputFile = fileChooser();
        if (outputFile != null)
            ImageIO.write(finalBuffer, "png", outputFile);
    }

    // execute when any checkbox is clicked
    @FXML
    void burnIn() {
        previewImage = originalImage;

        if (cbName.isSelected())
            burnName();
        if (cbRange.isSelected())
            burnRange();
        if (cbCreator.isSelected())
            burnCreator();
        if (cbLogo.isSelected())
            burnLogo();

        imageView.setImage(previewImage);
    }

    private void burnName() {
        String text = activeTimeline.getName();
        burnText(text, 1.0 / 10, false);
    }

    private void burnCreator() {
        String text = "Made with LyfeLine by: " + GUIManager.loggedInUser.getUserName();
        burnText(text, 29.0 / 30, false);
    }

    private void burnRange() {
        String text = DateUtil.ddmmyyToString(activeTimeline);
        burnText(text, 29.0 / 30, true);
    }

    private void burnText(String text, double yPlacementRatio, boolean adjustDown) {
        //initializes necessary graphic properties
        BufferedImage originalBuffer = SwingFXUtils.fromFXImage(previewImage, null);
        Graphics2D workingImage = (Graphics2D) originalBuffer.getGraphics();
        workingImage.drawImage(originalBuffer, 0, 0, null);
        workingImage.setColor(Color.BLACK);
        workingImage.setFont(new Font(Font.SANS_SERIF, Font.BOLD, originalBuffer.getHeight() / 30));
        Rectangle2D rect = workingImage.getFontMetrics().getStringBounds(text, workingImage);

        //calculate position of text
        int xPlacement = (int) (previewImage.getWidth() - rect.getWidth()) / 2;     //centered
        int yPlacement = (int) (previewImage.getHeight() * yPlacementRatio);
        if (adjustDown)   //when placing two fields near each other, one should be adjusted downwards so they don't overlap
            yPlacement -= rect.getHeight();

        //add text overlay to the image
        workingImage.drawString(text, xPlacement, yPlacement);
        previewImage = SwingFXUtils.toFXImage(originalBuffer, null);
        workingImage.dispose();
    }

    private void burnLogo() {
        try {
            //Logo settings
            BufferedImage logoBuffer = resize(ImageIO.read(new File("src/main/resources/Logo.png")));

            //initializes necessary graphic properties
            BufferedImage originalBuffer = SwingFXUtils.fromFXImage(previewImage, null);
            Graphics2D workingImage = (Graphics2D) originalBuffer.getGraphics();
            workingImage.drawImage(originalBuffer, 0, 0, null);
            workingImage.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

            //calculates the coordinate where the String is painted
            int yPlacement = (originalBuffer.getHeight() - originalBuffer.getHeight() / 15);
            int xPlacement = (originalBuffer.getWidth() / 100);

            //add text watermark to the image
            workingImage.drawImage(logoBuffer, xPlacement, yPlacement, null);
            previewImage = SwingFXUtils.toFXImage(originalBuffer, null);
            workingImage.dispose();
        } catch (IOException e) {
            System.err.println("Logo.png file not found.");
        }
    }

    //at the moment resizes the logo to hardcoded 100x100, seems to work well and the logo watermark should be reasonably small.
    private BufferedImage resize(BufferedImage img) {
        final int width = 104;
        final int height = 40;
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    public File fileChooser() {
        FileChooser fileChooser = new FileChooser();                                             //removes illegal chars for file name from default name
        fileChooser.setInitialFileName(activeTimeline.getName().replaceAll("\\s+", "_").replaceAll("[/:*?\"<>|\\\\]", ""));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.jpg", "*.jpeg", "*.png", "*.bmp", "*.gif", "*.wbmp"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"), new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"), new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"), new FileChooser.ExtensionFilter("WBMP", "*.wbmp"));

        // Show save file dialog
        return fileChooser.showSaveDialog(GUIManager.mainStage);
    }
}
