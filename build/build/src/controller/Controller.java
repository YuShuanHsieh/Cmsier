package controller;
import view.View;
import javafx.scene.layout.Pane;
import model.Model;
import system.SystemManager;

public abstract class Controller {

  protected View view;
  protected Model model;
  protected SystemManager systemManager;
  
  public void attached(View view, Model model) {
    view.setController(this);
    model.setController(this);
  }
  
  public void setSystemManager(SystemManager systemManager) {
    this.systemManager = systemManager;
  }
  
  public SystemManager getSystemManager() {
    return this.systemManager;
  }
  
  public void notify(boolean state) {
    if(state) {
      view.update();
    }
  }
  
  public void init(){
    
  }
  
  public Pane getView() {
    return this.view.getPane();
  }
  
}
