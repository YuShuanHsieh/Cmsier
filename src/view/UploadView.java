package view;
import controller.UploadController;
import controller.Controller;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.TextArea;
import system.Statement;
/*
 * @Author: Yu-Shuan
 *  */
public class UploadView extends Dialog<Void> implements View {

  Controller controller;
  /*
   * Required nodes of this view.
   * */
  private TextArea statementArea;
  private Button uploadButton;
  private TextField host;
  private TextField account;
  private PasswordField password;
  
  @Override
  public void init() {
    GridPane subView = new GridPane();
    subView.setAlignment(Pos.CENTER);
    subView.setHgap(10);
    subView.setVgap(10);
    
    Label viewTitle = new Label("Login to Web server");
    Label hostLabel = new Label("Web Host ");
    Label accountLabel = new Label("account ");
    Label passwordLabel = new Label("password ");
    
    host = new TextField("files.000webhost.com");
    host.setId("host");
    account = new TextField("cherriesweb");
    account.setId("account");
    password = new PasswordField();
    password.setId("password");
    
    uploadButton = new Button("Upload");
    uploadButton.setId("uploadButton");
    
    uploadButton.setPrefWidth(250);
    GridPane.setHalignment(uploadButton, HPos.CENTER);
    GridPane.setHalignment(viewTitle, HPos.CENTER);
    
    statementArea = new TextArea("Upload Statement:");
    statementArea.setPrefWidth(350);
    statementArea.setEditable(false);
      
    subView.add(viewTitle, 0, 0, 2, 1);
    subView.add(hostLabel, 0, 1);
    subView.add(host, 1, 1);
    subView.add(accountLabel, 0, 2);
    subView.add(account, 1, 2);
    subView.add(passwordLabel, 0, 3);
    subView.add(password, 1, 3);
    subView.add(uploadButton, 0, 4, 2, 1);
    subView.add(statementArea, 2, 0, 1, 5);

    this.setTitle("Upload pages to Web server");
    this.getDialogPane().setContent(subView);
    this.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
  }
  
  /*
   * @param statement a string sentence from controller that describes the progress of uploading.
   *  */

  @Override
  public <T> void updateStatement(String instruction, Statement<T> statement){
    if(instruction.equals(UploadController.UPLOAD_PROCESS) && statement.getResult()){
      StringBuilder currentStatement = new StringBuilder(statementArea.getText()); 
      currentStatement.append("\n" + statement.getValue());
      statementArea.setText(currentStatement.toString());
    }
  }

  @Override
  public void showPane() {
    this.showAndWait();
  }
  
  @Override
  public Pane getPane() {
    return this.getDialogPane();
  }
  
  /*
   * @return fields an array including the content of required fields with string type. 
   */
  public String[] getFieldsText() {
    String[] fields = {host.getText(), account.getText(), password.getText()};
    return fields;
  }
  
  /*
   * @return uploadButton an inner button of Web server login box.
   *  */
  public Button getButton() {
    return uploadButton;
  }
  
}
