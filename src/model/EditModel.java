package model;

import java.util.Optional;

import model.utility.ImageUploader;
import system.DataCenter;
import system.Statement;
import system.data.SinglePage;
import view.EditView;
import view.View;
import system.data.Category;

/**
 * Edit module: enable user to edit page's contents.
 * It contains functions of page selection, text editor, and save content.
 *  */

public class EditModel implements Model {
  
  private DataCenter dataCenter;
  private View view;
  
  public EditModel(DataCenter dataCenter){
    this.dataCenter = dataCenter;
  }
  
  public void attach(View view) {
    this.view = view;
  }
  
  @Override
  public void init() {
    view.updateStatement(EditView.SETUP_TREEVIEW, Statement.success(dataCenter.getPageCollection()));
  }

  /** Three steps of save contents: save page contents -> save Menu Items -> save categories */
  public void savePageContent(SinglePage page, String content, String customizedName, Category category) {
    String clippedContent = clipTheContent(content); 
    page.setContent(clippedContent);
    dataCenter.updatePage(page);
  
    if(customizedName != "") {
      dataCenter.addItem(customizedName, page);
      page.setIsOnMenu(true);
    }
    else {
      dataCenter.removeItem(page);
      page.setIsOnMenu(false);
    }
    
    if(category != null) {
      addPageToCategory(page, category);
    }
  }
  
  private void addPageToCategory(SinglePage page, Category category) {
    
    /** Should remove the page from the previous category. */
    Category originalCategory = page.getCategory();
    if(originalCategory != null && originalCategory != category) {
      dataCenter.removeCategoryPage(page);
    }
    
    page.setCategory(category);
    dataCenter.addCategoryPage(page, category);
  }
  
  public void deleteExistingPage(SinglePage page) {
    if(!page.toString().endsWith(".html")) {
      return;
    }
    dataCenter.removePage(page);
  }

  public Optional<SinglePage> addNewPage(String fileName) {
    if(!dataCenter.getPageCollection().isPageNameExist(fileName)) {
      SinglePage newPage = new SinglePage(fileName + ".html");
      newPage.setDirectory("page");
      dataCenter.addPage(newPage);
      
      view.updateStatement(EditView.UPDATE_TREEVIEW_ADD, Statement.success(newPage));
      return Optional.of(newPage);
    }
    else {
      return Optional.empty();
    }
  }
  
  private String clipTheContent(String content) {
    content = content.replaceAll("<h2><font[^>]+size=[^>]+[\"]>", "<h2 class = \"content-title-sub\"><font>");
    
    content = clipHTMLContent(content);
    content = imageStore(content);
    return content.replace("￿", "");
  } 
  
  public String clipHTMLContent(String content) {
    String start = "<body contenteditable=\"true\">";
    String end = "</body>";
    int firstIndex = content.lastIndexOf(start);
    int lastIndex = content.lastIndexOf(end);
    
    content = content.substring(firstIndex + start.length(), lastIndex);
    return content.replace("￿", "");
  }
  
  public String imageStore(String content) {
    ImageUploader uploader = new ImageUploader(content, dataCenter.getSettings());
    return uploader.run();
  }
  
}
