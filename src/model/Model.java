package model;
import system.SystemManager;
import view.View;

public abstract class Model {
 
  protected SystemManager dataCenter;
  protected View view;
  
  public void init(){
  
  }
  
  public void setDataCenter(SystemManager dataCenter){
    this.dataCenter = dataCenter;
  }
  
  public void setView(View view){
    this.view = view;
  }
}
