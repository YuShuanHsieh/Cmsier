package controller;

import view.UploadView;
import javafx.scene.input.MouseEvent;
import model.GenerateModel;
import model.UploadModel;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.concurrent.Task;
import view.component.ViewFactory;

/*
 * @Author: Yu-Shuan
 * */
public class UploadController extends Controller {

  private UploadModel uploadModel;
  private GenerateModel generateModel;
  private TextField host;
  private TextField account;
  private PasswordField password;
  private ViewFactory viewFactory;
  private Button uploadButton;
  private Button downloadButton;
    
  public UploadController() {
    view = new UploadView();
    viewFactory = new ViewFactory();
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
    
    host = (TextField)view.getPane().lookup("#host");
    account = (TextField)view.getPane().lookup("#account");
    password = (PasswordField)view.getPane().lookup("#password");
    uploadButton = (Button)view.getPane().lookup("#uploadButton");
    downloadButton = (Button)view.getPane().lookup("#downloadButton");
    
    setEvent();

    view.showPane();
  }
  
  @Override
  public void setEvent(){
    UploadView castView = (UploadView)view;
    castView.setResultConverter(button -> {
      uploadModel.saveFtpSettings(host.getText(), account.getText(), password.getText()); 
      return null;
    });
    uploadButton.setOnMousePressed(this::upload);
    downloadButton.setOnMousePressed(this::download);
  }
  
  private void download(MouseEvent event) {
    setButtonDisable(true);
    
    Task<Void> task = new Task<Void>() {
      
      @Override 
      public Void call() {
        if(!uploadModel.connectToWebServer(host.getText(), account.getText(), password.getText())){
          setButtonDisable(false);
          return null;
        }
        if(!uploadModel.downloadFileFromServer()){
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
  
  private void upload(MouseEvent event) {

    if(dataCenter.getSettings().getPublish().trim().isEmpty()){
      viewFactory.createAlertWindow("Please set up your Web site address before upload it!");
      return;
    }
    
    setButtonDisable(true);
    Task<Void> task = new Task<Void>() {
      @Override 
      public Void call() {
        if(!uploadModel.connectToWebServer(host.getText(), account.getText(), password.getText())){
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
    uploadButton.setDisable(isDisable);
    downloadButton.setDisable(isDisable);
  }

}
