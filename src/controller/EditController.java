package controller;

import view.EditView;
import model.EditModel;

import java.io.File;
import java.util.Optional;
import controller.AdminController;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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
  private TreeItem<?> selectedItem;

  public EditController() {
    view = new EditView();
    model = new EditModel();
    attached(view, model);
  }
  
  @Override
  public void init(){
    view.init();
    model.init();
    
    view.update();
    
    view.getPane().lookup("#tree").setOnMousePressed(this::selectSimplePage);
    view.getPane().lookup("#save").setOnMousePressed(this::saveThePageContent);
    view.getPane().lookup("#setting").setOnMousePressed(this::openSettingEvent);
    view.getPane().lookup("#upload").setOnMousePressed(this::uploadfile);
    view.getPane().lookup("#isShow").setOnMousePressed(this::checkShowBoxEvent);
       
    ((EditView)view).getInsertButton().setOnMousePressed(this::insertMediaEvent);
  }
  
  private void checkShowBoxEvent(MouseEvent event) {
    TreeView<?> targetTreeView = (TreeView<?>)view.getPane().lookup("#tree");
    TreeItem<?> targetTreeItem = targetTreeView.getSelectionModel().getSelectedItem();
    SimplePage currentSimplePage = (SimplePage)targetTreeItem.getValue();
    TextField showInput = (TextField)view.getPane().lookup("#showInput");
    CheckBox checkShowBox = (CheckBox) event.getSource();
    if(checkShowBox.isSelected()) {
      showInput.setVisible(false);
    }
    else {
      showInput.setVisible(true);
    }
    currentSimplePage.setChangeState(true);
  }
  
  private void openSettingEvent(MouseEvent event){
    AdminController adminController = new AdminController();
    this.getSystemManager().register(adminController);
    adminController.init();
  }
  
  private void saveThePageContent(MouseEvent event) {
    CheckBox checkShow = null;
    String menuItemName = "";
    String content = "";
    
    
    if(view.getPane().lookup("#isShow") instanceof CheckBox) {
      checkShow = (CheckBox) view.getPane().lookup("#isShow");      
    }
    
    if(checkShow.isSelected()) {
      if(view.getPane().lookup("#showInput") instanceof TextField) {
        TextField checkShowInput = (TextField) view.getPane().lookup("#showInput");
        if(!checkShowInput.getText().trim().equals("")){
          menuItemName = checkShowInput.getText();
        }
      }
    }
    
    if(view.getPane().lookup("#edit") instanceof HTMLEditor) {
      HTMLEditor editor = (HTMLEditor)view.getPane().lookup("#edit");
      content = editor.getHtmlText();
    }

    if(selectedItem == null) {
      ViewFactory viewFactory = new ViewFactory();
      viewFactory.createAlertWindow("Please select a page.");
      return;
    }
      
    if(!((EditModel)model).savePageContent(selectedItem.getValue(), content, menuItemName)) {
      ViewFactory viewFactory = new ViewFactory();
      viewFactory.createAlertWindow("This page cannot be saved, please check the content or setting.");
      return;
    }
    
     
    PreviewController preview = new PreviewController();
    systemManager.register(preview);
    preview.init();
  } 
  
  
  private void insertMediaEvent(MouseEvent event) {
    ViewFactory factory = new ViewFactory();
    ExtensionFilter filter = new ExtensionFilter("Image Files", "*.png", "*.jpg");
    FileChooser fileChooser = factory.createFileChooser("Select an image file", filter);
    File selectedFile = fileChooser.showOpenDialog(this.getSystemManager().getWindow());
    if(selectedFile != null){
      HTMLEditor editor = (HTMLEditor) view.getPane().lookup("#edit");
      String clipedContent = ((EditModel)model).clipHTMLContent(editor.getHtmlText());
      StringBuilder stringbuilder = new StringBuilder(clipedContent);
    
      stringbuilder.append("<img src=\"file://" + selectedFile.getPath() + "\">");
      editor.setHtmlText(stringbuilder.toString());
    }
  }
  
  private void selectSimplePage(MouseEvent event) {
    DataHelper dataHelper = new DataHelper();
    TreeView<?> target = (TreeView<?>)event.getSource();
    selectedItem = target.getSelectionModel().getSelectedItem();
    
    if(selectedItem != null && selectedItem.getValue() instanceof Page) {
      Page simplePage = (Page)selectedItem.getValue();
      
      if(currentRightMenu != null) {
        currentRightMenu.hide();
        currentRightMenu = null;
      }
      
      if(simplePage.toString().endsWith(".html")){
        
        view.getPane().lookup("#center-edit").setVisible(true);
        //((BorderPane)view.getPane()).getCenter().setVisible(true);
        ((HTMLEditor)view.getPane().lookup("#edit")).setHtmlText(((SimplePage)simplePage).getPageContent());
        
        Settings settings = this.getSystemManager().getSettings();
        Optional<SettingItem> menuItem = dataHelper.searchSettingMenuItemBySimplePage(settings, (SimplePage)simplePage);
        
        if(menuItem.isPresent()) {
          ((CheckBox)view.getPane().lookup("#isShow")).setSelected(true);
          ((TextField)view.getPane().lookup("#showInput")).setText(menuItem.get().getName());
          view.getPane().lookup("#showInput").setVisible(true);
        }
        else {
          ((CheckBox)view.getPane().lookup("#isShow")).setSelected(false);
          ((TextField)view.getPane().lookup("#showInput")).setText("");
          view.getPane().lookup("#showInput").setVisible(false);
        }
        
        if(event.isSecondaryButtonDown() && !simplePage.getName().contains("index")) {
          currentRightMenu = ((EditView)view).getRightButtonMenu(2);
          currentRightMenu.getItems().get(0).setOnAction(this::deleteExistingPageEvent);
          currentRightMenu.show( view.getPane(), event.getScreenX(), event.getScreenY());
        }
      }
      else {
        if(event.isSecondaryButtonDown()&& !simplePage.getName().contains("default")) {
          currentRightMenu = ((EditView)view).getRightButtonMenu(1);
          currentRightMenu.getItems().get(0).setOnAction(this::addNewPageEvent);
          currentRightMenu.show( view.getPane(), event.getScreenX(), event.getScreenY());;
        }
        
        view.getPane().lookup("#center-edit").setVisible(false);
        
      }
    }
  }
  
  private void addNewPageEvent(ActionEvent event) {
    TextInputDialog dialog = ((EditView)view).createAddNewPageDialog();
    Optional<String> result = dialog.showAndWait();
    if(!result.isPresent()) {
      return;
    }
    TreeView<?> targetTreeView = (TreeView<?>)view.getPane().lookup("#tree");
    TreeItem<?> targetTreeItem = targetTreeView.getSelectionModel().getSelectedItem();
    if(targetTreeItem.getValue() instanceof Page) {
      SetPage page = (SetPage)targetTreeItem.getValue();
      ((EditModel)model).addNewSimplePage(page, result.get());
    } 
  }
  
  // Type : SetPage class(1) and SimplePage class(2).
  private void deleteExistingPageEvent(ActionEvent event) {
    TreeView<?> targetTreeView = (TreeView<?>)view.getPane().lookup("#tree");
    TreeItem<?> targetTreeItem = targetTreeView.getSelectionModel().getSelectedItem();
    if(targetTreeItem.getValue() instanceof Page) {
      Page page = (Page)targetTreeItem.getValue();
      ((EditModel)model).deleteExistingPage(page);
    }
  }
  
  private void uploadfile(MouseEvent event) {
    UploadController uploadController = new UploadController();
    systemManager.register(uploadController);
    uploadController.init();
  }
  
}
