package view;
import javafx.scene.layout.Pane;
import system.Statement;

public interface View{
  
  void init();
 
  public Pane getPane();
  
  public void showPane();
  
  /*
   * It allows model to deliver update request.
   */
  public <T> void updateStatement(String instruction,Statement<T> statement);
}
