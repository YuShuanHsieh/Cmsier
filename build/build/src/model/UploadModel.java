package model;
import controller.Controller;
import model.generator.Generator;
import model.utility.PathHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
//import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPClient;

public class UploadModel implements Model {

  private Controller controller;
  private FTPClient ftpClient;
  
  public UploadModel() {
    ftpClient = new FTPClient();
  }
  
  @Override
  public void init() {
    
  }

  @Override
  public void setController(Controller controller) {
    this.controller = controller;
  }
  
  public boolean connectToWebServer(String host, String account, String password) {
    try{
      
      ftpClient.connect(host, 21);
      ftpClient.login(account, password);
      
      if(ftpClient.getReplyCode() == 530) {
       return false;
      }
      else {
        return true;
      }
    } 
    catch(IOException e){
      e.printStackTrace();
      return false;
    }
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
    try{
      Generator generator = new Generator();
      PathHelper pathHelper = new PathHelper();
      pathHelper.deleteFilesFromDirectory("./web/");
      generator.generateAllPage(controller.getSystemManager().getSettings(), controller.getSystemManager().getData());
      return true;
    }catch(Exception e) {
      return false;
    }
  }
  
  public boolean uploadFinalPageFile() {
    List<String> directoryList = new LinkedList<String>();
    List<String> tempList = new LinkedList<String>();
    String pathName;
    String publishPath;
    
    directoryList.add("./page");
    directoryList.add("./res");
    directoryList.add("./web");
    directoryList.add("./upload");
    
    try{
      ftpClient.changeWorkingDirectory("/public_html");
      ftpClient.enterLocalPassiveMode();
      ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
    }
    catch(Exception e) {
      e.printStackTrace();
      return false;
    }
    
    while(true){
      for(String directoryName : directoryList){
        pathName = directoryName + "/";
        publishPath = pathName;
        File directory = new File(pathName);
        File[] files = directory.listFiles();
      
        for(File file : files) {
      
          if(file.isDirectory() && file.getName().matches("[a-zA-Z0-9\\-]*")) {
            tempList.add(directoryName + "/" + file.getName());
          }
          else {
            try{
              FileInputStream uploadfile = new FileInputStream(pathName + file.getName());
              
              if(publishPath.contains("/web")) {
                publishPath = publishPath.replace("/web", "");
              }
              
              if(ftpClient.cwd("/public_html/" + publishPath) == 550){
                ftpClient.makeDirectory("/public_html/" + publishPath);
              }
              
              ftpClient.storeFile( "/public_html/" + publishPath + file.getName(), uploadfile);
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
    return true;
  }
}
