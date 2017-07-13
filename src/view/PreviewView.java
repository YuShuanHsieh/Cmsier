package view;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import system.Statement;

public class PreviewView extends Dialog<String> implements View {

  private final WebView webView;
  private final WebEngine webEngine;
  public static final String UPDATE_LOADPAGE = "UPDATE_LOADPAGE";
  
  public PreviewView() {
    webView = new WebView();
    webEngine = webView.getEngine();
  }

  @Override
  public void init() {
    ButtonType okayButton = new ButtonType("OK", ButtonData.OK_DONE);
    this.setTitle("Web page preview");
    this.getDialogPane().setContent(webView);
    this.getDialogPane().getButtonTypes().add(okayButton);
    
    this.setOnShown(new EventHandler<DialogEvent>(){
      @Override
      public void handle(DialogEvent event) {
        webEngine.reload();
      }
    });
  }

  @Override
  public <T> void updateStatement(String instruction,Statement<T> statement){
    if(!statement.getResult()){
      return;
    }
    
    if(instruction.equals(UPDATE_LOADPAGE)){
      String pagePath = (String)statement.getValue();
      webEngine.load(pagePath);
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
