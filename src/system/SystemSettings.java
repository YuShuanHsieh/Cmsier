package system;

import java.util.HashMap;
import java.util.Map;

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
  private Map<String, String[]> defaultLayout;
  
  public SystemSettings(){
    defaultLayout = new HashMap<String, String[]>();
  }

  public CSSXMLsettings getDefaultLayout(String layoutName){
    CSSXMLsettings newCSS = new CSSXMLsettings();
    if(defaultLayout.containsKey(layoutName)){
    //color order: [0]header, [1]title, [2]subtitle, [3]main, [4]content, [5]frame
      String[] defaultColors = defaultLayout.get(layoutName);
      newCSS.setName(layoutName);
      newCSS.setHeaderColor(defaultColors[0]);
      newCSS.setTitleColor(defaultColors[1]);
      newCSS.setSubTitleColor(defaultColors[2]);
      newCSS.setMainColor(defaultColors[3]);
      newCSS.setContentColor(defaultColors[4]);
      newCSS.setFrameColor(defaultColors[5]);
    }
    return newCSS;
  }
  
  public void initDefaultLayout(){
    //color order: [0]header, [1]title, [2]subtitle, [3]main, [4]content, [5]frame
    String[] blueLayout = {"#1d5468", "#FFFFFF", "#FFFFFF", "#fdf7e9", "#4f4f4f", "#0f2c35"};
    defaultLayout.put("blue", blueLayout);
    String[] roseLayout = {"#F2385A", "#FFFFFF", "#FFFFFF", "#F2F2F2", "#4f4f4f", "#333333"};
    defaultLayout.put("rose", roseLayout);
  }
  
  
}
