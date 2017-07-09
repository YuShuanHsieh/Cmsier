package controller;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.control.ChoiceBox;
import model.AdminModel;
import view.AdminView;
import view.AdminView.Filed;
import java.io.File;

public class AdminController extends Controller {

  public AdminController() {
    view = new AdminView();
    model = new AdminModel();
    attached(view, model);
  }
  
  @Override
  public void init(){
    view.init(); 
    model.init();
    
    AdminView castView = (AdminView)view; 
    castView.getBrowseButton().setOnMousePressed(this::browseLocalPathEvent);
    castView.setResultConverter(button -> {
      if(button.getButtonData() == ButtonData.OK_DONE) {
        String title = castView.getField(Filed.title).getText();
        String subTitle = castView.getField(Filed.subtitle).getText();
        String localPath = castView.getField(Filed.localPath).getText();
        String serverPath = castView.getField(Filed.serverPath).getText();
        
        ChoiceBox<String> layoutBox = castView.getLayoutBox();
        String selectedLayout = layoutBox.getSelectionModel().getSelectedItem();
         
        if(selectedLayout == null) {
          selectedLayout = "blue";
        }
        
        ((AdminModel)model).modifySettingsField(title, subTitle, localPath, serverPath, selectedLayout);
        
      }
      return null;
    });
    
    view.showPane();
  }
  
  private void browseLocalPathEvent(MouseEvent event) {
    AdminView castView = (AdminView)view;
    DirectoryChooser directoryChooser = new DirectoryChooser();
    File selectedFile = directoryChooser.showDialog(this.getSystemManager().getWindow());
    if(selectedFile != null && selectedFile.isDirectory()) {
      String selectedLocalPath = selectedFile.getAbsolutePath();
      castView.getField(Filed.localPath).setText(selectedLocalPath + "/");
    }
    Stage stage = (Stage) castView.getDialogPane().getScene().getWindow();
    stage.toFront();
  }
 
}
