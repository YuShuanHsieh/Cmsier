package controller;

import view.EditView;
import view.EditView.RIGHTMENU;
import model.EditModel;
import java.io.File;
import java.util.Optional;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.event.ActionEvent;
import model.utility.DataHelper;
import system.data.Page;
import system.data.SetPage;
import system.data.SettingItem;
import system.data.Settings;
import system.data.SimplePage;
import view.component.ViewFactory;
import controller.UploadController;

public class EditController extends Controller{
  
  private ContextMenu currentRightMenu = null;
  private TreeItem<?> selectedPage;
  private TreeView<?> pageList;
  private Button saveButton;
  private Button settingButton;
  private Button uploadButton;
  private CheckBox checkBox;
  private TextField menuNameField;
  private ViewFactory viewFactory;
  private HTMLEditor editor;
  private GridPane centerPane;

  public EditController() {
    view = new EditView();
    model = new EditModel();
    viewFactory = new ViewFactory();
  }
  
  @Override
  public void init(){
    attached(view, model);
    
    view.init();
    model.init();
    
    pageList = (TreeView<?>)view.getPane().lookup("#tree");
    saveButton = (Button)view.getPane().lookup("#save");
    settingButton = (Button)view.getPane().lookup("#setting");
    uploadButton = (Button)view.getPane().lookup("#upload");
    checkBox = (CheckBox)view.getPane().lookup("#isShow");
    menuNameField = (TextField)view.getPane().lookup("#showInput");
    editor = (HTMLEditor)view.getPane().lookup("#edit");
    centerPane = (GridPane)view.getPane().lookup("#center-edit");
    
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
    SimplePage currentSimplePage = (SimplePage)selectedPage.getValue();
    if(checkBox.isSelected()) {
      menuNameField.setVisible(false);
    }
    else {
      menuNameField.setVisible(true);
    }
    currentSimplePage.setChangeState(true);
  }
  
  private void openSettingEvent(MouseEvent event){
    AdminController adminController = new AdminController();
    systemManager.register(adminController);
    adminController.init();
  }
  
  private void saveThePageContent(MouseEvent event) {
    String menuItemName = "";
    String content = "";
   
    if(selectedPage == null) {
      viewFactory.createAlertWindow("Please select a page.");
      return;
    }
    
    if(checkBox.isSelected()) {
      menuItemName = menuNameField.getText();
      if(menuItemName.trim().isEmpty()){
        viewFactory.createAlertWindow("The name field of menu should not be empty.");
        return;
      }
    }
    content = editor.getHtmlText();
    
    if(!((EditModel)model).savePageContent(selectedPage.getValue(), content, menuItemName)) {
      viewFactory.createAlertWindow("This page cannot be saved, please check the content or setting.");
      return;
    }
        
    PreviewController preview = new PreviewController();
    systemManager.register(preview);
    preview.init();
  } 
  
  private void insertMediaEvent(MouseEvent event) {
    ExtensionFilter filter = new ExtensionFilter("Image Files", "*.png", "*.jpg");
    FileChooser fileChooser = viewFactory.createFileChooser("Select an image file", filter);
    File selectedFile = fileChooser.showOpenDialog(this.getSystemManager().getWindow());
    if(selectedFile != null){
      String clipedContent = ((EditModel)model).clipHTMLContent(editor.getHtmlText());
      StringBuilder stringbuilder = new StringBuilder(clipedContent);
    
      stringbuilder.append("<img src=\"file://" + selectedFile.getPath() + "\">");
      editor.setHtmlText(stringbuilder.toString());
    }
  }
  
  private void selectSimplePage(MouseEvent event) {
    selectedPage = pageList.getSelectionModel().getSelectedItem();
    DataHelper dataHelper = new DataHelper();
    
    if(selectedPage == null) {
      return;
    }
    
    Page page = (Page)selectedPage.getValue();
      
    if(currentRightMenu != null) {
      currentRightMenu.hide();
      currentRightMenu = null;
    }
      
    if(page.toString().endsWith(".html")){
        
      centerPane.setVisible(true);
      editor.setHtmlText(((SimplePage)page).getPageContent());
        
      Settings settings = this.getSystemManager().getSettings();
      Optional<SettingItem> menuItem = dataHelper.searchSettingMenuItemBySimplePage(settings, (SimplePage)page);
        
      if(menuItem.isPresent()) {
        checkBox.setSelected(true);
        menuNameField.setText(menuItem.get().getName());
        menuNameField.setVisible(true);
      }
      else {
        checkBox.setSelected(false);
        menuNameField.setText("");
        menuNameField.setVisible(false);
      }
        
      if(event.isSecondaryButtonDown() && !page.getName().contains("index")) {
        currentRightMenu = ((EditView)view).getRightButtonMenu(RIGHTMENU.delete);
        currentRightMenu.getItems().get(0).setOnAction(this::deleteExistingPageEvent);
        currentRightMenu.show( view.getPane(), event.getScreenX(), event.getScreenY());
      }
    }
    else {
      if(event.isSecondaryButtonDown()&& !page.getName().contains("default")) {
        currentRightMenu = ((EditView)view).getRightButtonMenu(RIGHTMENU.add);
        currentRightMenu.getItems().get(0).setOnAction(this::addNewPageEvent);
        currentRightMenu.show( view.getPane(), event.getScreenX(), event.getScreenY());;
      }
        
      centerPane.setVisible(false);
    }
  }
  
  private void addNewPageEvent(ActionEvent event) {
    TextInputDialog dialog = ((EditView)view).createAddNewPageDialog();
    Optional<String> result = dialog.showAndWait();
    if(!result.isPresent()) {
      return;
    }
    SetPage page = (SetPage)selectedPage.getValue();
    ((EditModel)model).addNewSimplePage(page, result.get());
  }
  
  private void deleteExistingPageEvent(ActionEvent event) {
    Page page = (Page)selectedPage.getValue();
    ((EditModel)model).deleteExistingPage(page);
  }
  
  private void uploadfile(MouseEvent event) {
    UploadController uploadController = new UploadController();
    systemManager.register(uploadController);
    uploadController.init();
  }
  
}
