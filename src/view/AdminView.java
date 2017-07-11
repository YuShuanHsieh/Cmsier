package view;
import controller.Controller;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import system.data.Settings;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ChoiceBox;

public class AdminView extends Dialog<String> implements View {

  private Controller controller;
  private TextField titleInput;
  private TextField subTitleInput;
  private TextField localPath;
  private TextField serverPath;
  private Button localPathBrowseButton;
  private ChoiceBox<String> layoutBox;
  
  public enum Filed{
    title, subtitle, localPath, serverPath
  }
  
  @Override
  public void setController(Controller controller) {
    this.controller = controller;
  }

  @Override
  public void init() {
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
    
    Tab layoutSettingTab = new Tab();
    layoutSettingTab.setClosable(false);
    Label layoutLabel = new Label("Layout Style ");
    GridPane layoutGrid = new GridPane();
    layoutBox = new ChoiceBox<String>();
    layoutBox.setId("layoutSetting");
    
    layoutSettingTab.setText("Layout");
    layoutBox.getItems().addAll("blue","rose");
    layoutGrid.add(layoutLabel, 0, 0);
    layoutGrid.add(layoutBox, 1, 0);
    layoutGrid.setPadding(new Insets(10));
    layoutSettingTab.setContent(layoutGrid);
    tabPane.getTabs().add(layoutSettingTab);
    
    this.getDialogPane().setContent(tabPane);
    this.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
    
    update();
  }
  
  @Override
  public void update() {
    Settings setting = controller.getSystemManager().getSettings();
    titleInput.setText(setting.getTitle());
    subTitleInput.setText(setting.getSubTitle());
    localPath.setText(setting.getLocalPath());
    serverPath.setText(setting.getPublish());
    layoutBox.getSelectionModel().select(setting.getLayout());
  }

  @Override
  public Pane getPane() {
    return this.getDialogPane();
  }
  
  public void showPane() {
    this.showAndWait();
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
