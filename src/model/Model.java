package model;
import system.DataCenter;
import system.InitializeException;
import view.View;

public abstract class Model {
 
  protected DataCenter dataCenter;
  protected View view;
  
  /*
   * Essential functions which must do not be changed.
   * */
  public final void setDataCenter(DataCenter dataCenter){
    this.dataCenter = dataCenter;
  }
  
  public final void setView(View view){
    this.view = view;
  }
  
  public final void isInitialize(){
    if(view == null || dataCenter == null){
      throw new InitializeException();
    } 
  }
  
  /*
   * Initialize model class.
   */
  public void init(){
    
  }
  
  
}
