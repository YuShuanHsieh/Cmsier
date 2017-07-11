package controller;

import javafx.event.ActionEvent;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import model.AdminModel;
import view.AdminView;
import view.AdminView.Filed;
import java.io.File;
import java.io.IOException;

public class AdminController extends Controller {

  private String currentPreviewPagePath;
  
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
    allocateColorPickerEvent(AdminView.CSSCOLOR_HEADER);
    allocateColorPickerEvent(AdminView.CSSCOLOR_TITLE);
    allocateColorPickerEvent(AdminView.CSSCOLOR_SUBTITLE);
 
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
        ((AdminModel)model).modifyCssSetting(this.getSystemManager().getCSSSettings());
        
        File file = new File(currentPreviewPagePath);
        file.delete();
      }
      return null;
    });
    
    view.update();
    view.showPane();
  }
  
  private void allocateColorPickerEvent(String colorPickId){
    AdminView castView = (AdminView)view; 
    ColorPicker colorPicker = (ColorPicker)castView.getPane().lookup("#" + colorPickId);
    colorPicker.setOnAction(this::colorChangeEvent);
  }
  
  private void colorChangeEvent(ActionEvent event){
    ColorPicker node = (ColorPicker)event.getSource();
    String colorValue = "#" + node.getValue().toString().substring(2, 8);
    if(node.getId().equals(AdminView.CSSCOLOR_HEADER)){
      this.getSystemManager().getCSSSettings().setHeaderBackground(colorValue);
    }
    else if(node.getId().equals(AdminView.CSSCOLOR_TITLE)) {
      this.getSystemManager().getCSSSettings().setTitleColor(colorValue);
    }
    else if(node.getId().equals(AdminView.CSSCOLOR_SUBTITLE)) {
      this.getSystemManager().getCSSSettings().setSubTitleColor(colorValue);
    }
    try{
      String previewPage = ((AdminModel)model).generateTempCssFile();
      updatePreviewPage(previewPage);
    } catch(IOException e){}
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
  
  public void updatePreviewPage(String pagePath){
    AdminView castView = (AdminView)view;
    castView.updateWebPage(pagePath);
    currentPreviewPagePath = pagePath;
  }
 
}
