package controllers;

import database.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import java.io.File;

public class ImageExport {
    public ChoiceBox choiceBoxAlignment;
    public CheckBox cbName;
    public CheckBox cbRange;
    public CheckBox cbLogo;
    public Button btnExport;
    public RadioButton rdbtnPng;
    public RadioButton rdbtnJpeg;
    public ToggleGroup format;
    public ImageView imageView;

    private WritableImage image;
    private Timeline activeTimeline;


    public void setUp(WritableImage image, Timeline activeTimeline) {
        this.image = image;
        this.activeTimeline = activeTimeline;
        imageView.setImage(this.image);
        System.out.println(activeTimeline.getName());
    }



    public void initialize() {
        choiceBoxAlignment.getItems().setAll("Top Left", "Top Right", "Bottom Left", "Bottom Right");
        choiceBoxAlignment.getSelectionModel().select(0);
    }

 public File fileChooser() {
  FileChooser fileChooser = new FileChooser();
  String format = ".png";
  if(!rdbtnPng.isSelected())
    format = ".jpeg";
  fileChooser.setInitialFileName(activeTimeline.getName().replaceAll("\\s+", "_") + format); //We will add read format from dropdown or use png
  fileChooser.getExtensionFilters().addAll( //keep all formats now, easy to add to the popup
          new FileChooser.ExtensionFilter("All Images", "*.jpg", "*.jpeg", "*.png", "*.bmp", "*.gif", "*.wbmp"),
          new FileChooser.ExtensionFilter("JPG", "*.jpg"),
          new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
          new FileChooser.ExtensionFilter("PNG", "*.png"),
          new FileChooser.ExtensionFilter("BMP", "*.bmp"),
          new FileChooser.ExtensionFilter("GIF", "*.gif"),
          new FileChooser.ExtensionFilter("WBMP", "*.wbmp")
  );

  //Show save file dialog
  File file = fileChooser.showSaveDialog(GUIManager.mainStage);
  return file;
 }


    public void export(ActionEvent actionEvent) {
        fileChooser();
    }
}
