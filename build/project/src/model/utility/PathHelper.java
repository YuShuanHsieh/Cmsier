/**
  * This PathHelper class provides full-path retrieving from page and check the path is valid.
  * @author yu-shuan
  */
package model.utility;
import data.SetPage;
import data.Settings;
import data.SimplePage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PathHelper {
  
  private static final String defaultDirectoryPath = "/Documents/CMS/";
  private static final String[] subDefaultDirectorys = {"draft/","draft/page/", "page/","upload/","web/","res/"};
  
  public String createDefaultDirectoyInLocalPath(){
    String homeDirectory = System.getProperty("user.home");
    String localPath = "";
    File defaultDirectory = new File(homeDirectory + defaultDirectoryPath);
    
    try{
      if(!defaultDirectory.exists()) {
        defaultDirectory.mkdirs();
      }
      
      localPath = homeDirectory + defaultDirectoryPath;
      
      for(String subDefaultDirectory : subDefaultDirectorys) {
        File sub = new File(homeDirectory + defaultDirectoryPath + subDefaultDirectory);
        if(!sub.exists()) {
          sub.mkdirs();
        }
      } 
      
      copyInnerFileToLocalPath(localPath, "res/");
      copyInnerFileToLocalPath(localPath, "page/");

      
    }catch(Exception e) {
      e.printStackTrace();
      return null;
    }
    return homeDirectory + defaultDirectoryPath;
  }
  
  /* !!Notice: ignore the ./default/ folder.
   * @param pathURL The initial pathURL representing to a specific parent folder. such as ./web/
   */
  public String getPathFromSimplePage(SimplePage simplePage,Settings settings, String pathURL) {
    Deque<SetPage> stack = new LinkedList<SetPage>();
    SetPage setPage = simplePage.getParent();
    
    while(setPage != null) {
      if(pathURL.equals(settings.getLocalPath() + "page/") || !setPage.getName().equals("default"))
      stack.push(setPage);
      setPage = setPage.getParent();
    }
    
    while(!stack.isEmpty()) {
      pathURL = pathURL + stack.pop().getName() + "/";
    }
    
    pathURL = pathURL + simplePage.getName();
    return pathURL;
  }
  
  public boolean checkPathFromSimplePage(SimplePage simplePage, int type) {
    
    String pathUrl = "";
    
    if(type == 1){
      pathUrl = "web/";
    }
    else {
      pathUrl = "draft/";
    }
    
    Deque<SetPage> stack = new LinkedList<SetPage>();
    SetPage setPage = simplePage.getParent();
    
    while(setPage != null && !setPage.getName().equals("default")) {
      stack.push(setPage);
      setPage = setPage.getParent();
    }
    
    while(!stack.isEmpty()) {
      pathUrl = pathUrl + stack.pop().getName() + "/";
      
      try{
        if(!Files.exists(Paths.get(pathUrl))){
          Files.createDirectories(Paths.get(pathUrl));
        }
      } catch(Exception e) {
        e.printStackTrace();
        return false;
      }
    }
    
   return true; 
  }
  
  public void deleteFilesFromDirectory(String directoryPath) {
    File rootDirectoy = new File(directoryPath);
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
  
  private static void copyInnerFileToLocalPath(String localPath, String innerPath) {
    List<File> fileList;
    List<File> temp = new ArrayList<File>();
    File innerDirectory = new File(innerPath);
    if(!innerDirectory.exists()) {
      throw new NullPointerException("InnerPath does not exist");
    }
    
    fileList = Arrays.asList(innerDirectory.listFiles());
    
    while(true){
      for(File file : fileList) {
        File newFile = new File(localPath + file.getPath());
        if(file.isDirectory()) {
          if(!newFile.exists()) {
            newFile.mkdirs();
          }
          temp.addAll(Arrays.asList(file.listFiles()));
        }
        else {
          if(!newFile.exists()) {
            try{
              Files.copy(file.toPath(), newFile.toPath());
            }
            catch(Exception e) {
              e.printStackTrace();
            }
          }
        }
      }
      if(temp.isEmpty()) {
        break;
      }
      
      fileList = temp.stream().collect(Collectors.toList());
      temp.clear();
    }
  }
  
  /*
  public static void main(String[] args) {
    PathHelper helper = new PathHelper();
    helper.createDefaultDirectoyInLocalPath();
  }
  */
  
}
