package system;

import system.data.CSSXMLsettings;

public class SystemSettings {

  public final static String settingsXMLPath = "res/config.xml";
  public final static String draftDirectory = "draft";
  public final static String editDirectory = "edit";
  public final static String sourceDirectory = "res";
  public final static String imgDirectory = "upload";
  public final static String publishDirectory = "web";
  public final static String templateDirectory = "template";
  public final static String defaultSubDirectory = "page";
  public final static String defaultDirectoryPath = "/Documents/CMS/";
  public final static String ftpdefaultDirectory = "public_html";
  public final static String[] blueDefaultLayout = {"#1d5468", "#FFFFFF", "#FFFFFF"};
  public final static String[] roseDefaultLayout = {"#F2385A", "#FFFFFF", "#FFFFFF"};
  
  public CSSXMLsettings getInitLayoutStyle(String layoutName){
    
    CSSXMLsettings newCSS = new CSSXMLsettings();
    newCSS.setName(layoutName);
    if(layoutName.equals("blue")){
      newCSS.setHeaderBackground(blueDefaultLayout[0]);
      newCSS.setTitleColor(blueDefaultLayout[1]);
      newCSS.setSubTitleColor(blueDefaultLayout[2]);
    }
    else if(layoutName.equals("rose")){
      newCSS.setHeaderBackground(blueDefaultLayout[0]);
      newCSS.setTitleColor(blueDefaultLayout[1]);
      newCSS.setSubTitleColor(blueDefaultLayout[2]);
    }
    
    return newCSS;
  }
  
  
}
