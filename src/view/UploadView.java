package view;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.control.TextArea;
import system.DataCenter;
import system.Statement;
import system.data.FtpSettings;
/*
 * @Author: Yu-Shuan
 *  */
public class UploadView extends Dialog<Void> implements View {

  public static final String UPLOAD_PROCESS = "UPLOAD_PROCESS";
  public static final String UPLOAD_INFO = "UPLOAD_INFO";
  /*
   * Required nodes of this view.
   * */
  private TextArea statementArea;
  private Button uploadButton;
  private Button downloadButton;
  private TextField host;
  private TextField account;
  private PasswordField password;
  
  @Override
  public void init() {
    GridPane subView = new GridPane();
    subView.setAlignment(Pos.CENTER);
    subView.setHgap(10);
    subView.setVgap(10);
    subView.setStyle("-fx-font-size: 12px;");
    
    String css = DataCenter.class.getResource("dialog.css").toExternalForm(); 
    this.getDialogPane().getStylesheets().add(css);
    
    Label viewTitle = new Label("Login to Web server");
    Label hostLabel = new Label("Web Host ");
    Label accountLabel = new Label("account ");
    Label passwordLabel = new Label("password ");
    
    viewTitle.setId("upload-title");
    
    host = new TextField();
    host.setId("host");
    account = new TextField();
    account.setId("account");
    password = new PasswordField();
    password.setId("password");
    
    uploadButton = new Button("Upload");
    uploadButton.setId("uploadButton");
    
    downloadButton = new Button("Download");
    downloadButton.setId("downloadButton");
    
    HBox hbox = new HBox();
    uploadButton.setPrefWidth(125);
    downloadButton.setPrefWidth(125);
    GridPane.setHalignment(viewTitle, HPos.CENTER);
    hbox.getChildren().addAll(uploadButton, downloadButton);
    hbox.setAlignment(Pos.CENTER);
    hbox.setSpacing(5);
    
    statementArea = new TextArea("[ Upload Statement ]");
    statementArea.setPrefWidth(350);
    statementArea.setEditable(false);
      
    subView.add(viewTitle, 0, 0, 2, 1);
    subView.add(hostLabel, 0, 1);
    subView.add(host, 1, 1);
    subView.add(accountLabel, 0, 2);
    subView.add(account, 1, 2);
    subView.add(passwordLabel, 0, 3);
    subView.add(password, 1, 3);
    subView.add(hbox, 0, 4, 2, 1);
    subView.add(statementArea, 2, 0, 1, 5);
    subView.setId("upload");

    this.setTitle("Upload pages to Web server");
    this.getDialogPane().setContent(subView);
    this.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
  }
  
  /*
   * @param statement a string sentence from controller that describes the progress of uploading.
   *  */

  @Override
  public <T> void updateStatement(String instruction, Statement<T> statement){
    
    if(!statement.getResult()){
      return;
    }
    
    if(instruction.equals(UPLOAD_PROCESS)){
      StringBuilder currentStatement = new StringBuilder(statementArea.getText()); 
      currentStatement.append("\n" + statement.getValue());
      statementArea.setText(currentStatement.toString());
    }
    else if(instruction.equals(UPLOAD_INFO)){
      FtpSettings ftpSettings = (FtpSettings) statement.getValue();
      host.setText(ftpSettings.getHost());
      account.setText(ftpSettings.getAccount());
      password.setText(ftpSettings.getPassword());
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
 
}
