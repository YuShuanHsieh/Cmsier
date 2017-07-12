package view;
import controller.Controller;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import system.data.Settings;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import system.data.CSSXMLsettings;
import javafx.concurrent.Worker.State;
import javafx.concurrent.Worker;

public class AdminView extends Dialog<String> implements View {

  private Controller controller;
  private TextField titleInput;
  private TextField subTitleInput;
  private TextField localPath;
  private TextField serverPath;
  private Button localPathBrowseButton;
  private ChoiceBox<String> layoutBox;
  private WebView webView;
  private WebEngine webEngine;
  private ColorPicker BKpick;
  private ColorPicker titleColorPick;
  private ColorPicker subTitleColorPick;
  private ColorPicker mainColorPick;
  private ColorPicker contentColorPick;
  private ColorPicker frameColorPick;
  
  public static final String CSSCOLOR_HEADER = "headerColor";
  public static final String CSSCOLOR_TITLE = "titleColor";
  public static final String CSSCOLOR_SUBTITLE = "subTitleColor";
  public static final String CSSCOLOR_MAIN = "mainColor";
  public static final String CSSCOLOR_CONTENT = "contentColor";
  public static final String CSSCOLOR_FRAME = "frameColor";
  private boolean isWebPageUpdate = false;
  
  public enum Filed{
    title, subtitle, localPath, serverPath
  }
  
  @Override
  public void setController(Controller controller) {
    this.controller = controller;
  }

  @Override
  public void init() { 
    this.getDialogPane().setStyle("-fx-font-size: 12px;");
    
    GridPane grid = new GridPane();
    TabPane tabPane = new TabPane();
    Tab generalSettingTab = new Tab();
    generalSettingTab.setClosable(false);
    generalSettingTab.setText("General");
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(10));
    
    this.setTitle("Web Site Settings");
    this.setResizable(true);
    
    Label titleLabel = new Label("Title");
    titleInput = new TextField();
    titleInput.setPrefWidth(350);

    Label subTitleLabel = new Label("Sub Title");
    subTitleInput = new TextField();
    subTitleInput.setPrefWidth(350);
    
    Label localPathLabel = new Label("Local Path");
    localPath = new TextField();
    localPath.setPrefWidth(350);
    
    localPathBrowseButton = new Button("Browse");
    
    Label serverPathLabel = new Label("Server Path");
    serverPath = new TextField();
    serverPath.setPrefWidth(350);
    
    grid.add( titleLabel, 0, 0);
    grid.add( titleInput, 1, 0, 2, 1);
    grid.add( subTitleLabel, 0, 1);
    grid.add( subTitleInput, 1, 1, 2, 1);
    grid.add( localPathLabel, 0, 2);
    grid.add( localPath, 1, 2);
    grid.add( localPathBrowseButton, 2, 2);
    grid.add( serverPathLabel, 0, 3);
    grid.add( serverPath, 1, 3, 2, 1);
    
    generalSettingTab.setContent(grid);
    tabPane.getTabs().add(generalSettingTab);
    
    webView = new WebView();
    webEngine = webView.getEngine();
    webView.setPrefWidth(300);
    webView.setPrefHeight(200);
    webView.setZoom(0.5);
    
    /* Reload the web page when a new page load into webEngine in order to update its CSS file. */
    webEngine.getLoadWorker().stateProperty().addListener(
        new ChangeListener<State>() {
          @Override public void changed(ObservableValue ov, State oldState, State newState) {
            if (!isWebPageUpdate && newState == Worker.State.SUCCEEDED) {
              webEngine.reload();
              isWebPageUpdate = true;
            }    
          }
        });
    
    Tab layoutSettingTab = new Tab();
    layoutSettingTab.setClosable(false);
    Label layoutLabel = new Label("Layout Style ");
    GridPane layoutGrid = new GridPane();
    layoutBox = new ChoiceBox<String>();
    layoutBox.setId("layoutSetting");
    layoutBox.setMaxWidth(Double.POSITIVE_INFINITY);
    
    layoutSettingTab.setText("Layout");
    layoutBox.getItems().addAll("blue","rose");
    
    Label layoutBKLabel = new Label("Header");
    BKpick = new ColorPicker();
    BKpick.setId(CSSCOLOR_HEADER);
    
    Label titleColorLabel = new Label("Title");
    titleColorPick = new ColorPicker();
    titleColorPick.setId(CSSCOLOR_TITLE);
    
    Label subTitleColorLabel = new Label("Subtitle");
    subTitleColorPick = new ColorPicker();
    subTitleColorPick.setId(CSSCOLOR_SUBTITLE);
    
    Label mainColorLabel = new Label("Background");
    mainColorPick = new ColorPicker();
    mainColorPick.setId(CSSCOLOR_MAIN);
    
    Label contentColorLabel = new Label("Content");
    contentColorPick = new ColorPicker();
    contentColorPick.setId(CSSCOLOR_CONTENT);
    
    Label frameColorLabel = new Label("Frame");
    frameColorPick = new ColorPicker();
    frameColorPick.setId(CSSCOLOR_FRAME);
    
    layoutGrid.setVgap(5);
    layoutGrid.setHgap(5);
    layoutGrid.add(layoutLabel, 0, 0);
    layoutGrid.add(layoutBox, 1, 0);
    layoutGrid.add(layoutBKLabel, 0, 1);
    layoutGrid.add(BKpick, 1, 1);
    layoutGrid.add(titleColorLabel, 0, 2);
    layoutGrid.add(titleColorPick, 1, 2);
    layoutGrid.add(subTitleColorLabel, 0, 3);
    layoutGrid.add(subTitleColorPick, 1, 3);
    layoutGrid.add(mainColorLabel, 0, 4);
    layoutGrid.add(mainColorPick, 1, 4);
    layoutGrid.add(contentColorLabel, 0, 5);
    layoutGrid.add(contentColorPick, 1, 5);
    layoutGrid.add(frameColorLabel, 0, 6);
    layoutGrid.add(frameColorPick, 1, 6);
    layoutGrid.add(webView, 2, 0, 1, 8);
    
    layoutGrid.setPadding(new Insets(10));
    layoutSettingTab.setContent(layoutGrid);
    tabPane.getTabs().add(layoutSettingTab);
    tabPane.setCache(false);
    
    this.getDialogPane().setContent(tabPane);
    this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
  }
  
  @Override
  public void update() {
    Settings setting = controller.getSystemManager().getSettings();
    CSSXMLsettings cssSettings = controller.getSystemManager().getCSSSettings();
    titleInput.setText(setting.getTitle());
    subTitleInput.setText(setting.getSubTitle());
    localPath.setText(setting.getLocalPath());
    serverPath.setText(setting.getPublish());
    layoutBox.getSelectionModel().select(setting.getLayout());
    
    BKpick.setValue(Color.web(cssSettings.getHeaderColor()));
    titleColorPick.setValue(Color.web(cssSettings.getTitleColor()));
    subTitleColorPick.setValue(Color.web(cssSettings.getSubTitleColor()));
    mainColorPick.setValue(Color.web(cssSettings.getMainColor()));
    contentColorPick.setValue(Color.web(cssSettings.getContentColor()));
    frameColorPick.setValue(Color.web(cssSettings.getFrameColor()));
  }
  
  @Override
  public Pane getPane() {
    return this.getDialogPane();
  }
  
  @Override
  public void showPane() {
    this.showAndWait();
  }
  
  public void updateWebPage(String pagePath){
    webEngine.reload();
    isWebPageUpdate = true;
  }
  
  public void reloadWebPage(String pagePath){
    webEngine.load("file://" + pagePath);
    isWebPageUpdate = false;
  }
  
  public TextField getField(Filed type) {
    switch(type) {
      case title :
      return this.titleInput;
      case subtitle :
      return this.subTitleInput;
      case localPath :
        return this.localPath;
      case serverPath :
        return this.serverPath;
      default:
      return null;
    }
  }
  
  public ChoiceBox<String> getLayoutBox() {
    return this.layoutBox;
  }
  
  public Button getBrowseButton() {
    return this.localPathBrowseButton;
  }
  
}
