package model;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import controller.UploadController;
import model.component.Templatetor;
import model.utility.PathHelper;
import system.Statement;
import system.SystemSettings;
import system.data.SetPage;
import system.data.SettingItem;
import system.data.Settings;
import system.data.SimplePage;
import view.PreviewView;
/*
 * This class is responsible for generating preview or official Web pages.
 * @Author: Yu-Shuan
 *  */
public class GenerateModel extends Model {

  private PathHelper pathHelper;
  
  /* draft - preview pages, official - published pages. */
  public enum GENERATE{draft, official}
  
  public GenerateModel(){
    pathHelper = new PathHelper();
  }
  
  @Override
  public void init(){
    this.isInitialize(); 
  }
  /* 
   * @param SimplePage - a selected page would be render to a complete Web page. 
   * */
  public void generateSinglePage(GENERATE type, SimplePage simplePage){
    Settings settings = dataCenter.getSettings();
    String templatePath;
    String pagePath;
    String cssPath;
    String menu = "";
    String modifiedContent;
    
    switch(type){
      case draft:
        pagePath = settings.getLocalPath() + SystemSettings.draftDirectory + "/";
        cssPath = "\"file://" + settings.getLocalPath() + "res/" + settings.getLayout() + ".css\"";
        for(SettingItem menuItem : settings.getMenu()) {
          menu = menu + "<li class = \"nav-item\"><a class = \"nav-item-link\">" + menuItem.getName() + "</a></li>";
        }
        modifiedContent = simplePage.getPageContent();
      break;
      case official:
        pagePath = settings.getLocalPath() + SystemSettings.publishDirectory + "/";
        cssPath = "\"" +settings.getPublish() + SystemSettings.sourceDirectory + "/" + settings.getLayout() + ".css\"";
        for(SettingItem menuItem : settings.getMenu()) {
          menu = menu + "<li class = \"nav-item\"><a class = \"nav-item-link\" href = \"";
          menu = menu + settings.getPublish() +  menuItem.getTargetURL();
          menu = menu + "\">" + menuItem.getName() + "</a></li>";
        }
        modifiedContent = replaceImagePath(simplePage.getPageContent(), settings);
      break;
      default:
        /* Set default value */
      return;
    }
    
    // get a complete path of page file from SimplePage 
    pagePath = pathHelper.getPathFromSimplePage(simplePage, settings, pagePath);
    templatePath = SystemSettings.templateDirectory + "/" + settings.getLayout() +".html";
    
    try{
      Templatetor page = new Templatetor(templatePath, pagePath);
      page.addKeyAndContent("css", cssPath);
      page.addKeyAndContent("title", settings.getTitle());
      page.addKeyAndContent("subTitle", settings.getSubTitle());
      page.addKeyAndContent("menu", menu);
      page.addKeyAndContent("content", modifiedContent);
      File finalPageFile = page.run();
      dataCenter.getData().setCurrentPageLocalPath("file://" + finalPageFile.getAbsolutePath());
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }
  /* 
   * push a updateStatement request to view before generate all Web pages.
   */
  public boolean generateAllFinalPages() {
    view.updateStatement(UploadController.UPLOAD_PROCESS, Statement.success("- Generate final Web pages."));
    List<SetPage> pageList = dataCenter.getData().getList();
    List<SetPage> tempData = new LinkedList<SetPage>();
    /* delete current final page in web folder. */
    try{
      pathHelper.deleteFilesFromDirectory(dataCenter.getSettings().getLocalPath() + "web/");
    
      while(!pageList.isEmpty()) {
    
        for(SetPage setPage : pageList) {
      
          if(setPage.getChild() != null) {
            tempData.add(setPage.getChild());
          }
          for(SimplePage simplePage : setPage.getPageList()) {
            generateSinglePage(GENERATE.official, simplePage);
          }
        }
        pageList = tempData.stream().collect(Collectors.toList());
        tempData.clear();
      }  
      return true;
    }
    catch(Exception e){
      e.printStackTrace();
      return false;
    }
  }
  
  public void generatePreviewPage(){
    String previewPagePath = dataCenter.getData().getCurrentPageLocalPath();
    view.updateStatement(PreviewView.UPDATE_LOADPAGE, Statement.success(previewPagePath));
  }
  
  
  /*
   *  internal function to remove unknown character.
   *  */
  private static String replaceImagePath(String content, Settings settings) {
    content = content.replace("file://" + settings.getLocalPath() , settings.getPublish());
    return content.replace("￿", "");
  } 
}
