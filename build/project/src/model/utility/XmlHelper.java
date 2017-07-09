package model.utility;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;
import data.Settings;

public class XmlHelper {

  private final File XMLfile;
  private Settings settings;
  
  public XmlHelper() {
    XMLfile = new File("./res/config.xml");
  }
  
  public Settings retrieveSettingFromXML() {
    try {
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
  
  public void writeSettingToXML(Settings web) {
    try {
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
