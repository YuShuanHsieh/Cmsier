package controller;
import javafx.event.ActionEvent;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import model.AdminModel;
import model.Model;
import system.DataCenter;
import view.AdminView;
import view.View;
import view.AdminView.Filed;
import java.io.File;

/**
 * Admin module: set up data about Web sites, layout, and categories.
 * The model should only attach AdminView to get the correct result.
 * @see AdminModel
 * @see AdminView
 *  */

public class AdminController implements Controller {
  
  private EditController parent;
  private AdminView view;
  private AdminModel model;
  private DataCenter dataCenter;
  
  private TilePane existingCategories;
  private TextField editNameField;
  private TextField addNameField;
  private Button addCategoryButton;
  private Button editCategoryButton;
  private Button browseButton;
  private Label selectedCategory;
  private Tab categoryTab;
  private ChoiceBox<String> choiceBox;
  
  public AdminController(DataCenter dataCenter) {
    this.dataCenter = dataCenter;
    view = new AdminView();
    
    model = new AdminModel(dataCenter);
    attached(view, model);
  }
  
  public void setParent(Controller parent) {
    this.parent = (EditController)parent;
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
  public void init(){
    view.init(); 
    model.init();
 
    existingCategories = (TilePane)view.getPane().lookup("#setting-category-existing-list");
    editNameField = (TextField)view.getPane().lookup("#setting-category-edit");
    addCategoryButton = (Button)view.getPane().lookup("#setting-category-add-button");
    addNameField = (TextField)view.getPane().lookup("#setting-category-add");
    editCategoryButton = (Button)view.getPane().lookup("#setting-category-edit-button");
    categoryTab = view.getCategoryTab();
    browseButton = view.getBrowseButton();
    choiceBox = view.getLayoutBox();
    
    setEvent();
    view.showPane();
  }
  
  @Override
  public void setEvent(){
    browseButton.setOnMousePressed(this::browseLocalPathEvent);
    addCategoryButton.setOnAction(this::addNewCategoryEvent);
    editCategoryButton.setOnAction(this::editCategoryEvent);
    
    allocateColorPickerEvent(AdminView.CSSCOLOR_HEADER);
    allocateColorPickerEvent(AdminView.CSSCOLOR_TITLE);
    allocateColorPickerEvent(AdminView.CSSCOLOR_SUBTITLE);
    allocateColorPickerEvent(AdminView.CSSCOLOR_MAIN);
    allocateColorPickerEvent(AdminView.CSSCOLOR_CONTENT);
    allocateColorPickerEvent(AdminView.CSSCOLOR_FRAME);
    
    /** Set a category view as user taps the tab.*/
    categoryTab.setOnSelectionChanged(value -> {
      if(existingCategories.getChildren().isEmpty()) {
        view.setUpExistingCategory(dataCenter.getCategory().values());
        setUpExistingCategoriteEvent();
      }
    });
    
    choiceBox.setOnAction(event -> {
      String selectedLayout = choiceBox.getSelectionModel().getSelectedItem();
      model.changeLayout(selectedLayout);
    });
    
    view.setResultConverter(button -> {
      if(button.getButtonData() == ButtonData.OK_DONE) {
        String title = view.getField(Filed.title).getText();
        String subTitle = view.getField(Filed.subtitle).getText();
        String localPath = view.getField(Filed.localPath).getText();
        String serverPath = view.getField(Filed.serverPath).getText();
        String footer = view.getField(Filed.footer).getText();
        
        String selectedLayout = choiceBox.getSelectionModel().getSelectedItem();
         
        if(selectedLayout == null) {
          selectedLayout = "blue";
        }
        model.modifySettingsField(title, subTitle, localPath, serverPath, selectedLayout,footer);
        model.modifyCssSetting();
       
      }
      parent.updateCacheData();
      return null;
    });
  }
  
  /** set an event to the target color picker.
   *  This is a sub methods of setEvent();
   *  */
  private void allocateColorPickerEvent(String colorPickId){
    ColorPicker colorPicker = (ColorPicker)view.getPane().lookup("#" + colorPickId);
    colorPicker.setOnAction(this::colorChangeEvent);
  }

  private void colorChangeEvent(ActionEvent event){
    ColorPicker node = (ColorPicker)event.getSource();
    String colorValue = "#" + node.getValue().toString().substring(2, 8);
    model.changeLayoutColor(node.getId(), colorValue);
  }
  
  private void browseLocalPathEvent(MouseEvent event) {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    File selectedFile = directoryChooser.showDialog(dataCenter.getWindow());
    if(selectedFile != null && selectedFile.isDirectory()) {
      String selectedLocalPath = selectedFile.getAbsolutePath();
      view.getField(Filed.localPath).setText(selectedLocalPath + "/");
    }
    Stage stage = (Stage) view.getDialogPane().getScene().getWindow();
    stage.toFront();
  }
  
  private void setUpExistingCategoriteEvent() {
    for(Node node : existingCategories.getChildren()) {
      node.setOnMousePressed(this::targetCategoryEvent);
    }
  }
  
  private void addNewCategoryEvent(ActionEvent event) {
    if(addNameField.getText().trim().isEmpty()) {
      // add some error messages.
      return;
    }
    
    String name = addNameField.getText().trim();
    if(!dataCenter.isCategoryExist(name)) {
      model.addNewCategory(name);
      Label newCategory = new Label(name);
      newCategory.prefWidthProperty().bind(existingCategories.widthProperty().divide(3));
      newCategory.setId("setting-category-item");
      
      newCategory.setOnMousePressed(this::targetCategoryEvent);
      existingCategories.getChildren().add(newCategory);
    }
  }
  
  private void targetCategoryEvent(MouseEvent event) {
    Label targetCategory = (Label) event.getSource();
    editNameField.setText(targetCategory.getText());
    selectedCategory = targetCategory;
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
    model.editCategoryName(newName, selectedCategory.getText());
    selectedCategory.setText(newName);
  }
  
}
