package model.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
//import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPClient;

public class Upload {

  private final FTPClient ftp = new FTPClient();
  private final String host = "files.000webhost.com";
  private final String account = "cherriesweb"; // should be changed.
  private final String password = "abcd3350368"; // should be changed.
  
  public void connect() {
    try{
      ftp.connect(host, 21);
      ftp.login(account, password);
      System.out.println(ftp.getReplyString());
    } 
    catch(IOException e){
        e.printStackTrace();
    }
  }
  
  public void disconnect(){
    try{
      ftp.logout();
      ftp.disconnect();
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }
  
  public void uploadFinalPageFile() {
    List<String> directoryList = new LinkedList<String>();
    List<String> tempList = new LinkedList<String>();
    String pathName;
    String publishPath;
    
    directoryList.add("./page");
    directoryList.add("./res");
    directoryList.add("./web");
    
    try{
      ftp.changeWorkingDirectory("/public_html");
      ftp.enterLocalPassiveMode();
    }
    catch(Exception e) {
      e.printStackTrace();
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
              
              if(ftp.cwd("/public_html/" + publishPath) == 550){
                ftp.makeDirectory("/public_html/" + publishPath);
              }
              
              ftp.storeFile( "/public_html/" + publishPath + file.getName(), uploadfile);
            }
            catch(IOException e) {
              e.printStackTrace();
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
    
    System.out.println("Upload success!");
  }
  
  
  public void deleteCurrentFinalPage() {
    
    File rootDirectoy = new File("./web/");
    List<File> currentList;
    List<File> tempList = new LinkedList<File>();
    Deque<File> stack = new ArrayDeque<File>();
    
    if(rootDirectoy.exists()) {
      currentList = Arrays.asList(rootDirectoy.listFiles());
      stack.push(rootDirectoy);
      
      while(true) {
        for(File existingFile : currentList) {
          if(existingFile.isDirectory()) {
            stack.push(existingFile);
            tempList.addAll(Arrays.asList(existingFile.listFiles()));
          }
        }
        if(tempList.isEmpty()){
          break;
        }
        currentList = tempList.stream().collect(Collectors.toList());
        tempList.clear();
      }
      
      while(!stack.isEmpty()){
        for(File file : stack.pop().listFiles()) {
          file.delete();
        }
      }
    }
    else {
      System.out.println("root directory error");
    }
  }
  
}
