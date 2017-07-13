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
    model.setView(view);
    model.setDataCenter(systemManager);
  }
  
  public void setSystemManager(SystemManager systemManager) {
    this.systemManager = systemManager;
  }
  
  public SystemManager getSystemManager() {
    return this.systemManager;
  }

  public void init(){
    
  }
  
  public void setEvent(){
    
  }
  
  public Pane getView() {
    return this.view.getPane();
  }
}
