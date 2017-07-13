package view;
import javafx.scene.layout.Pane;
import system.Statement;

public interface View{
  
  void init();
 
  public Pane getPane();
  
  public void showPane();
  
  public <T> void updateStatement(String instruction,Statement<T> statement);
}
