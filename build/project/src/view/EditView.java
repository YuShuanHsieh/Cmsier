package view;

import java.util.Optional;

import controller.Controller;
import javafx.scene.control.MenuItem;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ContextMenu;
import javafx.scene.web.HTMLEditor;
import view.component.ImageButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import data.SimplePage;
import data.SetPage;
import data.SettingItem;
import data.Data;
import data.Page;
import model.utility.DataHelper;

public class EditView implements View{
	
  private BorderPane view;
  private Controller controller;
	private TreeView<Page> TreeViewOfSetPage;
	private HTMLEditor editArea;
	private CheckBox isShowBox;
	private TextField showInput;
	private Button insertButton;
	private final Image showIconImg;
	private final Image offIconImg;
	private DataHelper dataHelper;
	private ImageView imgInstruction;
	
	public EditView() {
	  dataHelper = new DataHelper();
	  view = new BorderPane();

	  editArea = new HTMLEditor();
	  editArea.setId("edit");
	  
	  TreeViewOfSetPage = new TreeView<Page>();
	  TreeViewOfSetPage.setId("tree"); 
	  TreeViewOfSetPage.setPrefHeight(760);
	  
	  isShowBox = new CheckBox();
	  showInput = new TextField("Please enter a link name");
	  
	  showIconImg = new Image(getClass().getResourceAsStream("img/menu-on.png"));
	  offIconImg = new Image(getClass().getResourceAsStream("img/menu-off.png"));
	  
	  imgInstruction = new ImageView(new Image(View.class.getResourceAsStream("img/instructions.png")));
	}
	
	@Override
	public Pane getPane() {
	  return this.view;
	}
	
	@Override
	public void showPane() {
	  // do nothing.
	}
	
	@Override
	public void setController(Controller controller) {
	  this.controller = controller;
	}
	
	@Override
	public void init() {
	  
	  /* Customize HTML editor */
	  ToolBar node = (ToolBar)editArea.lookup(".top-toolbar");
	  insertButton = new Button("Insert Image");
	  node.getItems().add(insertButton);
	  //editArea.sk
	  VBox vbox = new VBox();
    vbox.setId("box");
    vbox.setPadding(new Insets(5));
    
    Button uploadbutton = new ImageButton("img/upload.png", "img/upload_active.png", "Upload");
    uploadbutton.setId("upload");
    uploadbutton.setTooltip(new Tooltip("Upload web pages"));
    
    Button saveButton = new ImageButton("img/save.png", "img/save_active.png", "Save");
    saveButton.setId("save");
    saveButton.setTooltip(new Tooltip("Save this page"));
    
    Button settingButton = new ImageButton("img/setting.png", "img/setting_active.png", "Settings");
    settingButton.setId("setting");
    settingButton.setTooltip(new Tooltip("Website settings"));
	  
    StackPane stackPane = new StackPane();
	  GridPane centerSubView = new GridPane();
	  centerSubView.setId("center-edit");
	  
	  centerSubView.add( editArea, 0, 1, 5, 1);
	  centerSubView.setVgap(5);
	  centerSubView.setPadding(new Insets(5));
	  
	  Label isShowLabel = new Label("Show this page on the menu");
	  isShowLabel.setId("showLabel");
	  
	  isShowBox.setId("isShow");
	  
	  showInput.setId("showInput");
	  
	  centerSubView.add( isShowBox, 0, 0);
	  centerSubView.add( isShowLabel, 1, 0);
	  centerSubView.add( showInput, 2, 0);
	  centerSubView.setVisible(false);
	  
	  stackPane.getChildren().addAll( imgInstruction, centerSubView);
	  
	  TreeViewOfSetPage.setShowRoot(false);
	  vbox.getChildren().addAll(TreeViewOfSetPage);
	  
	  HBox hbox = new HBox();
	  hbox.getChildren().addAll(settingButton, saveButton, uploadbutton);
	  
	  view.setTop(hbox);
	  view.setLeft(vbox);
	  view.setCenter(stackPane);
	}
	
	public ContextMenu getRightButtonMenu(int type) {
	  ContextMenu rightButtonMenu = new ContextMenu();
	  
	  switch(type) {
	    case 1:
	      MenuItem newItem = new MenuItem("add a page");
	      rightButtonMenu.getItems().add(newItem);
	      break;
	    case 2:
	      MenuItem deleteItem = new MenuItem("delete the page");
        rightButtonMenu.getItems().add(deleteItem);
        break;
	  }
    
	  return rightButtonMenu;
	}
	
	public TextInputDialog createAddNewPageDialog() {
	  
	  TextInputDialog newPageDialog = new TextInputDialog(); 
	  newPageDialog.setTitle("Add a new Page to website");
	  newPageDialog.setHeaderText("Please enter a unique name: ");
	  
	  return newPageDialog;
	  
	}
	
	public void update(){
	  Data data = controller.getSystemManager().getData();
	  createTreeView(data);
	}
	
	private void createTreeView(final Data data){
    TreeItem<Page> root = new TreeItem<Page> ();
    for(SetPage setPage: data.getList()){
      createTreeItem(root, setPage);
    }
    TreeViewOfSetPage.setRoot(root);
  }
	
	private void createTreeItem(TreeItem<Page> parent, SetPage setPage) {
	  TreeItem<Page> pageRoot = new TreeItem<Page> (setPage);
    pageRoot.setExpanded(true);
    
    for(SimplePage simplePage: setPage.getPageList()) {
      TreeItem<Page> pageItem;
      Optional<SettingItem> result = dataHelper.searchSettingMenuItemBySimplePage(controller.getSystemManager().getSettings(), simplePage);
      if(result.isPresent()){
        pageItem = new TreeItem<Page> (simplePage, new ImageView(showIconImg));
      }
      else {
        pageItem = new TreeItem<Page> (simplePage, new ImageView(offIconImg));
      }
      pageRoot.getChildren().add(pageItem);
    }

    if(setPage.getChild() != null) {
      createTreeItem(pageRoot, setPage.getChild());
    }
    
    parent.getChildren().add(pageRoot);
	}
	
	public Button getInsertButton() {
	  return this.insertButton;
	}

}
