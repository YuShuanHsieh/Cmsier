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
      File XMLfile = new File(SystemSettings.settingsXMLPath);
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
  
  public CSSXMLsettings retrieveCSSSettingFromXML(String layoutName) {
    try {
      JAXBContext context = JAXBContext.newInstance(CSSXMLsettings.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      
      File cssFile = new File("layout/CssXML/" + layoutName + ".xml");
      if(cssFile.exists()) {
        CSSXMLsettings CSSsettings = (CSSXMLsettings) unmarshaller.unmarshal(cssFile);
        return CSSsettings;
      }
      else{
        System.out.println("This file does not exist.");
      }
      
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public void writeSettingToXML(CSSXMLsettings cssSettings){
    try {
        File XMLfile = new File("layout/CssXML/" + cssSettings.getName() + ".xml");
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
        File XMLfile = new File(SystemSettings.settingsXMLPath);
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
