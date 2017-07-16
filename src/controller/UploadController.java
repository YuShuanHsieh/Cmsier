package controller;

import view.UploadView;
import javafx.scene.input.MouseEvent;
import model.GenerateModel;
import model.UploadModel;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.concurrent.Task;

/*
 * @Author: Yu-Shuan
 * */
public class UploadController extends Controller {

  public static final String UPLOAD_PROCESS = "UPLOAD_PROCESS";
  private UploadModel uploadModel;
  private GenerateModel generateModel;
    
  public UploadController() {
    view = new UploadView();
  }
  
  @Override
  public void init(){
    uploadModel = new UploadModel();
    attached(view, uploadModel);
    
    generateModel = new GenerateModel();
    attached(view, generateModel);
    
    view.init();
    uploadModel.init();
    generateModel.init();
    
    Button uploadButton = ((UploadView)view).getButton();
    uploadButton.setOnMousePressed(this::upload);
    view.showPane();
  }
  
  private void upload(MouseEvent event) {
    UploadView castView = (UploadView)view;
    String[] fields = castView.getFieldsText();
    
    setButtonDisable(true);
    Task<Void> task = new Task<Void>() {
      @Override 
      public Void call() {
        if(!uploadModel.connectToWebServer(fields[0], fields[1], fields[2])){
          setButtonDisable(false);
          return null;
        }
        if(!generateModel.generateAllFinalPages()){
          setButtonDisable(false);
          return null;
        }
        
        if(!uploadModel.uploadFinalPageFile()) {
          setButtonDisable(false);
          return null;
        }
        
        uploadModel.disconnectToWebServer();
        
        setButtonDisable(false);
        return null;
      }
    };
    new Thread(task).start();
  }
  
  private void setButtonDisable(boolean isDisable) {
    UploadView castView = (UploadView)view;
    castView.getDialogPane().lookupButton(ButtonType.CLOSE).setDisable(isDisable);
    castView.getButton().setDisable(isDisable);
  }

}
