package controller;

import view.EditView;
import view.UploadView;
import view.View;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import model.GenerateModel;
import model.Model;
import model.UploadModel;
import system.DataCenter;
import system.Statement;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.concurrent.Task;
import view.component.ViewFactory;

/**
 * @author yu-shuan
 *  */
public class UploadController implements Controller {

  private UploadView view;
  private UploadModel uploadModel;
  private GenerateModel generateModel;
  private DataCenter dataCenter;
  private EditController parent;
  
  private TextField host;
  private TextField account;
  private PasswordField password;
  private ViewFactory viewFactory;
  private Button uploadButton;
  private Button downloadButton;
    
  public UploadController(DataCenter dataCenter) {
    this.dataCenter = dataCenter;
    view = new UploadView();
    viewFactory = new ViewFactory();
    
    uploadModel = new UploadModel(dataCenter);
    attached(view, uploadModel);
    
    generateModel = new GenerateModel(dataCenter);
    attached(view, generateModel);
  }
  
  @Override
  public void attached(View view, Model model) {
    model.attach(view);
  }
  
  @Override
  public Pane getView() {
    return view.getPane();
  }
  
  @Override
  public void setParent(Controller parent) {
    this.parent = (EditController) parent;
  }
  
  @Override
  public void init(){
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
    view.setResultConverter(button -> {
      uploadModel.saveFtpSettings(host.getText(), account.getText(), password.getText()); 
      parent.updateTreeView();
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
