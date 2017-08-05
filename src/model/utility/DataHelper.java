package model.utility;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import system.data.PageCollection;
import system.data.SinglePage;

public class DataHelper {

  public static PageCollection retrievePage(String localPath) {
    PageCollection pageCollection = new PageCollection();
    String[] directoriesName = {"default", "page"}; 
    
    for(String directoryName : directoriesName) {
      File directory = new File(localPath + "edit/" + directoryName);
      File[] pageFiles = directory.listFiles(new PageNameFilter());
      
      try {
        for(File pageFile : pageFiles) {
          SinglePage page = new SinglePage(pageFile.getName());
          page.setContent(FileUtils.readFileToString(pageFile, "UTF-8"));
          page.setDirectory(directoryName);
          pageCollection.put(pageFile.getName(), page);
        }
      }
      catch(IOException exception) {
        exception.printStackTrace();
      }
      
    }
    return pageCollection;
  }
  
  public static void storePageToFile(String editLocalPath,SinglePage targetPage) {
    String pagePath = editLocalPath + targetPage.getDirectory() + "/" + targetPage.getName();
    File pageFile = new File(pagePath);
    try {
      if(!pageFile.exists()) {
        pageFile.createNewFile();
      }
      FileUtils.writeStringToFile(pageFile, targetPage.getContent(), "UTF-8");
    }
    catch(IOException exception) {
      exception.printStackTrace();
    } 
  }
  
  public static void deletePageFromFile(String editLocalPath,SinglePage targetPage) {
    String pagePath = editLocalPath + "edit/page/" + targetPage.getName();
    File pageFile = new File(pagePath);
    if(pageFile.exists()) {
      pageFile.delete();
    }
  }
  
  public static void deleteCategoryFile(String LocalPath, String categoryName) {
    String filePath = LocalPath + "category/" + categoryName + ".xml";
    File file = new File(filePath);
    if(file.exists()) {
      file.delete();
    }
  }
 
  private static class PageNameFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String name) {
      if(name.contains(".DS_Store")) return false;
      else return true;
    }

  }
  
}
