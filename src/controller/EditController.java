package controller;

import view.EditView;
import view.View;
import view.EditView.RIGHTMENU;
import model.EditModel;
import model.GenerateModel;
import model.Model;
import model.GenerateModel.GENERATE;
import java.io.File;
import java.util.Optional;
import java.util.stream.Collectors;
import controller.ControllerFactory.Id;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
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
import system.DataCenter;
import system.Statement;
import system.data.Category;
import system.data.SinglePage;
import view.component.ViewFactory;
import javafx.collections.ObservableList;

/**
 * Edit module: enable user to edit page's content.
 * It contains functions of page selection, text editor, and save content.
 *  */

public class EditController implements Controller{
  
  private EditView view;
  private EditModel editModel;
  private GenerateModel generateModel;
  private DataCenter dataCenter;
  
  private ContextMenu currentRightMenu = null;
  private TreeItem<?> selectedPage;
  private TreeView<?> pageList;
  private Button saveButton;
  private Button settingButton;
  private Button uploadButton;
  private Button insertButton;
  private CheckBox checkBox;
  private TextField menuNameField;
  private TextField titleField;
  private ViewFactory viewFactory;
  private HTMLEditor editor;
  private GridPane centerPane;
  private ChoiceBox<Category> selectCategory;
  private ObservableList<Category> selectCategoryList;

  public EditController(DataCenter dataCenter) {
    this.dataCenter = dataCenter;
    
    view = new EditView();
    viewFactory = new ViewFactory();
    
    editModel = new EditModel(dataCenter);
    attached(view, editModel);
    
    generateModel = new GenerateModel(dataCenter);
    attached(view, generateModel);
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
  public void setParent(Controller parent) {
  }
  
  @Override
  public void init(){
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
    selectCategory = view.getCategoryChoiceBox();
    saveButton = view.getSaveButton();
    insertButton = view.getInsertButton();
    
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
    insertButton.setOnMousePressed(this::insertMediaEvent);
  }
  
  private void checkShowBoxEvent(MouseEvent event) {
    menuNameField.setVisible(!checkBox.isSelected());
  }
  
  private void openSettingEvent(MouseEvent event){
    ControllerFactory.create(Id.ADMIN, dataCenter, this);
  }
  
  private void saveThePageContent(MouseEvent event) {
    SinglePage page = (SinglePage)selectedPage.getValue();
    String content = editor.getHtmlText();
    String title = titleField.getText();
    String menuItemName;
    Category category;
    
    if(checkBox.isSelected()) 
      menuItemName = menuNameField.getText(); 
    else menuItemName = "";
   
    /**  */
    if(validateField(title, menuItemName)) {
      page.setTitle(title);
      category = selectCategory.getSelectionModel().getSelectedItem();
      editModel.savePageContent(page, content, menuItemName, category);
      
      generateModel.generateSinglePage(GENERATE.draft, page);
      updateViewElement(checkBox.isSelected());
        
      ControllerFactory.create(Id.PREVIEW, dataCenter);
    }
  } 
  
  private void updateViewElement(boolean checkBoxSelected) {
    if(checkBoxSelected) {
      view.updateStatement(EditView.UPDATE_ON_MENU, Statement.success(selectedPage));
    }
    else {
      view.updateStatement(EditView.UPDATE_OFF_MENU, Statement.success(selectedPage));
    }
  }
  
  private Boolean validateField(String title, String menuName) {
    if(selectedPage == null || !selectedPage.getValue().toString().endsWith(".html")) {
      viewFactory.createAlertWindow("Please select a page.");
      return false;
    }
    else if(title.trim().isEmpty()) {
      viewFactory.createAlertWindow("The title field should not be empty.");
      return false;
    }
    else if(checkBox.isSelected() && menuName.trim().isEmpty()) {
      viewFactory.createAlertWindow("The name field of menu should not be empty.");
      return false;
    }
    else {
      return true;
    }
  }
  
  private void insertMediaEvent(MouseEvent event) {
    ExtensionFilter filter = new ExtensionFilter("Image Files", "*.png", "*.jpg");
    FileChooser fileChooser = viewFactory.createFileChooser("Select an image file", filter);
    File selectedFile = fileChooser.showOpenDialog(dataCenter.getWindow());
    if(selectedFile != null){
      String clipedContent = editModel.clipHTMLContent(editor.getHtmlText());
      StringBuilder stringbuilder = new StringBuilder(clipedContent);
    
      stringbuilder.append("<img src=\"file://" + selectedFile.getPath() + "\" style=\"max-width: 100%;\">");
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
    ControllerFactory.create(Id.UPLOAD, dataCenter);
  }
  
  /** Should be modified later. */
  public void updateCacheData() {
    dataCenter.updateCategory();
    dataCenter.organize();
    selectCategoryList = FXCollections.observableList(dataCenter.getCategory().values().stream().collect(Collectors.toList()));
    selectCategory.setItems(selectCategoryList);
    if(selectedPage != null && selectedPage.getValue().toString().endsWith(".html")) {
      SinglePage targetPage = (SinglePage)selectedPage.getValue();
      selectCategory.setValue(targetPage.getCategory());
    }
  }

}
