package controller;
/**
 * Admin - providing some configuring methods that helps user customized their web sites.
 * @see AdminView
 * @see AdminModel
 *  */
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.AdminModel;
import model.Model;
import system.DataCenter;
import view.AdminView;
import view.View;
import view.AdminView.Filed;
import java.io.File;

public class AdminController implements Controller {
  
  private EditController parent;
  private AdminView view;
  private AdminModel model;
  private DataCenter dataCenter;
  
  private  TilePane existingCategories;
  private TextField editNameField;
  private TextField addNameField;
  private Button addCategoryButton;
  private Button editCategoryButton;
  private Label selectedCategory;
  
  public AdminController(DataCenter dataCenter) {
    this.dataCenter = dataCenter;
    view = new AdminView();
    
    model = new AdminModel();
    attached(view, model);
  }
  
  public void setParent(Controller parent) {
    this.parent = (EditController)parent;
  }
  
  @Override
  public void attached(View view, Model model) {
    model.setView(view);
    model.setDataCenter(dataCenter);
  }
  
  @Override
  public Pane getView() {
    return view.getPane();
  }
  
  @Override
  public void init(){
    view.init(); 
    model.init();
 
    existingCategories = (TilePane)view.getPane().lookup("#setting-category-existing-list");
    editNameField = (TextField)view.getPane().lookup("#setting-category-edit");
    addCategoryButton = (Button)view.getPane().lookup("#setting-category-add-button");
    addNameField = (TextField)view.getPane().lookup("#setting-category-add");
    editCategoryButton = (Button)view.getPane().lookup("#setting-category-edit-button");
    
    setEvent();
    view.showPane();
  }
  
  @Override
  public void setEvent(){
    AdminModel castModel = (AdminModel)model;
    AdminView castView = (AdminView)view; 
    
    /** Set up a category view as user taps the tab.*/
    castView.getCategoryTab().setOnSelectionChanged(value -> {
      if(existingCategories.getChildren().isEmpty()) {
        castView.setUpExistingCategory(dataCenter.getCategory().values());
        setUpExistingCategoriteEvent();
      }
    });
    
    castView.getBrowseButton().setOnMousePressed(this::browseLocalPathEvent);
    
    allocateColorPickerEvent(AdminView.CSSCOLOR_HEADER);
    allocateColorPickerEvent(AdminView.CSSCOLOR_TITLE);
    allocateColorPickerEvent(AdminView.CSSCOLOR_SUBTITLE);
    allocateColorPickerEvent(AdminView.CSSCOLOR_MAIN);
    allocateColorPickerEvent(AdminView.CSSCOLOR_CONTENT);
    allocateColorPickerEvent(AdminView.CSSCOLOR_FRAME);
    
    addCategoryButton.setOnAction(this::addNewCategoryEvent);
    editCategoryButton.setOnAction(this::editCategoryEvent);
    
    ChoiceBox<String> choiceBox = castView.getLayoutBox();
    choiceBox.setOnAction(event -> {
      String selectedLayout = choiceBox.getSelectionModel().getSelectedItem();
      dataCenter.getSettings().setLayout(selectedLayout);
      castModel.changeLayout();
    });
    
    castView.setResultConverter(button -> {
      if(button.getButtonData() == ButtonData.OK_DONE) {
        String title = castView.getField(Filed.title).getText();
        String subTitle = castView.getField(Filed.subtitle).getText();
        String localPath = castView.getField(Filed.localPath).getText();
        String serverPath = castView.getField(Filed.serverPath).getText();
        String footer = castView.getField(Filed.footer).getText();
        
        ChoiceBox<String> layoutBox = castView.getLayoutBox();
        String selectedLayout = layoutBox.getSelectionModel().getSelectedItem();
         
        if(selectedLayout == null) {
          selectedLayout = "blue";
        }
        castModel.modifySettingsField(title, subTitle, localPath, serverPath, selectedLayout,footer);
        castModel.modifyCssSetting();
       
      }
      parent.updateCacheData();
      return null;
    });
  }
  
  private void allocateColorPickerEvent(String colorPickId){
    AdminView castView = (AdminView)view; 
    ColorPicker colorPicker = (ColorPicker)castView.getPane().lookup("#" + colorPickId);
    colorPicker.setOnAction(this::colorChangeEvent);
  }
  
  private void colorChangeEvent(ActionEvent event){
    ColorPicker node = (ColorPicker)event.getSource();
    String colorValue = "#" + node.getValue().toString().substring(2, 8);
    if(node.getId().equals(AdminView.CSSCOLOR_HEADER)){
      dataCenter.getCSSSettings().setHeaderColor(colorValue);
    }
    else if(node.getId().equals(AdminView.CSSCOLOR_TITLE)) {
      dataCenter.getCSSSettings().setTitleColor(colorValue);
    }
    else if(node.getId().equals(AdminView.CSSCOLOR_SUBTITLE)) {
      dataCenter.getCSSSettings().setSubTitleColor(colorValue);
    }
    else if(node.getId().equals(AdminView.CSSCOLOR_MAIN)) {
      dataCenter.getCSSSettings().setMainColor(colorValue);;
    }
    else if(node.getId().equals(AdminView.CSSCOLOR_CONTENT)) {
      dataCenter.getCSSSettings().setContentColor(colorValue);;
    }
    else if(node.getId().equals(AdminView.CSSCOLOR_FRAME)) {
      dataCenter.getCSSSettings().setFrameColor(colorValue);;
    }
    ((AdminModel)model).changeLayoutColor();
  }
  
  private void browseLocalPathEvent(MouseEvent event) {
    AdminView castView = (AdminView)view;
    DirectoryChooser directoryChooser = new DirectoryChooser();
    File selectedFile = directoryChooser.showDialog(dataCenter.getWindow());
    if(selectedFile != null && selectedFile.isDirectory()) {
      String selectedLocalPath = selectedFile.getAbsolutePath();
      castView.getField(Filed.localPath).setText(selectedLocalPath + "/");
    }
    Stage stage = (Stage) castView.getDialogPane().getScene().getWindow();
    stage.toFront();
  }
  
  private void setUpExistingCategoriteEvent() {
    for(Node node : existingCategories.getChildren()) {
      node.setOnMousePressed(new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          Label targetCategory = (Label) event.getSource();
          editNameField.setText(targetCategory.getText());
          selectedCategory = targetCategory;
        }
      });
    }
  }
  
  private void addNewCategoryEvent(ActionEvent event) {
    if(addNameField.getText().trim().isEmpty()) {
      // add some error messages.
      return;
    }
    
    String name = addNameField.getText().trim();
    if(!dataCenter.isCategoryExist(name)) {
      ((AdminModel)model).addNewCategory(name);
      Label newCategory = new Label(name);
      newCategory.prefWidthProperty().bind(existingCategories.widthProperty().divide(3));
      newCategory.setId("setting-category-item");
      
      newCategory.setOnMousePressed(new EventHandler<MouseEvent>() {
        public void handle(MouseEvent event) {
          Label targetCategory = (Label) event.getSource();
          editNameField.setText(targetCategory.getText());
          selectedCategory = targetCategory;
        }
      });
      existingCategories.getChildren().add(newCategory);
    }
  }
  
  private void editCategoryEvent(ActionEvent event) {
    String newName = editNameField.getText().trim();
    if(selectedCategory == null || newName.isEmpty()) {
      // add some error messages.
      return;
    }
    else if(newName.equals(selectedCategory.getText())) {
      return;
    }
    ((AdminModel)model).editCategoryName(newName, selectedCategory.getText());
    selectedCategory.setText(newName);
  }
  
}
