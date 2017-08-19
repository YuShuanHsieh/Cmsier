package model.utility.DataHelpers;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import system.SystemSettings;
import system.data.PageCollection;
import system.data.SinglePage;

/**
 * Deal with the page collection and single page.
 * Should notice the usage of write methods. 
 *  */

public class PageDataHelper implements DataHelperBase<PageCollection> {

  private String localPagePath;
  
  public PageDataHelper(String localPath) {
    this.localPagePath = localPath + SystemSettings.pagePath;
  }
  
  public PageCollection read(String name) {
    PageCollection pageCollection = new PageCollection();
    
    /** All edited pages will be put into default & page folders.*/
    String[] directoriesName = {"default", "page"}; 
    
    for(String directoryName : directoriesName) {
      File directory = new File(localPagePath + directoryName);
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
  
  public Boolean delete(SinglePage page) {
    return delete(page.getName());
  }
  
  public Boolean delete(String pageName) {
    String pagePath = localPagePath + "page/" + pageName + ".html";
    File pageFile = new File(pagePath);
    if(pageFile.exists()) {
      pageFile.delete();
      return true;
    }
    return false;
  }
  
  public Boolean write(PageCollection setting) {
    return null;
  }
  
  /** use to write a new page to file. */
  public Boolean write(String pageName) {
    String newPageFilePath = localPagePath + "page/" + pageName + ".html";
    File newPageFile = new File(newPageFilePath);
    
    try {
      if(!newPageFile.exists()) {
        newPageFile.createNewFile();
      }
      return true;
    }
    catch(IOException exception) {
      exception.printStackTrace();
      return false;
    }
  }
  
  /** use to write a existing page to file. */
  public Boolean write(SinglePage newPage) {
    String pagePath = localPagePath + newPage.getDirectory() + "/" + newPage.getName();
    File pageFile = new File(pagePath);
    try {
      FileUtils.writeStringToFile(pageFile, newPage.getContent(), "UTF-8");
      return true;
    }
    catch(IOException exception) {
      exception.printStackTrace();
      return false;
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
