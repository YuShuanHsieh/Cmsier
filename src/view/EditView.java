package view;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.scene.control.MenuItem;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ContextMenu;
import view.component.ImageButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import model.utility.DataHelper;
import system.Statement;
import system.data.Data;
import system.data.Page;
import system.data.SetPage;
import system.data.SettingItem;
import system.data.Settings;
import system.data.SimplePage;

public class EditView implements View{
	
  public enum RIGHTMENU{add, delete}
  
  private BorderPane view;
	private TreeView<Page> TreeViewOfSetPage;
	private TreeItem<Page> root;
	private HTMLEditor editArea;
	private CheckBox isShowBox;
	private TextField showInput;
	private Button insertButton;
	private final Image showIconImg;
	private final Image offIconImg;
	private DataHelper dataHelper;
	private ImageView imgInstruction;
	
	/*
	 * The instruction that determines the update statement.
	 * */
	public static final String UPDATE_LIST = "UPDATE_LIST";
	public static final String UPDATE_MENUITEM = "UPDATE_MENUITEM";
	
	public EditView() {
	  dataHelper = new DataHelper();
	  view = new BorderPane();

	  editArea = new HTMLEditor();
	  editArea.setId("edit");
	  
	  
	  TreeViewOfSetPage = new TreeView<Page>();
	  TreeViewOfSetPage.setId("tree"); 
	  TreeViewOfSetPage.setPrefHeight(760);
	  
	  root = new TreeItem<Page> ();
	  TreeViewOfSetPage.setRoot(root);
	  
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
	public void init() {
	  /* Add customized button into HTMLeditor */
	  ToolBar node = (ToolBar)editArea.lookup(".top-toolbar");
	  insertButton = new Button("Insert Image");
	  node.getItems().add(insertButton);

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
	
	@Override
  public <T> void updateStatement(String instruction,Statement<T> statement){
    if(!statement.getResult()){
      return;
    }
    
    if(instruction.equals(UPDATE_LIST)){
      root.getChildren().clear();
      Data data = (Data)statement.getValue();
      createTreeView(data);
    }
    
    if(instruction.equals(UPDATE_MENUITEM)){
      Settings settings = (Settings)statement.getValue();
      setTreeItemGraphic(settings);
    }
    
  }
	
	/*
	 * @return the pop-up menu interface of right click event.
	 *  */
	public ContextMenu getRightButtonMenu(RIGHTMENU type) {
	  ContextMenu rightButtonMenu = new ContextMenu();
	  
	  switch(type) {
	    case add:
	      MenuItem newItem = new MenuItem("add a page");
	      rightButtonMenu.getItems().add(newItem);
	      break;
	    case delete:
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
	
	public Button getInsertButton() {
    return this.insertButton;
  }
	
	
	private void createTreeView(final Data data){
    for(SetPage setPage: data.getList()){
      createTreeItem(root, setPage);
    }
  }
	
	private void createTreeItem(TreeItem<Page> parent, SetPage setPage) {
	  TreeItem<Page> pageRoot = new TreeItem<Page> (setPage);
    pageRoot.setExpanded(true);
    
    for(SimplePage simplePage: setPage.getPageList()) {
      TreeItem<Page> pageItem = new TreeItem<Page> (simplePage);
      pageRoot.getChildren().add(pageItem);
    }

    if(setPage.getChild() != null) {
      createTreeItem(pageRoot, setPage.getChild());
    }
    
    parent.getChildren().add(pageRoot);
	}
	
	private void setTreeItemGraphic(Settings settings){
	  List<TreeItem<Page>> treeItemList = root.getChildren();
	  List<TreeItem<Page>> tempList = new LinkedList<TreeItem<Page>>();
	  
	  while(true){ 
	    for(TreeItem<Page> treeItem : treeItemList){
	      if(treeItem.getValue().getName().endsWith(".html")){
	        SimplePage page = (SimplePage)treeItem.getValue();
	        Optional<SettingItem> result = dataHelper.searchSettingMenuItemBySimplePage(settings, page);
	        if(result.isPresent()){
	          treeItem.setGraphic(new ImageView(showIconImg));
	        }
	        else{
	          treeItem.setGraphic(new ImageView(offIconImg));
	        }
	      } else{
	        tempList.addAll(treeItem.getChildren());
	      }
	    }
	    if(tempList.isEmpty()){
	      break;
	    }
	    else{
	      treeItemList = tempList.stream().collect(Collectors.toList());
	      tempList.clear();
	    }
	  }
	}

}
