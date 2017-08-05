package model.utility;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;
import system.SystemSettings;
import system.data.CSSXMLsettings;
import system.data.Category;
import system.data.Settings;
import system.data.FtpSettings;

/** 
 * The helper is very important to get the stored data from specific files.
 * In most of situation, retrieving methods are invoked and used as user open this application. 
 */
public class XmlHelper {
  
  private Settings settings;
  
  public XmlHelper() {
  }
  
  /** 
   * Retrieving Settings if the root local directory exists or create a root directory automatically.
   */
  public Settings retrieveSettings() {
    try {
      File XMLfile = new File(SystemSettings.configXMLFile);
      JAXBContext context = JAXBContext.newInstance(Settings.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      
      if(XMLfile.exists()) {
        Settings settings = (Settings) unmarshaller.unmarshal(XMLfile);
        this.settings = settings;
        
        File rootDirectory = new File(settings.getLocalPath());
        if(!settings.isLocalPathExist() || !rootDirectory.exists()) {
          settings.setLocalPath(PathHelper.createDefaultDirectoy());
          writeSettingToXML(settings);
        }
        
        return settings;
      }
      
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public static Settings retrieveSettingFromFile() {
    try {
      File XMLfile = new File(SystemSettings.configXMLFile);
      JAXBContext context = JAXBContext.newInstance(Settings.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      
      if(XMLfile.exists()) {
        Settings settings = (Settings) unmarshaller.unmarshal(XMLfile);
        
        return settings;
      }
      
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public FtpSettings retrieveFtpSettingFromXML(File XMLfile) {
    try {
      JAXBContext context = JAXBContext.newInstance(FtpSettings.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      
      if(XMLfile.exists()) {
        FtpSettings ftpSettings = (FtpSettings) unmarshaller.unmarshal(XMLfile);
        
        return ftpSettings;
      }
      
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return new FtpSettings();
  }
  
  public static Map<String, Category> retrieveCatetoryFromXML(String localPath) {
    Map<String, Category> categories = new HashMap<String, Category>();
    File directory = new File(localPath + "category/");
    
    try {
      JAXBContext context = JAXBContext.newInstance(Category.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      
      for(File xmlFile : directory.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File directory, String fileName) {
          if(fileName.contains(".DS_Store")) return false;
          else return true;}
        }
      )) 
        {
          Category category = (Category) unmarshaller.unmarshal(xmlFile);
          categories.put(category.getName(), category);
        }
    } 
    catch (JAXBException e) {
      e.printStackTrace();
    }
    
    if(categories.isEmpty()) {
      categories = SystemSettings.getDefaultCategory();
    }
    
    return categories;
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
  
  public static void writeCategoryToXML(Category category, String XMLFilePath) {
    try {
      File XMLfile = new File(XMLFilePath + category.getName() + ".xml");
      if(!XMLfile.exists()){
        XMLfile.createNewFile();
      }
      JAXBContext context = JAXBContext.newInstance(Category.class);
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(category, XMLfile);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public static void writeSettingToXML(Settings settings){
    try {
        File XMLfile = new File(SystemSettings.configXMLFile);
        File localXMLfile = new File(settings.getLocalPath() + SystemSettings.configXMLFile);
        JAXBContext context = JAXBContext.newInstance(Settings.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(settings, XMLfile);
        marshaller.marshal(settings, localXMLfile);
    } catch (JAXBException e) {
        e.printStackTrace();
    }
  }
  
  public void writeSettingToXML(FtpSettings ftpSettings){
    try {
        File XMLfile = new File(SystemSettings.ftpXMLFile);
        if(!XMLfile.exists()){
          XMLfile.createNewFile();
        }
        JAXBContext context = JAXBContext.newInstance(FtpSettings.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(ftpSettings, XMLfile);
    } catch (Exception e) {
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
