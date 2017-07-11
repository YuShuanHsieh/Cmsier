package model.component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import system.SystemSettings;
import system.data.Data;
import system.data.SetPage;
import system.data.SettingItem;
import system.data.Settings;
import system.data.SimplePage;
import model.utility.PathHelper;

public class Generator {
  
  private String identifiedWord = "";

  public void generateFinalPage(SimplePage simplePage, Data data, Settings settings, int type) {
    int identifiedResult = -1;
    boolean isIdentify = false;
    PathHelper pathHelper = new PathHelper();
    String pagePathURL;
    String cSSLinkURL; // should be changed.
    
    if(type == 1) {
      pagePathURL = settings.getLocalPath() + SystemSettings.publishDirectory + "/";
      cSSLinkURL = "\"" +settings.getPublish() + SystemSettings.sourceDirectory + "/" + settings.getLayout() + ".css\"";
    }
    else {
      pagePathURL = settings.getLocalPath() + SystemSettings.draftDirectory + "/";
      cSSLinkURL = "\"file://" + settings.getLocalPath() + "res/" + settings.getLayout() + ".css\"";
    }
    
    try{
      pagePathURL = pathHelper.getPathFromSimplePage(simplePage, settings, pagePathURL);
      if(!pathHelper.checkPathFromSimplePage(simplePage, type)) {
        throw new IOException("PathHelper.class error.");
      }
      
      File finalPage = new File(pagePathURL);
      File templateFile = new File( SystemSettings.templateDirectory + "/" + settings.getLayout() +".html"); //should be changed
      
      FileReader templateInput = new FileReader(templateFile);
      FileWriter finalPageWriter;
      int templateData = 0;
      
      finalPage.createNewFile();
      finalPageWriter = new FileWriter(finalPage);
      
      while(templateData != -1) {
       
        templateData = templateInput.read();
        
        if(templateData == '{') {
          isIdentify = true;
        }
        
        if(templateData != -1 && !isIdentify){
          finalPageWriter.write(templateData);
        }
        
        identifiedResult = identifyInsertArea(isIdentify, (char)templateData);
        
        switch(identifiedResult) {
         case 1:
           finalPageWriter.write(settings.getTitle());
           break;
         case 2:
           finalPageWriter.write(settings.getSubTitle());
           break;
         case 3:
           String modifiedContent;
           if(type == 1) {
             modifiedContent = replaceImagePath(simplePage.getPageContent(), settings);
           }
           else {
             modifiedContent = simplePage.getPageContent();
           }
           finalPageWriter.write(modifiedContent);
           break;
         case 4:
           String menu = "";
           for(SettingItem menuItem : settings.getMenu()) {
             if(type == 1) {
               menu = menu + "<li class = \"nav-item\"><a class = \"nav-item-link\" href = \"" + settings.getPublish() +  menuItem.getTargetURL();
               menu = menu + "\">" + menuItem.getName() + "</a></li>";
             }
             else {
               menu = menu + "<li class = \"nav-item\"><a class = \"nav-item-link\">" + menuItem.getName() + "</a></li>";
             }
           }
           finalPageWriter.write(menu);
           break;
         case 5:
           finalPageWriter.write(cSSLinkURL);
           break;
         default:
           break;
        }
        
        if(templateData == '}') {
          isIdentify = false;
        }
      }
      
      data.setCurrentPageLocalPath("file://" + pagePathURL);
      
      templateInput.close();
      finalPageWriter.close();
          
    } catch(Exception e){
      e.printStackTrace();
    } 
  }
  
  public void generateAllPage(Settings settings, Data data) {
    List<SetPage> pageList = data.getList();
    List<SetPage> tempData = new LinkedList<SetPage>();
    
    while(!pageList.isEmpty()) {
    
      for(SetPage setPage : pageList) {
      
        if(setPage.getChild() != null) {
          tempData.add(setPage.getChild());
        }
      
        for(SimplePage simplePage : setPage.getPageList()) {
          generateFinalPage( simplePage, data, settings, 1);
        }
      }
      
      pageList = tempData.stream().collect(Collectors.toList());
      tempData.clear();
    }  
  }
  
  private String replaceImagePath(String content, Settings settings) {
    content = content.replace("file://" + settings.getLocalPath() , settings.getPublish());
    return content.replace("￿", "");
  }
  
  private int identifyInsertArea(boolean state ,char single) {
    if(!state) {
      return -1;
    } 
    identifiedWord = identifiedWord + single;
    
    if(identifiedWord.equals("{title}")) {
      identifiedWord = "";
      return 1;
    }
    
    if(identifiedWord.equals("{subTitle}")) {
      identifiedWord = "";
      return 2;
    }
    
    if(identifiedWord.equals("{content}")) {
      identifiedWord = "";
      return 3;
    }
    
    if(identifiedWord.equals("{menu}")) {
      identifiedWord = "";
      return 4;
    }
    
    if(identifiedWord.equals("{css}")) {
      identifiedWord = "";
      return 5;
    }
    
    return -1;
  }
  
}
