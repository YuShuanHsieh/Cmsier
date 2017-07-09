package view;
import controller.Controller;
import javafx.scene.layout.Pane;

public interface View{
  
  void init();
  
  void update();
  
  void setController(Controller controller);
  
  Pane getPane();
  
  void showPane();
}
