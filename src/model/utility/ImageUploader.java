package model.utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import system.SystemSettings;
import system.data.Settings;
/**
  The class deals with the image uploading and the path modification as users save the content.
  First it searches the file paths and copy the image file to specified folder.(The default name of folder is Upload.)
  Then it replaces the original local path to the new path of uploaded file.
*/
public class ImageUploader {
  private StringBuilder content;
  private String imgUploadFilePath = "";
  private final String imgUploadPath;
  private final String replacedMark = "file://";
  private final String imgTagStart = "<img";
  private final String imgTagEnd = ">";
  private final int max;
  private int start = 0;
  private int end = 0;
    
  public ImageUploader(String content, Settings settings) {
    this.content = new StringBuilder(content);
    this.imgUploadPath = settings.getLocalPath() + SystemSettings.uploadPath;
    this.max = content.length();
  }
  
  public String run() {
    while(start < max) {
      start = getStartIndex(start);
      end = getEndIndex(start);
      
      if(end == -1 || start == -1)
        break;
      
      storeFile(getImageFileString(start, end));
      replaceImgFilePath(start);
      start = end + 1;
    }
    return content.toString();
  }
  
  private int getStartIndex(int start) {
    if(start < 0 || start == content.length()) {
      return 0;
    }
    else {
      return content.indexOf(imgTagStart, start);
    }
  }
  
  private int getEndIndex(int start) {
    if(start < 0 || start == content.length()) {
      return 0;
    }
    else {
      return content.indexOf(imgTagEnd, start);
    }
  }
  
  private String getImage(int start, int end) {
    return content.substring(start, end);
  }
  
  private String getImageFileString(int start, int end) {
    return getImageFileString(getImage(start, end));
  }
  
  private String getImageFileString(String imgContent) {
    int i;
    String[] splited = imgContent.split("\"");
    for(i=0; i < splited.length; i++ ) {
      if(splited[i].contains("file")) {
        return splited[i];
      }
    }
    
    return null;
  }
  
  private void storeFile(String imgFileString) {
    storeFile(getImageFile(imgFileString), getUploadFile(imgFileString));
  }
  
  private void storeFile(File imageFile, File uploadFile) {
    try{
      if(!uploadFile.exists() && imageFile.exists()){
        Files.copy(imageFile.toPath(), uploadFile.toPath());
      }
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  
  private File getImageFile(String imgFileString) {
    return new File(imgFileString.replace("file://", ""));
  }
  
  private File getUploadFile(String imgFileString) {
    String[] splited = imgFileString.split("/"); 
    imgUploadFilePath = imgUploadPath+splited[splited.length-1];
    return new File(imgUploadFilePath);
  }
  
  private void replaceImgFilePath(int start) {
    int startIndex = getReplacedStartIndex(start);
    int endIndex = getReplacedEndIndex(startIndex);
    this.content.replace(startIndex, endIndex, imgUploadFilePath);
    
    end = getReplacedEndIndex(start);
  }
  
  private int getReplacedStartIndex(int start) {
    if(start < 0 || start == content.length()) {
      return 0;
    }
    else {
      return content.indexOf(replacedMark, start) + replacedMark.length();
    }
  }
  
  private int getReplacedEndIndex(int start) {
    if(start < 0 || start == content.length()) {
      return 0;
    }
    else {
      return content.indexOf("\"", start);
    }
  }
  
}
