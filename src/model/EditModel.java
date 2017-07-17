package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import model.utility.PathHelper;
import model.utility.XmlHelper;
import model.utility.DataHelper;
import system.Statement;
import system.SystemSettings;
import system.data.Data;
import system.data.Page;
import system.data.SetPage;
import system.data.SettingItem;
import system.data.Settings;
import system.data.SimplePage;
import view.EditView;

public class EditModel extends Model {
  
  private DataHelper dataHelper;
  private XmlHelper xmlHelper;
  private PathHelper pathHelper;
  private Settings settings;
  private Data data;
  
  public EditModel(){
    dataHelper = new DataHelper();
    dataHelper = new DataHelper();
    xmlHelper = new XmlHelper();
    pathHelper = new PathHelper();
  }
  
  @Override
  public void init() {
    
    settings = xmlHelper.retrieveSettingFromXML();
    
    /* 
     * Initialize the local path when user firstly start this application.
     * */
    File rootDirectory = new File(settings.getLocalPath());
    if(!xmlHelper.isLocalPathExistingInXML() || !rootDirectory.exists()) {
      String defaultDirectory = pathHelper.createDefaultDirectoy();
      settings.setLocalPath(defaultDirectory);
      xmlHelper.writeSettingToXML(settings);
    }
    
    data = dataHelper.retrieveDataFromFiles(settings.getLocalPath());
    dataCenter.setData(data);
    dataCenter.setSettings(settings);
    
    view.updateStatement(EditView.UPDATE_LIST, Statement.success(data));
    view.updateStatement(EditView.UPDATE_MENUITEM, Statement.success(settings));
  }

  public boolean savePageContent(Object treeItem, String content, String customizedName) {
    settings = dataCenter.getSettings();   
    //Generator generator = new Generator();
    String pathUrl = settings.getLocalPath() + SystemSettings.D_edit + "/";
    
    Page page = (Page)treeItem;
    if(page.getName().endsWith("html")){
      try {
        String clippedContent = clipTheContent(content);
        ((SimplePage)page).setPageContent(clippedContent);
        pathUrl = pathHelper.getPathFromSimplePage((SimplePage)page, settings, pathUrl);
     
        FileWriter writer = new FileWriter(pathUrl);
        writer.write(clippedContent);
        writer.close();
  
        if(((SimplePage)page).getChangeState()) {
          if(customizedName != "") {
            addMenuItemToSettingXML(customizedName, (SimplePage)page);
          }
          else {
            removeMenuItemToSettingXML((SimplePage)page);
          }
        }
        
        //generator.generateFinalPage( (SimplePage)page, data, settings, 0);
        view.updateStatement(EditView.UPDATE_MENUITEM, Statement.success(settings));

      }catch(Exception e) {
        e.printStackTrace();
        return false;
      }      
    }
    
    return true;
  }
  
  public void deleteExistingPage(Page page) {
    if(!page.toString().endsWith(".html")) {
      return;
    }
    SimplePage targetSimplePage = (SimplePage)page;
    
    List<SetPage> pageList = data.getList();
    List<SetPage> tempData = new LinkedList<SetPage>();
    
    deleteExistingPageFile(targetSimplePage);
    removeMenuItemToSettingXML(targetSimplePage);
    
    while(!pageList.isEmpty()) {
      
      for(SetPage setPage : pageList) {
      
        if(setPage.getChild() != null) {
          tempData.add(setPage.getChild());
        }
        
        for(SimplePage simplePage : setPage.getPageList()) {
          if(simplePage == page){
            setPage.getPageList().remove(simplePage);
            view.updateStatement(EditView.UPDATE_LIST, Statement.success(data));
            return;
          }
        }
      }
      pageList = tempData.stream().collect(Collectors.toList());
      tempData.clear();
    }
  }
  
  private void deleteExistingPageFile(SimplePage page) {
    String localFullPath = dataCenter.getSettings().getLocalPath() + SystemSettings.D_edit + "/";
    String filePath = pathHelper.getPathFromSimplePage(page, settings, localFullPath);
    File file = new File(filePath);
    if(file.exists()){
      file.delete();
    }
  }
  
  public void addNewSimplePage(SetPage setPage, String fileName) {
    String fullFileName = fileName.trim() + ".html";
    String defaultEditPath = dataCenter.getSettings().getLocalPath() + SystemSettings.D_edit +"/";
    Deque<SetPage> stack = new LinkedList<SetPage>();
    
    if(data.isExistingPage(2, fullFileName)) {
      return;
    }
    // Add new SimplePage to current Data;
    SimplePage newSimplePage = new SimplePage(fullFileName);
    setPage.AddPage(newSimplePage);
    
    while(setPage != null) {
      stack.push(setPage);
      setPage = setPage.getParent();
    }
    
    while(!stack.isEmpty()) {
      defaultEditPath = defaultEditPath + stack.pop().getName() + "/";
    }
   
    try{
      File newFile = new File(defaultEditPath + fullFileName);
      newFile.createNewFile(); 
      view.updateStatement(EditView.UPDATE_LIST, Statement.success(data));
    } 
    catch(IOException e) {
      e.printStackTrace();
    }
  }
  
  private void addMenuItemToSettingXML(String itemName, SimplePage simplePage) {
    String publishedUrl = "./";
    SettingItem newMenuItem = new SettingItem();
    publishedUrl = pathHelper.getPathFromSimplePage(simplePage, settings, publishedUrl);
    newMenuItem.setName(itemName);
    newMenuItem.setTargetURL(publishedUrl);
    settings.addItemToMenu(newMenuItem);
    xmlHelper.writeSettingToXML(settings);
  }
  
  private void removeMenuItemToSettingXML(SimplePage simplePage) {
    Optional<SettingItem> removeSettingItem = dataHelper.searchSettingMenuItemBySimplePage(settings, simplePage);
    
    if(removeSettingItem.isPresent()) {
      settings.getMenu().remove(removeSettingItem.get());
      xmlHelper.writeSettingToXML(settings);
    }
  }
  
  private String clipTheContent(String content) {
    content = content.replaceAll("<h1><font[^>]+size=[^>]+[\"]>", "<h1 class = \"content-title\"><font>");
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
    
    String clip = "<img src=\"file://";
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
        String imgUploadPath = settings.getLocalPath() + SystemSettings.D_upload + "/";
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
