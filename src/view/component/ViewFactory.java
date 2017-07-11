package view.component;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ViewFactory {

  public void createAlertWindow(String alertInfo) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Ooops, There was something wrong!");
    alert.setHeaderText(null);
    alert.setContentText(alertInfo);
    alert.showAndWait();  
  }
  
  public FileChooser createFileChooser(String title, ExtensionFilter... filters) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle(title);
    
    for(ExtensionFilter filter :filters){
      fileChooser.getExtensionFilters().add(filter);
    }
    
    return fileChooser;
  }
  
}
