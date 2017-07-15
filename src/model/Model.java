package model;
import system.DataCenter;
import view.View;

public abstract class Model {
 
  protected DataCenter dataCenter;
  protected View view;
  
  public void init(){
  }
  
  public void setDataCenter(DataCenter dataCenter){
    this.dataCenter = dataCenter;
  }
  
  public void setView(View view){
    this.view = view;
  }
}
