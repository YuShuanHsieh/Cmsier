package model;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
//import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import model.utility.XmlHelper;
import system.Statement;
import system.SystemSettings;
import system.data.FtpSettings;
import view.UploadView;

public class UploadModel extends Model {

  private FTPClient ftpClient;
  private FtpSettings ftpSetting;
  private XmlHelper xmlHelper;
  private String localRootPath;
   
  public UploadModel() {
    ftpClient = new FTPClient();
    xmlHelper = new XmlHelper();
  }
  
  @Override
  public void init(){
    localRootPath = dataCenter.getSettings().getLocalPath();
    File ftpXmlFile = new File(SystemSettings.ftpXMLFile);
    ftpSetting = xmlHelper.retrieveFtpSettingFromXML(ftpXmlFile);
    if(ftpSetting != null){
      view.updateStatement(UploadView.UPLOAD_INFO, Statement.success(ftpSetting));
    }
  }

  public boolean connectToWebServer(String host, String account, String password) {
    try{
      view.updateStatement(UploadView.UPLOAD_PROCESS, Statement.success("- Start to connect to server."));
      ftpClient.connect(host, 21);
      ftpClient.login(account, password);
      
      if(ftpClient.getReplyCode() == 530) {
        view.updateStatement(UploadView.UPLOAD_PROCESS, Statement.success("- Connot connect to server."));
        
      }
      else {
        /* This part should be changed. */
        ftpClient.changeWorkingDirectory("/" + SystemSettings.D_ftp);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        view.updateStatement(UploadView.UPLOAD_PROCESS, Statement.success("- Connect to Web server successfully."));
        return true;
      }
      
    } 
    catch(IOException e){
      e.printStackTrace();
    }
    return false;
  }
  
  public void disconnectToWebServer() {
    try{
      ftpClient.logout();
      ftpClient.disconnect();
      view.updateStatement(UploadView.UPLOAD_PROCESS, Statement.success("- Close the connection."));
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }

  public boolean uploadFinalPageFile() {
    //String localPath = dataCenter.getSettings().getLocalPath();
    List<String> directoryList = new LinkedList<String>();
    List<String> tempList = new LinkedList<String>();
    String pathName;
    String publishPath;
    String ftpPath = "/"+SystemSettings.D_ftp+"/";
    
    directoryList.add(SystemSettings.D_edit);
    directoryList.add(SystemSettings.D_css);
    directoryList.add(SystemSettings.D_web);
    directoryList.add(SystemSettings.D_upload);
    
    view.updateStatement(UploadView.UPLOAD_PROCESS, Statement.success("- Start to upload Web pages to server."));
    /* Upload directory to Web server */
    while(true){
      for(String directoryName : directoryList){
        pathName = directoryName + "/";
        publishPath = pathName;
        File directory = new File(localRootPath + pathName);
        File[] files = directory.listFiles();
      
        for(File file : files) {
      
          if(file.isDirectory() && file.getName().matches("[a-zA-Z0-9\\-]*")) {
            tempList.add(directoryName + "/" + file.getName());
          }
          else {
            try{
              FileInputStream uploadfile = new FileInputStream(localRootPath + pathName + file.getName());
              
              if(publishPath.contains(SystemSettings.D_web + "/")) {
                publishPath = publishPath.replace(SystemSettings.D_web + "/", "");
              }
              
              if(ftpClient.cwd(ftpPath + publishPath) == 550){
                ftpClient.makeDirectory(ftpPath + publishPath);
              }
              
              ftpClient.storeFile( ftpPath + publishPath + file.getName(), uploadfile);
              uploadfile.close();
            }
            catch(IOException e) {
              e.printStackTrace();
              return false;
            }
          }
        
        }
      }
      if(tempList.isEmpty()) {
        break;
      }
      directoryList = tempList.stream().collect(Collectors.toList());
      tempList.clear();
    }
    
    /* Upload the config file to Web server. */
    try{
      FileInputStream configFile = new FileInputStream(localRootPath + SystemSettings.configXMLFile);
      ftpClient.storeFile( ftpPath + SystemSettings.configXMLFile, configFile);
      configFile.close();
    }
    catch(Exception e){
      e.printStackTrace();  
    }
    
    view.updateStatement(UploadView.UPLOAD_PROCESS, Statement.success("- Upload Web pages to server successfully."));
    return true;
  }
  
  public void saveFtpSettings(String host, String account, String password){
    ftpSetting.setHost(host);
    ftpSetting.setAccount(account);
    ftpSetting.setPassword(password);
    
    xmlHelper.writeSettingToXML(ftpSetting);
  }
  
  public boolean downloadFileFromServer(){
    
    String[] downloadDirectories = {SystemSettings.D_config + "/",SystemSettings.D_edit + "/",
        SystemSettings.D_upload + "/",SystemSettings.D_layout + "/" + SystemSettings.D_layout_xml + "/"};
    
    for(String downloadDirectory : downloadDirectories){
      view.updateStatement(UploadView.UPLOAD_PROCESS, Statement.success("- Download files from " + downloadDirectory + "."));
      if(!downloadFileFromServerByPath(downloadDirectory)){
        return false;
      }
    }
    
    view.updateStatement(UploadView.UPLOAD_PROCESS, Statement.success("- Download files successfully."));
    return true;
  }
  
  private boolean downloadFileFromServerByPath(String directoryPath){
    List<String> currentList= new ArrayList<String>();
    List<String> tempList = new ArrayList<String>();
    try{
      /* Initialize */
      currentList.add(directoryPath);
      
      while(true){
        for(String ftpFilePath : currentList){
          FTPFile[] ftpFiles = ftpClient.listFiles(ftpFilePath);
          
          for(FTPFile ftpFile :ftpFiles){
            if(ftpFile.isDirectory() && ftpFile.getName().matches("[a-zA-Z0-9\\-]*")){
              tempList.add(ftpFilePath + ftpFile.getName() + "/");
            }
            else if(ftpFile.isFile()){
              File localFile = new File(localRootPath + ftpFilePath + ftpFile.getName());
              if(!localFile.exists()){
                localFile.createNewFile();
              }
              OutputStream localFileStream = new FileOutputStream(localFile);
              ftpClient.retrieveFile(ftpFilePath + ftpFile.getName(), localFileStream);
            }
          }
          
        }
        if(tempList.isEmpty()){
          break;
        }
        currentList = tempList.stream().collect(Collectors.toList());
        tempList.clear();
      }
    }
    catch(Exception e){
      e.printStackTrace();
      return false;
    }
    
    return true;
  }
 
  
}
