package model;
import java.io.File;
import java.io.IOException;
import view.AdminView;
import view.View;
import model.render.Templatetor;
import model.utility.DataHelpers.CSSDataHelper;
import system.DataCenter;
import system.Statement;
import system.SystemSettings;
import system.data.CSSXMLsettings;
import system.data.Category;
import system.data.Settings;

/**
 * Admin module: set up data about Web sites, layout, and categories.
 * The model should only attach AdminView to get the correct result.
 * @see AdminController
 * @see AdminView
 *  */

public class AdminModel implements Model {
  private View view;
  private CSSDataHelper cssDataHelper;
  private CSSXMLsettings cssSettings;
  private DataCenter dataCenter;
  private Settings settings;
  private final String cssPreviewPath;
  
  public AdminModel(DataCenter dataCenter) {
    this.dataCenter = dataCenter;
    settings = dataCenter.getSettings();
    cssPreviewPath = settings.getLocalPath() + SystemSettings.CSSpreviewPath;
    cssDataHelper = new CSSDataHelper(settings.getLocalPath());
  }
  
  @Override
  public void attach(View view) {
    this.view = view;
  }
  
  @Override
  public void init() {
    changeLayout(settings.getLayout());
    view.updateStatement(AdminView.UPDATE_SETTINGS, Statement.success(settings));
  }
  
  public void changeLayout(String layoutName){
    cssSettings = cssDataHelper.read(layoutName);
    String previewPagePath = generatePreviewPage(layoutName);
    view.updateStatement(AdminView.UPDATE_CSSSETTINGS, Statement.success(cssSettings));
    view.updateStatement(AdminView.UPDATE_LOADPAGE, Statement.success(previewPagePath));
  }
  
  public void changeLayoutColor(String fieldName, String color){
    
    if(fieldName.equals(AdminView.CSSCOLOR_HEADER)) {
      cssSettings.setHeaderColor(color);
    }
    else if(fieldName.equals(AdminView.CSSCOLOR_TITLE)) {
      cssSettings.setTitleColor(color); 
    }
    else if(fieldName.equals(AdminView.CSSCOLOR_SUBTITLE)){
      cssSettings.setSubTitleColor(color);
    }
    else if(fieldName.equals(AdminView.CSSCOLOR_MAIN)) {
      cssSettings.setMainColor(color);
    }
    else if(fieldName.equals(AdminView.CSSCOLOR_CONTENT)) {
      cssSettings.setContentColor(color);
    }
    else if(fieldName.equals(AdminView.CSSCOLOR_FRAME)) {
      cssSettings.setFrameColor(color);
    }
    
    String targetCssFile = cssPreviewPath + cssSettings.getName() + ".css";
    generateCssFile(targetCssFile);
    view.updateStatement(AdminView.UPDATE_RELOADPAGE, Statement.success(null));
  }
  
  /** Invoked by the submit button. write the setting object to files.*/
  public boolean modifySettingsField(String title, String subTitle, String localPath, String serverPath, String layout, String footer) {
    if(title.trim().isEmpty()) {
      return false;
    }
    else if(localPath.trim().isEmpty()) { 
      return false;
    }
    else if(serverPath.trim().isEmpty()) {
      return false;
    }
    else {
      settings.setTitle(title);
      settings.setSubTitle(subTitle);
      settings.setLocalPath(localPath);
      settings.setPublish(serverPath);
      settings.setLayout(layout); 
      settings.setFooter(footer);
    }
    dataCenter.updateSetting();
    return true;
  }
  
  /** Invoked by the submit button. write the CSS object to files.*/
  public void modifyCssSetting(){
    cssDataHelper.write(cssSettings);
    String tempPath = settings.getLocalPath() + SystemSettings.cssPath + cssSettings.getName() + ".css";
    generateCssFile(tempPath);
  }
    
  private String generatePreviewPage(String layoutName){
    String pageTemplatePath = SystemSettings.templatePath + layoutName + ".html";
    String previewPagePath = cssPreviewPath + layoutName + ".html";
    String cssFilePath = cssPreviewPath + layoutName + ".css";
    
    try{
      File cssFile = generateCssFile(cssFilePath);
      Templatetor page = new Templatetor(pageTemplatePath, previewPagePath);
      page.addKeyAndContent("css", "file://" + cssFile.getAbsolutePath());
      page.addKeyAndContent("title", "layout title example");
      page.addKeyAndContent("subTitle", "layout subtitle example");
      page.addKeyAndContent("menu", "<li class = \"nav-item\"><a>menu</a></li>");
      page.addKeyAndContent("articleTitle", "Article title");
      page.addKeyAndContent("content", "this is layout page preview.");
      page.addKeyAndContent("footer", "example@copy right.");
      File previewPageFile = page.run();
      return previewPageFile.getAbsolutePath();
    }
    catch(IOException e){
      e.printStackTrace();
      return null;
    }
  }
  
  private File generateCssFile(String targetPath){
    String templatePath = SystemSettings.CSStemplatePath + cssSettings.getName() + ".css";
    File cssFile;
    try{
      Templatetor template = new Templatetor(templatePath, targetPath);
      template.setMarkNotation('<', '>');
      template.addKeyAndContent("bk", cssSettings.getHeaderColor());
      template.addKeyAndContent("title", cssSettings.getTitleColor());
      template.addKeyAndContent("subtitle", cssSettings.getSubTitleColor());
      template.addKeyAndContent("main", cssSettings.getMainColor());
      template.addKeyAndContent("content", cssSettings.getContentColor());
      template.addKeyAndContent("frame", cssSettings.getFrameColor());
      cssFile = template.run();
      
      return cssFile;
    } 
    catch(IOException e){
      e.printStackTrace();
    }
    return null;
  }
  
  public void addNewCategory(String categoryName) {
    Category newCategory = new Category();
    newCategory.setName(categoryName);
    dataCenter.addNewCategory(newCategory);
  }
 
  public void editCategoryName(String newName, String originalCategoryName) {
    Category originalCategory = dataCenter.getCategory().get(originalCategoryName);
    dataCenter.removeCategory(originalCategory);
    originalCategory.setName(newName);
    dataCenter.addNewCategory(originalCategory);
  }
  
}
