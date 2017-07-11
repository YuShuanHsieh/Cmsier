/*
 * This is the top-level class of system.
 * All related data are initialized and stored in it.
 * It contains a main pane and a controller list that allows controllers retrieve data from this.  
 * 
 * @author Yu-Shuan
 */
package system;

import java.util.List;
import controller.Controller;

import java.util.LinkedList;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import system.data.Data;
import system.data.Settings;

public class SystemManager {
  
  private Stage window;
  private Data data;
  private Settings settings;
  private List<Controller> controllerList;
  private StackPane pane;
  
  public SystemManager(Stage window) {
    this.window = window;
    controllerList = new LinkedList<Controller>();
    pane = new StackPane();
    
    String css = this.getClass().getResource("layout.css").toExternalForm(); 
    pane.getStylesheets().add(css);  
  }

  public Stage getWindow() {
    return this.window;
  }
  
  public StackPane getPane() {
    return this.pane;
  }
  
  public Settings getSettings() {
    return this.settings;
  }
  
  public void setSettings(Settings settings) {
    this.settings = settings;
  }
  
  public Data getData() {
    return this.data;
  }
  
  public void setData(Data data) {
    this.data = data;
  }
  
  public void addPane(Controller controller) {
    pane.getChildren().add(controller.getView());
  }
  
  public void removePane(Controller controller) {
    pane.getChildren().remove(controller.getView());
  }
  
  public void register(Controller controller) {
    if(controllerList.indexOf(controller) == -1) {
      controllerList.add(controller);
      controller.setSystemManager(this);
    }
    else {
      System.err.println("The controller has been registed.");
    }
  }
  
  public void unRegister(Controller controller) {
    if(controllerList.indexOf(controller) != -1) {
      controllerList.remove(controller);
      controller.setSystemManager(null);
    }
    else {
      System.err.println("The controller does not be registed.");
    }
  }
  
  public void notifyAllController() {
    for(Controller controller: controllerList) {
      controller.notify(true);
    }
  }
  
}
