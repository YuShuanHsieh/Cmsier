package model;
import controller.UploadController;
import model.component.Generator;
import model.utility.PathHelper;
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
import view.View;

public class UploadModel extends Model {

  private FTPClient ftpClient;
  private View view;
   
  public UploadModel() {
    ftpClient = new FTPClient();
  }

  public boolean connectToWebServer(String host, String account, String password) {
    try{
      
      ftpClient.connect(host, 21);
      ftpClient.login(account, password);
      
      if(ftpClient.getReplyCode() == 530) {
        view.updateStatement(UploadController.UPLOAD_PROCESS, Statement.success("Connot connect to server."));
      }
      else {
        view.updateStatement(UploadController.UPLOAD_PROCESS, Statement.success("Connect to Web server successfully."));
        return true;
      }
    } 
    catch(IOException e){
      e.printStackTrace();
    }
    return false;
  }
  
  public boolean disconnectToWebServer() {
    try{
      ftpClient.logout();
      ftpClient.disconnect();
      return true;
    }
    catch(IOException e){
      e.printStackTrace();
      return false;
    }
  }
  
  public boolean generateFinalPage() {
    view.updateStatement(UploadController.UPLOAD_PROCESS, Statement.success("Start to generate Web pages."));
    try{
      Generator generator = new Generator();
      PathHelper pathHelper = new PathHelper();
      pathHelper.deleteFilesFromDirectory(dataCenter.getSettings().getLocalPath() + "web/");
      generator.generateAllPage(dataCenter.getSettings(), dataCenter.getData());
      return true;
    }catch(Exception e) {
      return false;
    }
  }
  
  public boolean uploadFinalPageFile() {
    String localPath = dataCenter.getSettings().getLocalPath();
    List<String> directoryList = new LinkedList<String>();
    List<String> tempList = new LinkedList<String>();
    String pathName;
    String publishPath;
    String ftpPath = "/"+SystemSettings.ftpdefaultDirectory+"/";
    
    directoryList.add(SystemSettings.editDirectory);
    directoryList.add(SystemSettings.sourceDirectory);
    directoryList.add(SystemSettings.publishDirectory);
    directoryList.add(SystemSettings.imgDirectory);
    
    try{
      ftpClient.changeWorkingDirectory("/" + SystemSettings.ftpdefaultDirectory);
      ftpClient.enterLocalPassiveMode();
      ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
    }
    catch(Exception e) {
      e.printStackTrace();
      return false;
    }
    view.updateStatement(UploadController.UPLOAD_PROCESS, Statement.success("Start to upload Web pages to server."));
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
              
              if(publishPath.contains(SystemSettings.publishDirectory + "/")) {
                publishPath = publishPath.replace(SystemSettings.publishDirectory + "/", "");
              }
              
              if(ftpClient.cwd(ftpPath + publishPath) == 550){
                ftpClient.makeDirectory(ftpPath + publishPath);
              }
              System.out.println("path test : " + localPath + pathName + file.getName());
              ftpClient.storeFile( ftpPath + publishPath + file.getName(), uploadfile);
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
    view.updateStatement(UploadController.UPLOAD_PROCESS, Statement.success("Upload Web pages to server successfully."));
    return true;
  }
}
