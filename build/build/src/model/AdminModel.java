package model;

import controller.Controller;
import data.Settings;
import model.utility.XmlHelper;

public class AdminModel implements Model {

  Controller controller;
  XmlHelper xmlHelper;
  
  @Override
  public void init() {
    xmlHelper = new XmlHelper();
    Settings settings = xmlHelper.retrieveSettingFromXML();
    controller.getSystemManager().setSettings(settings);
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
  
}
