/*
 * A collection of all required data.
 * 
 * @author Yu-Shuan
 */
package system;

import javafx.stage.Stage;
import system.data.CSSXMLsettings;
import system.data.Data;
import system.data.Settings;

public class DataCenter {
  
  /* it is used to calculate the size of view components. */
  private Stage window;
  
  private Data data;
  private Settings settings;
  private CSSXMLsettings cssSettings;
  
  public DataCenter(Stage window) {
    this.window = window;
  }

  public Stage getWindow() {
    return this.window;
  }
  
  public void setCSSSettings(CSSXMLsettings cssSettings) {
    this.cssSettings = cssSettings;
  }
  
  public CSSXMLsettings getCSSSettings() {
    return this.cssSettings;
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
   
}
