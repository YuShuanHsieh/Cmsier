package controller;

import view.EditView;
import view.EditView.RIGHTMENU;
import model.EditModel;
import model.GenerateModel;
import model.GenerateModel.GENERATE;
import model.utility.XmlHelper;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import controller.AdminController;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import system.Statement;
import system.data.Category;
import system.data.SinglePage;
import view.component.ViewFactory;
import controller.UploadController;
import javafx.collections.ObservableList;

public class EditController extends Controller{
  
  private EditModel editModel;
  private GenerateModel generateModel;
  
  private ContextMenu currentRightMenu = null;
  private TreeItem<?> selectedPage;
  private TreeView<?> pageList;
  private Button saveButton;
  private Button settingButton;
  private Button uploadButton;
  private CheckBox checkBox;
  private TextField menuNameField;
  private TextField titleField;
  private ViewFactory viewFactory;
  private HTMLEditor editor;
  private GridPane centerPane;
  private ChoiceBox<Category> selectCategory;
  private ObservableList<Category> selectCategoryList;

  public EditController() {
    view = new EditView();
    viewFactory = new ViewFactory();
  }
  
  @Override
  public void init(){
    editModel = new EditModel();
    attached(view, editModel);
    
    generateModel = new GenerateModel();
    attached(view, generateModel);
    
    view.init();
    editModel.init();
    generateModel.init();
    
    pageList = (TreeView<?>)view.getPane().lookup("#tree");
    settingButton = (Button)view.getPane().lookup("#setting");
    uploadButton = (Button)view.getPane().lookup("#upload");
    checkBox = (CheckBox)view.getPane().lookup("#isShow");
    menuNameField = (TextField)view.getPane().lookup("#showInput");
    titleField = (TextField)view.getPane().lookup("#titleInput");
    editor = (HTMLEditor)view.getPane().lookup("#edit");
    centerPane = (GridPane)view.getPane().lookup("#center-edit");
    selectCategory = ((EditView)view).getCategoryChoiceBox();
    saveButton = ((EditView)view).getSaveButton();
    
    selectCategoryList = FXCollections.observableList(dataCenter.getCategory().values().stream().collect(Collectors.toList()));

    setEvent();
  }
  
  @Override
  public void setEvent(){
    pageList.setOnMousePressed(this::selectSimplePage);
    saveButton.setOnMousePressed(this::saveThePageContent);
    settingButton.setOnMousePressed(this::openSettingEvent);
    uploadButton.setOnMousePressed(this::uploadfile);
    checkBox.setOnMousePressed(this::checkShowBoxEvent);
    ((EditView)view).getInsertButton().setOnMousePressed(this::insertMediaEvent);
  }
  
  /*
   * Below is the function of each event.
  */
  private void checkShowBoxEvent(MouseEvent event) {
    if(checkBox.isSelected()) {
      menuNameField.setVisible(false);
    }
    else {
      menuNameField.setVisible(true);
    }
  }
  
  private void openSettingEvent(MouseEvent event){
    AdminController adminController = new AdminController();
    adminController.setDataCenter(dataCenter);
    adminController.setParent(this);
    adminController.init();
  }
  
  private void saveThePageContent(MouseEvent event) {
    SinglePage page = (SinglePage)selectedPage.getValue();
    String content = editor.getHtmlText();
    String title = titleField.getText();
    String menuItemName;
    
    if(checkBox.isSelected()) 
      menuItemName = menuNameField.getText(); 
    else menuItemName = "";
   
    if(selectedPage == null || !selectedPage.getValue().toString().endsWith(".html")) {
      viewFactory.createAlertWindow("Please select a page.");
      return;
    }
    else if(title.trim().isEmpty()) {
      viewFactory.createAlertWindow("The title field should not be empty.");
      return;
    }
    else if(checkBox.isSelected() && menuItemName.trim().isEmpty()) {
      viewFactory.createAlertWindow("The name field of menu should not be empty.");
      return;
    }
    else {
      page.setTitle(title);
      if(!editModel.savePageContent(page, content, menuItemName, selectCategory.getSelectionModel().getSelectedItem())) {
        viewFactory.createAlertWindow("This page cannot be saved, please check the content or setting.");
        return;
      }
      else {
        generateModel.generateSinglePage(GENERATE.draft, page);
        PreviewController preview = new PreviewController();
        preview.setDataCenter(dataCenter);
        preview.init();
      }
      
      if(checkBox.isSelected()) {
        view.updateStatement(EditView.UPDATE_ON_MENU, Statement.success(selectedPage));
      }
      else {
        view.updateStatement(EditView.UPDATE_OFF_MENU, Statement.success(selectedPage));
      }
    }
  } 
  
  private void insertMediaEvent(MouseEvent event) {
    ExtensionFilter filter = new ExtensionFilter("Image Files", "*.png", "*.jpg");
    FileChooser fileChooser = viewFactory.createFileChooser("Select an image file", filter);
    File selectedFile = fileChooser.showOpenDialog(dataCenter.getWindow());
    if(selectedFile != null){
      String clipedContent = editModel.clipHTMLContent(editor.getHtmlText());
      StringBuilder stringbuilder = new StringBuilder(clipedContent);
    
      stringbuilder.append("<img style=\"max-width:100%;\" src=\"file://" + selectedFile.getPath() + "\">");
      editor.setHtmlText(stringbuilder.toString());
    }
  }
  
  private void selectSimplePage(MouseEvent event) {
    selectedPage = pageList.getSelectionModel().getSelectedItem();
    
    if(selectedPage == null) {
      return;
    }
    
    SinglePage page = (SinglePage)selectedPage.getValue();
      
    if(currentRightMenu != null) {
      currentRightMenu.hide();
      currentRightMenu = null;
    }
      
    if(!page.getDirectory().equals("")){
      showPageContent(page);
      
      if(event.isSecondaryButtonDown() && !page.getName().contains("index")) {
        currentRightMenu = ((EditView)view).getRightButtonMenu(RIGHTMENU.delete);
        currentRightMenu.getItems().get(0).setOnAction(this::deleteExistingPageEvent);
        currentRightMenu.show( view.getPane(), event.getScreenX(), event.getScreenY());
      }
    }
    else {
      centerPane.setVisible(false);
      
      if(event.isSecondaryButtonDown()&& !page.getName().contains("default")) {
        currentRightMenu = ((EditView)view).getRightButtonMenu(RIGHTMENU.add);
        currentRightMenu.getItems().get(0).setOnAction(this::addNewPageEvent);
        currentRightMenu.show( view.getPane(), event.getScreenX(), event.getScreenY());;
      }
    }
  }
  
  private void showPageContent(SinglePage targetPage) {
    centerPane.setVisible(true);
    editor.setHtmlText(targetPage.getContent());
    if(targetPage.isOnMenu()) {
      checkBox.setSelected(true);
      menuNameField.setVisible(true);
      menuNameField.setText(dataCenter.getSettings().getMenuItemName(targetPage.getName()));
    }
    else {
      checkBox.setSelected(false);
      menuNameField.setText("");
      menuNameField.setVisible(false);
    }
    titleField.setText(targetPage.getTitle());
    selectCategory.setItems(selectCategoryList); //selectCategoryList
    selectCategory.setValue(targetPage.getCategory());
  }
  
  private void addNewPageEvent(ActionEvent event) {
    TextInputDialog dialog = ((EditView)view).createAddNewPageDialog();
    Optional<String> result = dialog.showAndWait();
    if(!result.isPresent()) {
      return;
    }
    Optional<SinglePage> addResult = editModel.addNewPage(result.get());
    if(addResult.isPresent()) {
      showPageContent(addResult.get());
    }
  }
  
  private void deleteExistingPageEvent(ActionEvent event) {
    SinglePage page = (SinglePage)selectedPage.getValue();
    editModel.deleteExistingPage(page);
    centerPane.setVisible(false);
    view.updateStatement(EditView.UPDATE_TREEVIEW_DELETE, Statement.success(selectedPage));
  }
  
  private void uploadfile(MouseEvent event) {
    UploadController uploadController = new UploadController();
    uploadController.setDataCenter(dataCenter);
    uploadController.init();
  }
  
  /** Should be modified later. */
  public void updateCacheData() {
    Map<String, Category> categories = XmlHelper.retrieveCatetoryFromXML(dataCenter.getSettings().getLocalPath());
    dataCenter.setCategory(categories);
    dataCenter.organize();
    selectCategoryList = FXCollections.observableList(categories.values().stream().collect(Collectors.toList()));
    selectCategory.setItems(selectCategoryList);
    if(selectedPage != null && selectedPage.getValue().toString().endsWith(".html")) {
      SinglePage targetPage = (SinglePage)selectedPage.getValue();
      selectCategory.setValue(targetPage.getCategory());
    }
  }
  
}
