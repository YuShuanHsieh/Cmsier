package controller;

import view.UploadView;
import javafx.scene.input.MouseEvent;
import model.UploadModel;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.concurrent.Task;

public class UploadController extends Controller {

  public static final String UPLOAD_PROCESS = "UPLOAD_PROCESS";
    
  public UploadController() {
    view = new UploadView();
    model = new UploadModel();
    attached(view, model);
  }
  
  @Override
  public void init(){
    view.init();
    model.init();
    
    Button uploadButton = ((UploadView)view).getButton();
    uploadButton.setOnMousePressed(this::upload);
    view.showPane();
  }
  
  private void upload(MouseEvent event) {
    UploadView castView = (UploadView)view;
    UploadModel uploadModel = (UploadModel) model;
    String[] fields = castView.getFieldsText();
    
    setButtonDisable(true);
    Task<Void> task = new Task<Void>() {
      @Override 
      public Void call() {
        
        if(!uploadModel.connectToWebServer(fields[0], fields[1], fields[2])){
          setButtonDisable(false);
          return null;
        }
        
        if(!uploadModel.generateFinalPage()){
          setButtonDisable(false);
          return null;
        }
        
        if(!uploadModel.uploadFinalPageFile()) {
          setButtonDisable(false);
          return null;
        }
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
