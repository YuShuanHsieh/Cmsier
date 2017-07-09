package view;

import controller.Controller;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class PreviewView extends Dialog<String> implements View {

  private Controller controller;
  final private WebView webView;
  final private WebEngine webEngine;
  
  public PreviewView() {
    webView = new WebView();
    webEngine = webView.getEngine();
  }
  
  @Override
  public void setController(Controller controller) {
    this.controller = controller;
  }

  @Override
  public void init() {
    ButtonType okayButton = new ButtonType("OK", ButtonData.OK_DONE);
    this.setTitle("Web page preview");
    this.getDialogPane().setContent(webView);
    this.getDialogPane().getButtonTypes().add(okayButton);
    update();
  }

  @Override
  public void update() {
    String pageLocalPath = controller.getSystemManager().getData().getCurrentPageLocalPath();
    webEngine.load(pageLocalPath);
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
