package controller;
import view.View;
import javafx.scene.layout.Pane;
import model.Model;

public interface Controller {
  
  public void init();

  public void attached(View view, Model model);
  
  public void setEvent();
  
  public Pane getView();
  
  public void setParent(Controller parent);
}
