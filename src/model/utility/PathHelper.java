/**
  * This PathHelper class provides full-path retrieving from page and check the path is valid.
  * @author yu-shuan
  */
package model.utility;
import system.SystemSettings;
import system.data.Settings;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;

public class PathHelper {
  /*
   * @see SystemSettings 
   */
  private static final String[] defaultDirectories = {
      SystemSettings.D_config,
      SystemSettings.D_draft, SystemSettings.D_draft + "/" + SystemSettings.D_sub_page + "/", 
      SystemSettings.D_edit, SystemSettings.D_upload,
      SystemSettings.D_web, SystemSettings.D_web + "/" + SystemSettings.D_sub_page + "/",
      SystemSettings.D_css,
      SystemSettings.D_layout,SystemSettings.D_layout + "/" + SystemSettings.D_layout_xml + "/",
      SystemSettings.D_layout + "/" + SystemSettings.D_layout_preview + "/"
      };
  
  public static String createDefaultDirectoy(){
    String homeDirectory = System.getProperty("user.home");
    String rootDirectoryPath = homeDirectory + SystemSettings.D_root;
    File rootDirectory = new File(rootDirectoryPath);
    
    try{
      
      if(!rootDirectory.exists()) {
        rootDirectory.mkdirs();
      }
      
      for(String subDefaultDirectory : defaultDirectories) {
        File sub = new File(homeDirectory + SystemSettings.D_root + subDefaultDirectory);
        if(!sub.exists()) {
          sub.mkdirs();
        }
      } 
      
      FileUtils.copyDirectory(new File(SystemSettings.D_css + "/"), rootDirectory);
      FileUtils.copyDirectory(new File(SystemSettings.D_edit + "/"), rootDirectory);
      FileUtils.copyDirectory(new File(SystemSettings.D_config + "/"), rootDirectory);
      
      //copyInnerDirdctoryToLocalPath(rootDirectoryPath, SystemSettings.D_css + "/");
      //copyInnerDirdctoryToLocalPath(rootDirectoryPath, SystemSettings.D_edit + "/");
      //copyInnerDirdctoryToLocalPath(rootDirectoryPath, SystemSettings.D_config + "/");
      
    }catch(Exception e) {
      e.printStackTrace();
      return null;
    }
    return rootDirectoryPath;
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
          if(!file.isDirectory()) {
            file.delete();
          }
        }
      }
    }
    else {
      System.out.println("root directory error");
    }
  }
  
  /*
  private static void copyInnerDirdctoryToLocalPath(String localPath, String innerPath) {
    List<File> fileList;
    List<File> temp = new ArrayList<File>();
    File innerDirectory = new File(innerPath);

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
  */
  private static void copyInnerFileToLocalPath(String localPath, String innerPath, String filName){
    File innerFile = new File(innerPath + filName);
    File localFile = new File(localPath + filName);
    
    if(!localFile.exists()){
      try{
        Files.copy(innerFile.toPath(), localFile.toPath());
      }
      catch(Exception e) {
        e.printStackTrace();
      }
    }
  }
  
}
