package model;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import model.render.Templatetor;
import system.Statement;
import system.SystemSettings;
import system.data.SinglePage;
import system.data.SettingItem;
import system.data.Settings;
import view.PreviewView;
import view.UploadView;
import org.apache.commons.io.FileUtils;
/*
 * This class is responsible for generating preview or official Web pages.
 * @Author: Yu-Shuan
 *  */
public class GenerateModel extends Model {

  private String pageDirectory = "";
  private Settings settings;
  
  /* draft - preview pages, official - published pages. */
  public enum GENERATE{draft, official}
  
  public GenerateModel(){
  }
  
  @Override
  public void init(){
    settings = dataCenter.getSettings();
    pageDirectory = settings.getLocalPath() + SystemSettings.D_web + "/";
  }
 
  public void generateSinglePage(GENERATE type, SinglePage page){
    String templatePath;
    String pagePath;
    String cssPath;
    String menu = "";
    String modifiedContent;
    
    switch(type){
      case draft:
        cssPath = "\"file://" + settings.getLocalPath() + SystemSettings.D_css + "/" + settings.getLayout() + ".css\"";
        for(SettingItem menuItem : settings.getMenu()) {
          menu += "<li class = \"nav-item\"><a class = \"nav-item-link\">" + menuItem.getName() + "</a></li>";
        }
        modifiedContent = page.getContent();
      break;
      case official:
        cssPath = "\"" +settings.getPublish() + SystemSettings.D_css + "/" + settings.getLayout() + ".css\"";
        for(SettingItem menuItem : settings.getMenu()) {
          menu += "<li class = \"nav-item\"><a class = \"nav-item-link\" href = \"";
          menu += settings.getPublish() +  menuItem.getTargetURL();
          menu += "\">" + menuItem.getName() + "</a></li>";
        }
        modifiedContent = replaceImagePath(page.getContent(), settings);
      break;
      default:
        /* Set default value */
      return;
    }
    modifiedContent = inserTitleToContent(page.getTitle(), modifiedContent);
    // get a complete path of page file from SimplePage 
    verifyDirectory(pageDirectory + page.getDirectory() + "/");
    
    pagePath = pageDirectory + page.getDirectory() + "/" + page.getName();
    templatePath = SystemSettings.D_template + "/" + settings.getLayout() +".html";
    
    try{
      Templatetor templatetor = new Templatetor(templatePath, pagePath);
      templatetor.addKeyAndContent("css", cssPath);
      templatetor.addKeyAndContent("title", settings.getTitle());
      templatetor.addKeyAndContent("subTitle", settings.getSubTitle());
      templatetor.addKeyAndContent("menu", menu);
      templatetor.addKeyAndContent("content", modifiedContent);
      templatetor.addKeyAndContent("footer", settings.getFooter());
      File finalPageFile = templatetor.run();
      dataCenter.setlocalPreviewPagePath("file://" + finalPageFile.getAbsolutePath());
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }
  
  public String inserTitleToContent(String title, String content) {
    content = "<h1 class=\"content-title\">" + title + "</h1>" + content;
    return content;
  }
  
  /* 
   * push a updateStatement request to view before generate all Web pages.
   */
  public boolean generateAllFinalPages() {
    File pageDirctory = new File(pageDirectory);
    view.updateStatement(UploadView.UPLOAD_PROCESS, Statement.success("- Generate final Web pages."));
    Collection<SinglePage> pages = dataCenter.getData().values();
    try {
      FileUtils.deleteDirectory(pageDirctory);
      for(SinglePage page : pages) {
        generateSinglePage(GENERATE.official,page);
      }
      return true;
    }
    catch(Exception exception) {
      exception.printStackTrace();
      return false;
    }
  }
  
  private void verifyDirectory(String directoryPath) {
    File directory = new File(directoryPath);
    if(!directory.exists()) {
      directory.mkdirs();
    }
  }
  
  public void generatePreviewPage(){
    String previewPagePath = dataCenter.getlocalPreviewPagePath();
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
