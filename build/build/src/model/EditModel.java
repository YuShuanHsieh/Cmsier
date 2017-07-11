package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import controller.Controller;
import data.Data;
import data.Page;
import data.SetPage;
import data.SettingItem;
import data.Settings;
import data.SimplePage;
import model.generator.Generator;
import model.utility.PathHelper;
import model.utility.XmlHelper;
import model.utility.DataHelper;
import model.generator.Upload;
import system.SystemSettings;

public class EditModel implements Model {
  
  private Controller controller;

  @Override
  public void init() {
    DataHelper dataHelper = new DataHelper();
    XmlHelper xmlHelper = new XmlHelper();
    PathHelper pathHelper = new PathHelper();
    
    Settings settings = xmlHelper.retrieveSettingFromXML();
    if(!xmlHelper.isLocalPathExistingInXML()) {
      String defaultDirectory = pathHelper.createDefaultDirectoyInLocalPath();
      settings.setLocalPath(defaultDirectory);
      xmlHelper.writeSettingToXML(settings);
    }
    
    Data data = dataHelper.retrieveDataFromFiles(settings.getLocalPath());
    controller.getSystemManager().setData(data);
    
    controller.getSystemManager().setSettings(settings);
  }

  @Override
  public void setController(Controller controller) {
    this.controller = controller;
  }
  
  public boolean savePageContent(Object treeItem, String content, String customizedName) {
    Generator generator = new Generator();
    Settings settings = controller.getSystemManager().getSettings();
    Data data = controller.getSystemManager().getData();
    PathHelper pathHelper = new PathHelper();
    String pathUrl = settings.getLocalPath() + SystemSettings.editDirectory + "/";
    
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
        generator.generateFinalPage( (SimplePage)page, data, settings, 0);
        controller.getSystemManager().notifyAllController();

      }catch(Exception e) {
        e.printStackTrace();
        return false;
      }      
    }
    
    return true;
  }
  
  public void generateAllPage() {
    Settings website = controller.getSystemManager().getSettings();
    Data data = controller.getSystemManager().getData();
    Generator generator = new Generator();
    List<SetPage> pageList = controller.getSystemManager().getData().getList();
    List<SetPage> tempData = new LinkedList<SetPage>();
    Upload upload = new Upload();
    
    /* Delete current final page */
    upload.deleteCurrentFinalPage();
    
    
    while(!pageList.isEmpty()) {
    
      for(SetPage setPage : pageList) {
      
        if(setPage.getChild() != null) {
          tempData.add(setPage.getChild());
        }
      
        for(SimplePage simplePage : setPage.getPageList()) {
          generator.generateFinalPage( simplePage, data, website, 1);
        }
      }
      
      pageList = tempData.stream().collect(Collectors.toList());
      tempData.clear();
    }
    
  }
  
  public void deleteExistingPage(Page page) {
    if(!page.toString().endsWith(".html")) {
      return;
    }
    List<SetPage> pageList = controller.getSystemManager().getData().getList();
    List<SetPage> tempData = new LinkedList<SetPage>();
    
    while(!pageList.isEmpty()) {
      
      for(SetPage setPage : pageList) {
      
        if(setPage.getChild() != null) {
          tempData.add(setPage.getChild());
        }
        
        for(SimplePage simplePage : setPage.getPageList()) {
          if(simplePage == page){
            setPage.getPageList().remove(simplePage);
            deleteExistingPageFile(simplePage.getName());
            removeMenuItemToSettingXML(simplePage);
            controller.getSystemManager().notifyAllController();
            return;
          }
        }
      }
      pageList = tempData.stream().collect(Collectors.toList());
      tempData.clear();
    }
  }
  
  private void deleteExistingPageFile(String pageName) {
    String localFullPath = controller.getSystemManager().getSettings().getLocalPath();
    File file = new File(localFullPath + SystemSettings.editDirectory + "/");
    List<File> fileList = Arrays.asList(file.listFiles());
    List<File> tempFile = new LinkedList<File>();
    
    while(!fileList.isEmpty()) {
      for(File eachFile : fileList){
      
        if(eachFile.isDirectory() && eachFile.getName().matches("[a-zA-Z0-9\\-]*")){
          tempFile.addAll(Arrays.asList(eachFile.listFiles()));
        }
      
        if(eachFile.getName().equals(pageName)) {
          eachFile.delete();
          return;
        } 
      }
      fileList = tempFile.stream().collect(Collectors.toList());
      tempFile.clear();
    }
  }
  
  public void addNewSimplePage(SetPage setPage, String fileName) {
    Data data = controller.getSystemManager().getData();
    String fullFileName = fileName.trim() + ".html";
    String defaultEditPath = controller.getSystemManager().getSettings().getLocalPath() + SystemSettings.editDirectory +"/";
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
      controller.getSystemManager().notifyAllController();
    } 
    catch(IOException e) {
      e.printStackTrace();
    }
  }
  
  public void uploadFinalPage(){
    Upload upload = new Upload();
    upload.connect();
    upload.uploadFinalPageFile();
    upload.disconnect();
  }
  
  private void addMenuItemToSettingXML(String itemName, SimplePage simplePage) {
    PathHelper pathHelper = new PathHelper();
    String publishedUrl = "./";
    XmlHelper xmlHelper = new XmlHelper();
    SettingItem newMenuItem = new SettingItem();
    publishedUrl = pathHelper.getPathFromSimplePage(simplePage, controller.getSystemManager().getSettings(), publishedUrl);
    newMenuItem.setName(itemName);
    newMenuItem.setTargetURL(publishedUrl);
    controller.getSystemManager().getSettings().addItemToMenu(newMenuItem);
    xmlHelper.writeSettingToXML(controller.getSystemManager().getSettings());
  }
  
  private void removeMenuItemToSettingXML(SimplePage simplePage) {
    XmlHelper xmlHelper = new XmlHelper();
    DataHelper datahelper = new DataHelper();
    Settings currentSettings = controller.getSystemManager().getSettings();
    
    Optional<SettingItem> removeSettingItem = datahelper.searchSettingMenuItemBySimplePage(currentSettings, simplePage);
    
    if(removeSettingItem.isPresent()) {
      controller.getSystemManager().getSettings().getMenu().remove(removeSettingItem.get());
      xmlHelper.writeSettingToXML(controller.getSystemManager().getSettings());
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
        String imgUploadPath = controller.getSystemManager().getSettings().getLocalPath() + SystemSettings.imgDirectory + "/";
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
