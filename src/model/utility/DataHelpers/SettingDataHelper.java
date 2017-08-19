package model.utility.DataHelpers;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import model.utility.PathHelper;
import system.SystemSettings;
import system.data.Settings;

/**
 * Deal with the Settings object.
 * @see Settings
 *  */

public class SettingDataHelper implements DataHelperBase<Settings> {
  
  @Override
  public Settings read(String name) {
    File XMLfile = new File(SystemSettings.configXMLFile);
    
    try {
      JAXBContext context = JAXBContext.newInstance(Settings.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      Settings setting = (Settings)unmarshaller.unmarshal(XMLfile);
      
      /** Local path is essential data in Settings object.
       *  Must be set up local path if it does not exist. */
      if(!setting.isLocalPathExist()) {
        setupLocalPath(setting);
      }
      
      return setting;
    } 
    catch (JAXBException e) {
      e.printStackTrace();
      return null;
    }
  }
  
  @Override
  public Boolean delete(String name) {
    return null;
  }
  
  @Override
  public Boolean write(Settings setting) {
    try {
      /**
       * The data must write into two XML files.
       * one is in the inner folder of system.
       * The other one is in the local folder.
       *  */
      File XMLfile = new File(SystemSettings.configXMLFile);
      File localXMLfile = new File(setting.getLocalPath() + SystemSettings.configXMLFile);
      JAXBContext context = JAXBContext.newInstance(Settings.class);
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(setting, XMLfile);
      marshaller.marshal(setting, localXMLfile);
      return true;
    } 
    catch (JAXBException e) {
      e.printStackTrace();
      return false;
    }
  }
  
  private void setupLocalPath(Settings setting) {
    File rootDirectory = new File(setting.getLocalPath());
    if(rootDirectory.exists()) {
      setting.setLocalPath(PathHelper.createDefaultDirectoy());
      write(setting);
    }
  }
  
}
