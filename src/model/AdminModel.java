package model;

import java.io.File;
import java.io.IOException;
import view.AdminView;
import model.component.Templatetor;
import model.utility.XmlHelper;
import system.Statement;
import system.SystemSettings;
import system.data.CSSXMLsettings;
import system.data.Settings;

public class AdminModel extends Model {

  private XmlHelper xmlHelper;
  private CSSXMLsettings cssSettings;
  private Settings settings;
  
  @Override
  public void init() {
    xmlHelper = new XmlHelper();
    settings = xmlHelper.retrieveSettingFromXML();
    dataCenter.setSettings(settings);
    view.updateStatement(AdminView.UPDATE_SETTINGS, Statement.success(settings));
    
    generateCssSetting();
    generatePreviewPage();
  }
  
  private void generateCssSetting(){
    cssSettings = getCssSettings();
    dataCenter.setCSSSettings(cssSettings);
    view.updateStatement(AdminView.UPDATE_CSSSETTINGS, Statement.success(cssSettings));
  }
  
  public void changeLayout(){
    generateCssSetting();
    generatePreviewPage();
  }
  
  public void changeLayoutColor(){
    String layoutName = settings.getLayout();
    String tempPath = "layout/preview/" + layoutName + ".css";
    generateCssFile(tempPath);
    view.updateStatement(AdminView.UPDATE_RELOADPAGE, Statement.success(null));
  }
  
  public boolean modifySettingsField(String title, String subTitle, String localPath, String serverPath, String layout) {
    if(title.trim().isEmpty()) {
      return false;
    }
    else if(subTitle.trim().isEmpty()) {
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
    }
    XmlHelper xmlHelper = new XmlHelper();
    xmlHelper.writeSettingToXML(settings);
    return true;
  }
  
  public void modifyCssSetting(){
    xmlHelper.writeSettingToXML(cssSettings);
    String tempPath = settings.getLocalPath() + "res/" + cssSettings.getName() + ".css";
    generateCssFile(tempPath);
  }
    
  public void generatePreviewPage(){
    String layoutName = settings.getLayout();
    String tempPath = "layout/preview/" + layoutName + ".css";
    File cssFile = generateCssFile(tempPath);
    try{
      
      String pageTemplatePath = "template/" + layoutName + ".html";
      String pageTempPath = "layout/preview/" + layoutName + ".html";
    
      Templatetor page = new Templatetor(pageTemplatePath, pageTempPath);
      page.addKeyAndContent("css", "\"file://" + cssFile.getAbsolutePath() + "\"");
      page.addKeyAndContent("title", "layout title example");
      page.addKeyAndContent("subTitle", "layout subtitle example");
      page.addKeyAndContent("menu", "<li class = \"nav-item\"><a class = \"nav-item-link\">menu</a></li>");
      page.addKeyAndContent("content", "this is layout page preview.");
      File previewPageFile = page.run();
      view.updateStatement(AdminView.UPDATE_LOADPAGE, Statement.success(previewPageFile.getAbsolutePath()));
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }
  
  private File generateCssFile(String targetPath){
    String templatePath = "layout/CssTemplate/" + settings.getLayout() + ".css";
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
  
  private CSSXMLsettings getCssSettings(){
    CSSXMLsettings cssSetting = null;
    String layoutName = settings.getLayout();
    File file = new File("layout/CssXML/" + layoutName + ".xml"); 
    try{
      if(!file.exists()){
        SystemSettings systemSettings = new SystemSettings();
        systemSettings.initDefaultLayout();
        cssSetting = systemSettings.getDefaultLayout(layoutName);
      }
      else{
        XmlHelper xmlHelper = new XmlHelper();
        cssSetting = xmlHelper.retrieveCSSSettingFromXML(layoutName);
      }
    }
    catch(Exception e){
      e.printStackTrace();
    }
    
    return cssSetting;
  }
  
}
