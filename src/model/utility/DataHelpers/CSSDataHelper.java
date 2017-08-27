package model.utility.DataHelpers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import system.SystemSettings;
import system.data.CSSXMLsettings;

/**
 * @see CSSXMLsettings
 *  */

public class CSSDataHelper implements DataHelper<CSSXMLsettings> {

  private String localCSSPath;
  private Map<String, String[]> defaultLayout;
  
  public CSSDataHelper(String localPath) {
    this.localCSSPath = localPath + SystemSettings.CSSxmlPath;
  }
  
  @Override
  public CSSXMLsettings read(String layoutName)  {
    File XMLFile = new File(localCSSPath + layoutName + ".xml"); 
    try{
      if(!XMLFile.exists()){
        return getDefault(layoutName);
      }
      else{
        JAXBContext context = JAXBContext.newInstance(CSSXMLsettings.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (CSSXMLsettings) unmarshaller.unmarshal(XMLFile);
      }
    }
    catch(Exception e){
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public Boolean delete(String name) {
    return null;
  }

  @Override
  public Boolean write(CSSXMLsettings cssSettings) {
    try {
      File XMLfile = new File(localCSSPath + cssSettings.getName() + ".xml");
      if(!XMLfile.exists()){
        XMLfile.createNewFile();
      }
      JAXBContext context = JAXBContext.newInstance(CSSXMLsettings.class);
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(cssSettings, XMLfile);
    } 
    catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  
  private CSSXMLsettings getDefault(String layoutName) {
    setupDefault();
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
  
  private void setupDefault() {
    defaultLayout = new HashMap<String, String[]>();
    String[] blueLayout = {"#1d5468", "#FFFFFF", "#FFFFFF", "#fdf7e9", "#4f4f4f", "#0f2c35"};
    defaultLayout.put("blue", blueLayout);
    String[] roseLayout = {"#F2385A", "#FFFFFF", "#FFFFFF", "#F2F2F2", "#4f4f4f", "#333333"};
    defaultLayout.put("rose", roseLayout);
    String[] testLayout = {"#000000", "#FFFFFF", "#FFFFFF", "#F2F2F2", "#4f4f4f", "#333333"};
    defaultLayout.put("test", testLayout);
  }

}
