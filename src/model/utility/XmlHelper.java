package model.utility;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;
import system.SystemSettings;
import system.data.CSSXMLsettings;
import system.data.Settings;

public class XmlHelper {

  private Settings settings;
  
  public XmlHelper() {
 
  }
  
  public Settings retrieveSettingFromXML() {
    try {
      File XMLfile = new File(SystemSettings.configXMLFile);
      JAXBContext context = JAXBContext.newInstance(Settings.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      
      if(XMLfile.exists()) {
        Settings settings = (Settings) unmarshaller.unmarshal(XMLfile);
        this.settings = settings;
        
        return settings;
      }
      
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public CSSXMLsettings retrieveCSSSettingFromXML(File CssXMLFile) {
    
    try {
      JAXBContext context = JAXBContext.newInstance(CSSXMLsettings.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      
      CSSXMLsettings CSSsettings = (CSSXMLsettings) unmarshaller.unmarshal(CssXMLFile);
      return CSSsettings;
      
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public void writeSettingToXML(CSSXMLsettings cssSettings, Settings settings){
    final String CssXMLPath =settings.getLocalPath() + SystemSettings.D_layout + "/" + SystemSettings.D_layout_xml + "/";
    try {
        File XMLfile = new File(CssXMLPath + cssSettings.getName() + ".xml");
        if(!XMLfile.exists()){
          XMLfile.createNewFile();
        }
        JAXBContext context = JAXBContext.newInstance(CSSXMLsettings.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(cssSettings, XMLfile);
    } catch (Exception e) {
        e.printStackTrace();
    }
  }
  
  public void writeSettingToXML(Settings web){
    try {
        File XMLfile = new File(SystemSettings.configXMLFile);
        JAXBContext context = JAXBContext.newInstance(Settings.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(web, XMLfile);
    } catch (JAXBException e) {
        e.printStackTrace();
    }
  }
  
  public boolean isLocalPathExistingInXML(){
    if(settings == null){
      throw new NullPointerException("Settings variable have not been set");
    }
    
    String localPath = settings.getLocalPath();
    if(localPath.trim().isEmpty()){
      return false;
    }
    else {
      return true;
    } 
  }
}
