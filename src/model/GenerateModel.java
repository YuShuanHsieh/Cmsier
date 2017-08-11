package model;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import model.render.Templatetor;
import system.Statement;
import system.SystemSettings;
import system.data.SinglePage;
import system.data.Category;
import system.data.CategoryPage;
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
        cssPath = "file://" + settings.getLocalPath() + SystemSettings.D_css + "/" + settings.getLayout() + ".css";
        for(SettingItem menuItem : settings.getMenu()) {
          menu += "<li><a class = \"nav-item\">" + menuItem.getName() + "</a></li>";
        }
        menu += getCategoryMenu(dataCenter.getCategory().values());
        modifiedContent = page.getContent();
      break;
      case official:
        cssPath = settings.getPublish() + SystemSettings.D_css + "/" + settings.getLayout() + ".css";
        for(SettingItem menuItem : settings.getMenu()) {
          menu += "<li ><a class = \"nav-item\" href = \"";
          menu += settings.getPublish() +  menuItem.getTargetURL();
          menu += "\">" + menuItem.getName() + "</a></li>";
        }
        menu += getCategoryMenu(dataCenter.getCategory().values());
        modifiedContent = replaceImagePath(page.getContent(), settings);
      break;
      default:
        /* Set default value */
      return;
    }
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
      templatetor.addKeyAndContent("articleTitle", page.getTitle());
      templatetor.addKeyAndContent("content", modifiedContent);
      templatetor.addKeyAndContent("footer", settings.getFooter());
      File finalPageFile = templatetor.run();
      dataCenter.setlocalPreviewPagePath("file://" + finalPageFile.getAbsolutePath());
    }
    catch(IOException e){
      e.printStackTrace();
    }
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
      
      for(Category category : dataCenter.getCategory().values()) {
        generateSingleCategoryPage(GENERATE.official,category);
      }      
      return true;
    }
    catch(Exception exception) {
      exception.printStackTrace();
      return false;
    }
  }
  
  public String getCategoryMenu(Collection<Category> categories) {
    String publishURL = dataCenter.getSettings().getPublish();
    String categoryMenu = "<li class=\"dropdown\">" +
        "<a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\" role=\"button\" aria-haspopup=\"true\" aria-expanded=\"false\">Categories<span class=\"caret\"></span></a>" +
        "<ul class=\"dropdown-menu\">";
    
    for(Category category: categories) {
      if(!category.getPageList().isEmpty()) {
        categoryMenu += "<li><a href=\"" + publishURL + "list/" + category.getName() + ".html\">" + category.getName() + "</a></li>";
      }
    }
    return categoryMenu += "</ul></li>";
  }
  
  public String getCategoryContent(Category category) {
    String content = "<ul>";
    for(CategoryPage categoryPage : category.getPageList()) {
      if(!categoryPage.getDirectory().equals("default")) {
        content += "<li><a href=\"" + "../" + categoryPage.getDirectory()+"/"+ categoryPage.getFileName() + "\">" + categoryPage.getTitle() +"</a></li>";
      }
    }
    return content + "</ul>";
  }
  
  public void generateSingleCategoryPage(GENERATE type, Category category){
    String templatePath;
    String pagePath;
    String cssPath;
    String menu = "";
    String modifiedContent = getCategoryContent(category);
    
    switch(type){
      case draft:
        cssPath = "file://" + settings.getLocalPath() + SystemSettings.D_css + "/" + settings.getLayout() + ".css";
        for(SettingItem menuItem : settings.getMenu()) {
          menu += "<li><a class = \"nav-item\">" + menuItem.getName() + "</a></li>";
        }
      break;
      case official:
        cssPath = settings.getPublish() + SystemSettings.D_css + "/" + settings.getLayout() + ".css";
        for(SettingItem menuItem : settings.getMenu()) {
          menu += "<li ><a class = \"nav-item\" href = \"";
          menu += settings.getPublish() +  menuItem.getTargetURL();
          menu += "\">" + menuItem.getName() + "</a></li>";
        }
      break;
      default:
        /* Set default value */
      return;
    } 
    menu += getCategoryMenu(dataCenter.getCategory().values());
    verifyDirectory(pageDirectory + "list/");
    
    pagePath = pageDirectory + "list/" + category.getName()+".html";
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
    }
    catch(IOException e){
      e.printStackTrace();
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
