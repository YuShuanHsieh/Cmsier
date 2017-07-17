package model;
import controller.UploadController;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
//import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPClient;
import system.Statement;
import system.SystemSettings;

public class UploadModel extends Model {

  private FTPClient ftpClient;
   
  public UploadModel() {
    ftpClient = new FTPClient();
  }

  public boolean connectToWebServer(String host, String account, String password) {
    try{
      view.updateStatement(UploadController.UPLOAD_PROCESS, Statement.success("- Start to connect to server."));
      ftpClient.connect(host, 21);
      ftpClient.login(account, password);
      
      if(ftpClient.getReplyCode() == 530) {
        view.updateStatement(UploadController.UPLOAD_PROCESS, Statement.success("- Connot connect to server."));
        
      }
      else {
        view.updateStatement(UploadController.UPLOAD_PROCESS, Statement.success("- Connect to Web server successfully."));
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
      view.updateStatement(UploadController.UPLOAD_PROCESS, Statement.success("- Close the connection."));
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }

  public boolean uploadFinalPageFile() {
    String localPath = dataCenter.getSettings().getLocalPath();
    List<String> directoryList = new LinkedList<String>();
    List<String> tempList = new LinkedList<String>();
    String pathName;
    String publishPath;
    String ftpPath = "/"+SystemSettings.D_ftp+"/";
    
    directoryList.add(SystemSettings.D_edit);
    directoryList.add(SystemSettings.D_css);
    directoryList.add(SystemSettings.D_web);
    directoryList.add(SystemSettings.D_upload);
    
    try{
      ftpClient.changeWorkingDirectory("/" + SystemSettings.D_ftp);
      ftpClient.enterLocalPassiveMode();
      ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
    }
    catch(Exception e) {
      e.printStackTrace();
      return false;
    }
    view.updateStatement(UploadController.UPLOAD_PROCESS, Statement.success("- Start to upload Web pages to server."));
    /* Upload directory to Web server */
    while(true){
      for(String directoryName : directoryList){
        pathName = directoryName + "/";
        publishPath = pathName;
        File directory = new File(localPath + pathName);
        File[] files = directory.listFiles();
      
        for(File file : files) {
      
          if(file.isDirectory() && file.getName().matches("[a-zA-Z0-9\\-]*")) {
            tempList.add(directoryName + "/" + file.getName());
          }
          else {
            try{
              FileInputStream uploadfile = new FileInputStream(localPath + pathName + file.getName());
              
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
      FileInputStream configFile = new FileInputStream(localPath + SystemSettings.configXMLFile);
      ftpClient.storeFile( ftpPath + SystemSettings.configXMLFile, configFile);
      configFile.close();
    }
    catch(Exception e){
      e.printStackTrace();  
    }
    
    view.updateStatement(UploadController.UPLOAD_PROCESS, Statement.success("- Upload Web pages to server successfully."));
    return true;
  }
}
