package model.utility.DataHelpers;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import system.SystemSettings;
import system.data.FtpSettings;

public class FTPDataHelper implements DataHelperBase<FtpSettings> {
  
  @Override
  public FtpSettings read(String name) {
    File FTPXMLfile = new File(SystemSettings.ftpXMLFile);
    try {
      JAXBContext context = JAXBContext.newInstance(FtpSettings.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      
      if(FTPXMLfile.exists()) {
        FtpSettings ftpSettings = (FtpSettings) unmarshaller.unmarshal(FTPXMLfile);
        return ftpSettings;
      }
      
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return new FtpSettings();
  }

  @Override
  public Boolean delete(String name) {
    return null;
  }

  @Override
  public Boolean write(FtpSettings ftpSettings) {
    try {
      File XMLfile = new File(SystemSettings.ftpXMLFile);
      if(!XMLfile.exists()){
        XMLfile.createNewFile();
      }
      JAXBContext context = JAXBContext.newInstance(FtpSettings.class);
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(ftpSettings, XMLfile);
      return true;
    } 
    catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
