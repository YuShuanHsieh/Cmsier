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

public class AdminController extends Controller {
  
  public AdminController() {
    view = new AdminView();
    model = new AdminModel();
  }
  
  @Override
  public void init(){
    attached(view, model);
    
    view.init(); 
    model.init();
    
    setEvent();
 
    view.showPane();
  }
  
  @Override
  public void setEvent(){
    AdminModel castModel = (AdminModel)model;
    AdminView castView = (AdminView)view; 
    
    castView.getBrowseButton().setOnMousePressed(this::browseLocalPathEvent);
    allocateColorPickerEvent(AdminView.CSSCOLOR_HEADER);
    allocateColorPickerEvent(AdminView.CSSCOLOR_TITLE);
    allocateColorPickerEvent(AdminView.CSSCOLOR_SUBTITLE);
    allocateColorPickerEvent(AdminView.CSSCOLOR_MAIN);
    allocateColorPickerEvent(AdminView.CSSCOLOR_CONTENT);
    allocateColorPickerEvent(AdminView.CSSCOLOR_FRAME);
    
    ChoiceBox<String> choiceBox = castView.getLayoutBox();
    choiceBox.setOnAction(event -> {
      String selectedLayout = choiceBox.getSelectionModel().getSelectedItem();
      systemManager.getSettings().setLayout(selectedLayout);
      castModel.changeLayout();
    });
    
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
        
        castModel.modifySettingsField(title, subTitle, localPath, serverPath, selectedLayout);
        castModel.modifyCssSetting();
       
      }
      return null;
    });
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
      systemManager.getCSSSettings().setHeaderColor(colorValue);
    }
    else if(node.getId().equals(AdminView.CSSCOLOR_TITLE)) {
      systemManager.getCSSSettings().setTitleColor(colorValue);
    }
    else if(node.getId().equals(AdminView.CSSCOLOR_SUBTITLE)) {
      systemManager.getCSSSettings().setSubTitleColor(colorValue);
    }
    else if(node.getId().equals(AdminView.CSSCOLOR_MAIN)) {
      systemManager.getCSSSettings().setMainColor(colorValue);;
    }
    else if(node.getId().equals(AdminView.CSSCOLOR_CONTENT)) {
      systemManager.getCSSSettings().setContentColor(colorValue);;
    }
    else if(node.getId().equals(AdminView.CSSCOLOR_FRAME)) {
      systemManager.getCSSSettings().setFrameColor(colorValue);;
    }
    ((AdminModel)model).changeLayoutColor();
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
