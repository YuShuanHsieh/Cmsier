package controller;

import view.UploadView;
import javafx.scene.input.MouseEvent;
import model.UploadModel;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.concurrent.Task;

public class UploadController extends Controller {

  
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
    castView.updateStatement(" Start to connect to Web server, please wait.");
    
    Task<Void> task = new Task<Void>() {
      @Override 
      public Void call() {
        if(uploadModel.connectToWebServer(fields[0], fields[1], fields[2])){
          castView.updateStatement(" Connect to Web server successfully.");
        }
        else {
          castView.updateStatement(" Error. Cannot connect to Web server");
          setButtonDisable(false);
          return null;
        }
        if(uploadModel.generateFinalPage()){
          castView.updateStatement(" Generate web pages.");
        }
        else {
          castView.updateStatement(" Error. Cannot generate web pages..");
          setButtonDisable(false);
          return null;
        }
        castView.updateStatement(" Upload web pages now, please wait.");
        if(uploadModel.uploadFinalPageFile()) {
          castView.updateStatement(" Upload web pages successfully.");
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
