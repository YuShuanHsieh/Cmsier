package model.utility;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import system.SystemSettings;
import system.data.Settings;
import org.jsoup.nodes.Element;

public class ImageFilter {

  private Document content;
  private String localPath;
  
  public ImageFilter(String HTMLcontent, Settings settings) {
    this.content = Jsoup.parse(HTMLcontent);
    this.localPath = settings.getLocalPath();
  }
  
  public boolean store() {
    Elements images = getImage();
    for(Element image : images) {
      String url = image.attr("src").replace("file://", "");
      try {
        storeImageFile(url);
      }
      catch(IOException exception) {
        exception.printStackTrace();
        return false;
      }
    }
    return true;
  }
  
  private boolean storeImageFile(String imgFilePath) throws IOException {
    File imgFile = new File(imgFilePath);
    File uploadImgFile = getUploadFile(imgFilePath);
    if(!uploadImgFile.exists() && imgFile.exists()) {
      Files.copy(imgFile.toPath(), uploadImgFile.toPath());
      return true;
    }
    return false;
  }
  
  private File getUploadFile(String imgFilePath) {
    return new File(localPath + SystemSettings.uploadPath + getFileName(imgFilePath));
  }
  
  private String getFileName(String imgFilePath) {
    String[] splitedStrings = imgFilePath.split("/");
    return splitedStrings[splitedStrings.length-1];
  }
  
  
  private Elements getImage() {
    return content.getElementsByTag("img");
  }
  
public String imageStore(String content) {
    
    String clip = "<img src=\"file://";
    StringBuilder stringBuilder = new StringBuilder(content);
    int startIndex = 0;
    
    while(startIndex != -1){
      
      startIndex = stringBuilder.indexOf("<img", startIndex);
      if(startIndex != -1){
        int lastIndex = stringBuilder.indexOf("\" style=\"max-width: 100%;\">", startIndex + 1);
        stringBuilder.substring(startIndex, lastIndex);
        String filePah = stringBuilder.substring(startIndex + clip.length(), lastIndex).toString();
      
        String[] splitFile = filePah.split("/");
        String fileName = splitFile[splitFile.length -1];
        
        File originalFile = new File(filePah);
        String imgUploadPath = localPath + SystemSettings.D_upload + "/";
        File newFile = new File(imgUploadPath + fileName);
        
        try{
          if(!newFile.exists()){
            Files.copy(originalFile.toPath(), newFile.toPath());
          }
        } catch(Exception e) {
          e.printStackTrace();
          return null;
        }
        stringBuilder = stringBuilder.replace(startIndex + clip.length(), lastIndex, newFile.getAbsolutePath());
        /** find the last index again because of inserting string. */
        lastIndex = stringBuilder.indexOf("\">", startIndex + 1);
        startIndex = lastIndex;
      }
    }
    
    return stringBuilder.toString();
  }
  
  
}
