package model;

import java.io.File;
import java.io.IOException;
import controller.Controller;
import controller.AdminController;
import model.component.Templatetor;
import model.utility.XmlHelper;
import system.SystemSettings;
import system.data.CSSXMLsettings;
import system.data.Settings;

public class AdminModel implements Model {

  Controller controller;
  XmlHelper xmlHelper;
  
  @Override
  public void init() {
    xmlHelper = new XmlHelper();
    Settings settings = xmlHelper.retrieveSettingFromXML();
    controller.getSystemManager().setSettings(settings);
    generatePreviewPage(settings.getLayout());
  }
  
  public void generatePreviewPage(String layoutName){
    // ***The path should be modified.
    File file = new File("layout/CssXML/" + layoutName + ".xml"); 
    
    try{
      CSSXMLsettings cssSetting;
      if(!file.exists()){
        SystemSettings systemSettings = new SystemSettings();
        systemSettings.initDefaultLayout();
        cssSetting = systemSettings.getDefaultLayout(layoutName);
      }
      else{
        XmlHelper xmlHelper = new XmlHelper();
        cssSetting = xmlHelper.retrieveCSSSettingFromXML(layoutName);
      }
      controller.getSystemManager().setCSSSettings(cssSetting);
      String previewPagePath = generateTempCssFile();
      ((AdminController)controller).reloadPreviewPage(previewPagePath);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  @Override
  public void setController(Controller controller) {
    this.controller = controller;
  }
  
  public boolean modifySettingsField(String title, String subTitle, String localPath, String serverPath, String layout) {
    Settings currentSettings = controller.getSystemManager().getSettings();
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
      currentSettings.setTitle(title);
      currentSettings.setSubTitle(subTitle);
      currentSettings.setLocalPath(localPath);
      currentSettings.setPublish(serverPath);
      currentSettings.setLayout(layout);  
    }
    XmlHelper xmlHelper = new XmlHelper();
    xmlHelper.writeSettingToXML(currentSettings);
    return true;
  }
  
  public void modifyCssSetting(CSSXMLsettings cssSetting){
    Settings currentSettings = controller.getSystemManager().getSettings();
    XmlHelper xmlHelper = new XmlHelper();
    xmlHelper.writeSettingToXML(cssSetting);
    
    String templatePath = "layout/CssTemplate/" + cssSetting.getName() + ".css";
    String tempPath = currentSettings.getLocalPath() + "res/" + cssSetting.getName() + ".css";
    
    try{
      Templatetor template = new Templatetor(templatePath, tempPath);
      template.setMarkNotation('<', '>');
      template.addKeyAndContent("bk", cssSetting.getHeaderColor());
      template.addKeyAndContent("title", cssSetting.getTitleColor());
      template.addKeyAndContent("subtitle", cssSetting.getSubTitleColor());
      template.addKeyAndContent("main", cssSetting.getMainColor());
      template.addKeyAndContent("content", cssSetting.getContentColor());
      template.addKeyAndContent("frame", cssSetting.getFrameColor());
      template.run();
    } 
    catch(IOException e){
      e.printStackTrace();
    }
  }
  
  public String generateTempCssFile() throws IOException{
    CSSXMLsettings cssSetting = controller.getSystemManager().getCSSSettings();
    String templatePath = "layout/CssTemplate/" + cssSetting.getName() + ".css";
    String tempPath = "layout/preview/" + cssSetting.getName() + ".css";
    
    Templatetor template = new Templatetor(templatePath, tempPath);
    template.setMarkNotation('<', '>');
    template.addKeyAndContent("bk", cssSetting.getHeaderColor());
    template.addKeyAndContent("title", cssSetting.getTitleColor());
    template.addKeyAndContent("subtitle", cssSetting.getSubTitleColor());
    template.addKeyAndContent("main", cssSetting.getMainColor());
    template.addKeyAndContent("content", cssSetting.getContentColor());
    template.addKeyAndContent("frame", cssSetting.getFrameColor());
    File tempFile = template.run();
    
    String pageTemplatePath = "template/" + cssSetting.getName() + ".html";
    String pageTempPath = "layout/preview/" + cssSetting.getName() + ".html";
    
    Templatetor page = new Templatetor(pageTemplatePath, pageTempPath);
    page.addKeyAndContent("css", "\"file://" + tempFile.getAbsolutePath() + "\"");
    page.addKeyAndContent("title", "layout title example");
    page.addKeyAndContent("subTitle", "layout subtitle example");
    page.addKeyAndContent("menu", "<li class = \"nav-item\"><a class = \"nav-item-link\">menu</a></li>");
    page.addKeyAndContent("content", "this is layout page preview.");
    File tempPageFile = page.run();
    
    return tempPageFile.getAbsolutePath();
  }
  
}
