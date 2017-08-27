package model;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.io.FilenameFilter;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import model.utility.DataHelpers.FTPDataHelper;
import system.DataCenter;
import system.Statement;
import system.SystemSettings;
import system.data.FtpSettings;
import view.UploadView;
import view.View;

public class UploadModel implements Model {

  private View view;
  private DataCenter dataCenter;
  
  private FTPClient ftpClient;
  private FtpSettings ftpSetting;
  private String localRootPath;
  private FTPDataHelper helper;
   
  public UploadModel(DataCenter dataCenter) {
    this.dataCenter = dataCenter;
    this.localRootPath = dataCenter.getSettings().getLocalPath();
    this.ftpClient = new FTPClient();
    this.helper = new FTPDataHelper();
  }
  
  @Override
  public void attach(View view) {
    this.view = view;
  }
  
  @Override
  public void init(){
    ftpSetting = helper.read(null);
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
    view.updateStatement(UploadView.UPLOAD_PROCESS, Statement.success("- Start to upload Web pages to server."));
    
    uploadSingleDirectory("/public_html/",localRootPath + "web/","default/");
    uploadSingleDirectory("/public_html/css/",localRootPath + "css/","");
    uploadSingleDirectory("/public_html/upload/",localRootPath + "upload/","");
    uploadSingleDirectory("/public_html/edit/",localRootPath + "edit/","");
    uploadSingleDirectory("/public_html/config/",localRootPath + "config/","");
    uploadSingleDirectory("/public_html/category/",localRootPath + "category/","");
    uploadSingleDirectory("/public_html/layout/",localRootPath + "layout/","");

    view.updateStatement(UploadView.UPLOAD_PROCESS, Statement.success("- Upload Web pages to server successfully."));
    return true;
  }
  
  /**
   * @param targetPath - remote directory path. should give an existing directory.
   *  */
  public boolean uploadSingleDirectory(String target, String local, String ignore) {
    File localDirectory = new File(local);
    uploadSingleDirectory(target, localDirectory, ignore);
    return true;
  }
    
  public void uploadSingleDirectory(String targetDirectoryPath,File directoyFile, String ignore){
    targetDirectoryPath = targetDirectoryPath.replace(ignore, "");
    createRemoteDirectory(targetDirectoryPath, ignore);
    for(File file : directoyFile.listFiles(new FilenameFilter() {
      @Override 
      public boolean accept(File dir, String name) {
        return !name.matches(".DS_Store");
      }
    })) {
      if(file.isDirectory()) {
        uploadSingleDirectory(targetDirectoryPath + file.getName()+"/", file, ignore);
      }
      else if(file.isFile()) {
        uploadSingleFile(targetDirectoryPath + file.getName() ,file);
      }
    }
  }
  
  private void createRemoteDirectory(String targetDirectoryPath, String ignore) {
    try {
      if(!ignore.equals("")) {
        if(!targetDirectoryPath.contains(ignore) && ftpClient.cwd(targetDirectoryPath) == 550){
          if(!ftpClient.makeDirectory(targetDirectoryPath)) {
          return;
          }
        }
      }
      else {
        if(ftpClient.cwd(targetDirectoryPath) == 550){
          if(!ftpClient.makeDirectory(targetDirectoryPath)) {
          return;
          }
        }
      }
    }
    catch(Exception exception) {
      exception.printStackTrace();
    }  
  }
  
  public Boolean uploadSingleFile(String targetPath, File uploadFile){
    try {
      FileInputStream upload = new FileInputStream(uploadFile);
      if(!ftpClient.storeFile(targetPath, upload)) {
        System.out.println("targetPath" + targetPath);
        System.out.println("uploadPath" + uploadFile.getAbsolutePath());
        System.out.println(ftpClient.getReplyString());
      }
      upload.close();
      return true;
    }
    catch(Exception exception) {
      exception.printStackTrace();
      return false;
    }
  }
  
  public void saveFtpSettings(String host, String account, String password){
    ftpSetting.setHost(host);
    ftpSetting.setAccount(account);
    ftpSetting.setPassword(password);
    helper.write(ftpSetting);
  }
  
  public boolean downloadFileFromServer(){
    String[] downloadDirectories = {SystemSettings.configurePath,SystemSettings.pagePath,
        SystemSettings.uploadPath,SystemSettings.CSSxmlPath, SystemSettings.categoryPath};
    
    for(String downloadDirectory : downloadDirectories){
      view.updateStatement(UploadView.UPLOAD_PROCESS, Statement.success("- Download files from " + downloadDirectory + "."));
      if(!downloadFileFromServerByPath(downloadDirectory)){
        return false;
      }
    }
    
    dataCenter.init();
    view.updateStatement(UploadView.UPLOAD_PROCESS, Statement.success("- Download files successfully."));
    return true;
  }
  
  private boolean downloadFileFromServerByPath(String directoryPath){
    List<String> currentList= new ArrayList<String>();
    List<String> tempList = new ArrayList<String>();
    try{
      /** Initialize */
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
