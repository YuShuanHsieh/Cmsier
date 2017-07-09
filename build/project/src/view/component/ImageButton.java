package view.component;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import view.View;
import javafx.scene.paint.Color;

public class ImageButton extends Button {
  
  private Image normal;
  private Image active;
  ImageView imgView;
  Text buttonText;
  
  public ImageButton(String normalImgPath, String activeImgPath, String ImgTitle) {
    super();
    normal = new Image(View.class.getResourceAsStream(normalImgPath));
    active = new Image(View.class.getResourceAsStream(activeImgPath));
    VBox buttonFrame = new VBox(5);
    buttonFrame.setAlignment(Pos.CENTER); 
    buttonText = new Text(ImgTitle);
    buttonText.setFill(Color.GRAY);
    buttonText.setFont(new Font(10));
    imgView = new ImageView();
    imgView.setImage(normal);
    buttonFrame.getChildren().addAll(imgView, buttonText);
    this.setGraphic(buttonFrame); 
    this.setOnMouseEntered(this::onMouseMoveOnEvent);
    this.setOnMouseExited(this::onMouseMoveOutEvent);
  }
  
  private void onMouseMoveOutEvent(MouseEvent event) {
    imgView.setImage(normal);
    buttonText.setFill(Color.GRAY);
  }
  
  private void onMouseMoveOnEvent(MouseEvent event) {
    imgView.setImage(active);
    buttonText.setFill(Color.web("#004a80"));
  }
}
