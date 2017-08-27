package view;

import javafx.scene.control.MenuItem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import system.DataCenter;
import system.Statement;
import system.data.Category;
import system.data.PageCollection;
import system.data.SinglePage;

public class EditView implements View{
	
  public enum RIGHTMENU{add, delete}
  
  private BorderPane view;
	private TreeView<SinglePage> TreeViewOfSetPage;
	private TreeItem<SinglePage> root;
	private HTMLEditor editArea;
	private CheckBox isShowBox;
	private TextField showInput;
	private TextField titleInput;
	private Button insertButton;
	private Button saveButton;
	private final Image showIconImg;
	private final Image offIconImg;
	private ImageView imgInstruction;
	private ChoiceBox<Category> selectCategory;
	private TreeItem<SinglePage> pagesRoot;
	
	/*
	 * The instruction that determines the update statement.
	 * */
	public static final String SETUP_TREEVIEW = "SETUP_TREEVIEW";
	public static final String UPDATE_TREEVIEW_ADD = "UPDATE_TREEVIEW_ADD";
	public static final String UPDATE_TREEVIEW_DELETE = "UPDATE_TREEVIEW_DELETE";
	public static final String UPDATE_ON_MENU = "UPDATE_ON_MENU";
	public static final String UPDATE_OFF_MENU = "UPDATE_OFF_MENU";
	public static final String UPDATE_CATEGORY = "UPDATE_CATEGORY";
	
	public EditView() {
	  view = new BorderPane();
	  view.setId("edit-view");
	  
	  String css = DataCenter.class.getResource("layout.css").toExternalForm(); 
	  view.getStylesheets().add(css);

	  editArea = new HTMLEditor();
	  editArea.setId("edit");
	  
	  
	  TreeViewOfSetPage = new TreeView<SinglePage>();
	  TreeViewOfSetPage.setId("tree"); 
	  TreeViewOfSetPage.setPrefHeight(760);
	  
	  root = new TreeItem<SinglePage> ();
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
	  
	  selectCategory = new ChoiceBox<Category>();
    selectCategory.setId("selectCategory");
    
	  node.getItems().addAll(selectCategory, insertButton);
	  node.getStylesheets().add(DataCenter.class.getResource("layout.css").toExternalForm());
	  
	  
	  
	  saveButton = new Button("Save The Content");
	  ToolBar bottomBar = (ToolBar)editArea.lookup(".bottom-toolbar");
	  bottomBar.getItems().add(saveButton);
	  saveButton.setId("save");
	  

	  VBox vbox = new VBox();
    vbox.setId("box");
    vbox.setPrefWidth(180);
    //vbox.setPadding(new Insets(5));
    
    Button uploadbutton = new ImageButton("img/upload.png", "img/upload_active.png", "Upload");
    uploadbutton.setId("upload");
    uploadbutton.setTooltip(new Tooltip("Upload web pages"));

    Button settingButton = new ImageButton("img/setting.png", "img/setting_active.png", "Settings");
    settingButton.setId("setting");
    settingButton.setTooltip(new Tooltip("Website settings"));
	  
    StackPane stackPane = new StackPane();
	  GridPane centerSubView = new GridPane();
	  centerSubView.setId("center-edit");
	  
	  centerSubView.add( editArea, 0, 1, 11, 1);
	  centerSubView.setVgap(5);
	  centerSubView.setPadding(new Insets(5));
	  
	  Label isShowLabel = new Label("TOP Menu");
	  isShowLabel.setId("showLabel");
	  
	  isShowBox.setId("isShow");
	  
	  showInput.setId("showInput");
	  
	  Label titleLabel = new Label("Title");
	  titleLabel.setId("titleLabel");
	  titleInput = new TextField("");
	  titleInput.setPrefWidth(450);
	  titleInput.setId("titleInput");
	  
	  HBox titlebox = new HBox();
	  titlebox.setSpacing(5);
	  titlebox.setAlignment(Pos.CENTER);
	  titlebox.getChildren().addAll(titleLabel, titleInput, isShowBox, isShowLabel, showInput);
	  
	  centerSubView.add( titlebox, 0, 0);
	  centerSubView.setVisible(false);
	  

	  stackPane.getChildren().addAll( imgInstruction, centerSubView);
	  
	  TreeViewOfSetPage.setShowRoot(false);
	  
	  HBox hbox = new HBox();
	  hbox.setSpacing(15);
	  hbox.setStyle("-fx-padding: 5 10 5 0;");
	  hbox.setAlignment(Pos.CENTER);
	  hbox.getChildren().addAll(settingButton, uploadbutton);
	  
	  vbox.getChildren().addAll(hbox, TreeViewOfSetPage);
	  
	  HBox authorHbox = new HBox();
	  authorHbox.setId("authorHbox");
	  authorHbox.setAlignment(Pos.CENTER);
	  authorHbox.getChildren().add(new Label("@Developer Yu-Shuan Hsieh"));
	  
	  BorderPane testView = new BorderPane();
	  //testView.setTop(hbox);
	  testView.setCenter(stackPane);
	  testView.setBottom(authorHbox);
	  testView.setStyle("-fx-padding: 10 0 0 0;");
	  
	  
	  view.setLeft(vbox);
	  view.setCenter(testView);
	}
	
	@Override
  public <T> void updateStatement(String instruction,Statement<T> statement){
    if(!statement.getResult()){
      return;
    }
    
    if(instruction.equals(SETUP_TREEVIEW)){
      PageCollection pageCollection = (PageCollection)statement.getValue();
      setUpTreeView(pageCollection);
    }
    
    if(instruction.equals(UPDATE_TREEVIEW_ADD)){
      SinglePage newPage = (SinglePage)statement.getValue();
      TreeItem<SinglePage> treeItem = new TreeItem<SinglePage>(newPage);
      treeItem.setGraphic(new ImageView(offIconImg));
      pagesRoot.getChildren().add(treeItem);
    }
    
    if(instruction.equals(UPDATE_TREEVIEW_DELETE)){
      TreeItem<?> treeItem = (TreeItem<?>) statement.getValue();
      pagesRoot.getChildren().remove(treeItem);
      TreeViewOfSetPage.getSelectionModel().select(pagesRoot);
    }
    
    if(instruction.equals(UPDATE_ON_MENU)){
      TreeItem<?> treeItem = (TreeItem<?>) statement.getValue();
      resetGraphic(treeItem, true);
    }
    
    if(instruction.equals(UPDATE_OFF_MENU)){
      TreeItem<?> treeItem = (TreeItem<?>) statement.getValue();
      resetGraphic(treeItem, false);
    }
    
    if(instruction.equals(UPDATE_CATEGORY)){ 
       selectCategory.setValue((Category)statement.getValue());
    }
      
  }
	
	
	/*
	 * @return the pop-up menu interface of right click event.
	 *  */
	public ContextMenu getRightButtonMenu(RIGHTMENU type) {
	  ContextMenu rightButtonMenu = new ContextMenu();
	  
	  switch(type) {
	    case add:
	      MenuItem newItem = new MenuItem("add a new page");
	      rightButtonMenu.getItems().add(newItem);
	      break;
	    case delete:
	      MenuItem deleteItem = new MenuItem("delete this page");
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
	
	public ChoiceBox<Category> getCategoryChoiceBox() {
    return this.selectCategory;
  }
	
	public Button getSaveButton() {
    return this.saveButton;
  }
	
	private void setUpTreeView(final PageCollection pageCollection) {
	  root.getChildren().clear();
	  TreeItem<SinglePage> defaultRoot = new TreeItem<SinglePage>(new SinglePage("Default Page"));
	  pagesRoot = new TreeItem<SinglePage>(new SinglePage("Your Page"));
	  defaultRoot.setExpanded(true);
	  pagesRoot.setExpanded(true);
	  
	  for(SinglePage page : pageCollection.values()) {
	    TreeItem<SinglePage> pageItem = new TreeItem<SinglePage>(page);
	    
	    // Allocate a category
	    if(page.getDirectory().equals("default")) {
	      defaultRoot.getChildren().add(pageItem);
	    }
	    else {
	      pagesRoot.getChildren().add(pageItem);
	    }
	    
	    // Add the image graphic based on menu.
	    if(page.isOnMenu()) {
	      pageItem.setGraphic(new ImageView(showIconImg));
	    }
	    else {
	      pageItem.setGraphic(new ImageView(offIconImg));
	    }
	  }
	  root.getChildren().add(defaultRoot);
	  root.getChildren().add(pagesRoot);
	}
	
	void resetGraphic(TreeItem<?> page, Boolean isOnMenu) {
	  if(isOnMenu) {
	    page.setGraphic(new ImageView(showIconImg));
	  }
	  else {
	    page.setGraphic(new ImageView(offIconImg));
	  }
	}
	
}
