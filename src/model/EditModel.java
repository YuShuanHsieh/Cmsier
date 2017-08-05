package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import model.utility.XmlHelper;
import model.utility.DataHelper;
import system.Statement;
import system.SystemSettings;
import system.data.PageCollection;
import system.data.Settings;
import system.data.SinglePage;
import view.EditView;
import system.data.Category;

public class EditModel extends Model {
  
  private XmlHelper xmlHelper;
  
  public EditModel(){
    xmlHelper = new XmlHelper();
  }
  
  @Override
  public void init() {
    Settings settings = xmlHelper.retrieveSettings();
    dataCenter.setSettings(settings);
    
    Map<String, Category> categories = XmlHelper.retrieveCatetoryFromXML(settings.getLocalPath());
    dataCenter.setCategory(categories);
    
    PageCollection pageCollection = DataHelper.retrievePage(settings.getLocalPath());
    dataCenter.setData(pageCollection);
    dataCenter.organize();
    
    view.updateStatement(EditView.SETUP_TREEVIEW, Statement.success(pageCollection));
  }

  public boolean savePageContent(SinglePage page, String content, String customizedName, Category category) {
    
    String pathUrl = dataCenter.getSettings().getLocalPath() + SystemSettings.D_edit + "/";

    try {
      String clippedContent = clipTheContent(content); 
      page.setContent(clippedContent);
        
      pathUrl += page.getDirectory() + "/" + page.getName();
     
      FileWriter writer = new FileWriter(pathUrl);
      writer.write(clippedContent);
      writer.close();
  
      if(customizedName != "") {
        dataCenter.getSettings().addItemToMenu(customizedName, page);
        page.setIsOnMenu(true);
      }
      else {
        dataCenter.getSettings().removeItemFromMenu(page);
        page.setIsOnMenu(false);
      }
       
      addPageToCategory(page, category);

      }catch(Exception e) {
        e.printStackTrace();
        return false;
      }      
    
    
    return true;
  }
  
  private void addPageToCategory(SinglePage page, Category Category) {
    /** Remove the original category before add. */
    if(page.getCategory() != null && !page.getCategory().equals(Category)) {
      dataCenter.removePageFromCategory(page.getName());
    }
    page.setCategory(Category);
    Category.addPageToList(page);
    XmlHelper.writeCategoryToXML(Category, dataCenter.getSettings().getLocalPath()+ "category/");
  }
  
  public void deleteExistingPage(SinglePage page) {
    if(!page.toString().endsWith(".html")) {
      return;
    }
    dataCenter.removePageFromCategory(page.getName());
    dataCenter.getData().removePage(page);
    dataCenter.getSettings().removeItemFromMenu(page);
    DataHelper.deletePageFromFile(dataCenter.getSettings().getLocalPath(), page);
  }

  public Optional<SinglePage> addNewPage(String fileName) {
    if(!dataCenter.getData().isPageNameExist(fileName)) {
      SinglePage newPage = new SinglePage(fileName + ".html");
      newPage.setDirectory("page");
      dataCenter.getData().addNewPage(newPage);
      
      String mewPageFilePath = dataCenter.getSettings().getLocalPath() + "edit/page/" + fileName + ".html";
      File newPageFile = new File(mewPageFilePath);
      
      try {
        if(!newPageFile.exists()) {
          newPageFile.createNewFile();
        }
      }
      catch(IOException exception) {
        exception.printStackTrace();
      }
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
    
    String clip = "<img style=\"max-width:100%;\" src=\"file://";
    StringBuilder stringBuilder = new StringBuilder(content);
    int startIndex = 0;
    
    while(startIndex != -1){
      
      startIndex = stringBuilder.indexOf("<img", startIndex);
      if(startIndex != -1){
        int lastIndex = stringBuilder.indexOf("\">", startIndex + 1);
        stringBuilder.substring(startIndex, lastIndex);
        String filePah = stringBuilder.substring(startIndex + clip.length(), lastIndex).toString();
      
        String[] splitFile = filePah.split("/");
        String fileName = splitFile[splitFile.length -1];
        
        File originalFile = new File(filePah);
        String imgUploadPath = dataCenter.getSettings().getLocalPath() + SystemSettings.D_upload + "/";
        File newFile = new File(imgUploadPath + fileName);
        
        try{
          if(!newFile.exists()){
            Files.copy(originalFile.toPath(), newFile.toPath());
          }
        } catch(Exception e) {
          e.printStackTrace();
          return null;
        }
        stringBuilder = stringBuilder.replace(startIndex + clip.length(), lastIndex, newFile.getAbsolutePath());
        /* find the last index again because of inserting string. */
        lastIndex = stringBuilder.indexOf("\">", startIndex + 1);
        startIndex = lastIndex;
      }
    }
    
    return stringBuilder.toString();
  }
  
}
