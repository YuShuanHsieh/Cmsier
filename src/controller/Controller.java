package controller;
import view.View;
import javafx.scene.layout.Pane;
import model.Model;
import system.DataCenter;

public abstract class Controller {

  protected View view;
  protected Model model;
  protected DataCenter dataCenter;
  
  /*
   * Allocate view and data center to model.
   */
  public void attached(View view, Model model) {
    model.setView(view);
    model.setDataCenter(dataCenter);
  }
  
  public void setDataCenter(DataCenter dataCenter) {
    this.dataCenter = dataCenter;
  }
  
  public DataCenter getDataCenter() {
    return this.dataCenter;
  }

  public void init(){
    
  }
  
  /*
   * add eventHandler to selected view components.
   */
  public void setEvent(){
    
  }
  
  public Pane getView() {
    return this.view.getPane();
  }
}
