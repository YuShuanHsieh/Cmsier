package model.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import data.Data;
import data.SetPage;
import data.SettingItem;
import data.Settings;
import data.SimplePage;

public class DataHelper {

  /*
   * @param settings the Web site settings from SystemManager class
   * @param simplePage target from current data
   * @return searching result(Optional type)
   */
  public Optional<SettingItem> searchSettingMenuItemBySimplePage(Settings settings, SimplePage simplePage) {
    for(SettingItem settingItem : settings.getMenu()) {
      String[] splitItemName = settingItem.getTargetURL().split("/");
      int itemNameIndex = splitItemName.length - 1;
    
      if(splitItemName[itemNameIndex].equals(simplePage.getName())) {
        return Optional.of(settingItem);
      }
    }
    return Optional.empty();
  }
  
  /*
   * Use this function when data need to be initialized or refreshed.
   */
  public Data retrieveDataFromFiles(String localPath) {
    Data data = new Data();
    String rootDirectory = localPath +  "page/";
    retrieveDataFromFile(data, rootDirectory, null);
    return data;
  }
  
  /*
   * Search a target SimplePage from current data.
   */
  public Optional<SimplePage> searchSimplePageByName(Data data, String simpleName) {
    List<SetPage> setPageList = data.getList();
    List<SetPage> temp = new LinkedList<SetPage>();
    
    while(!setPageList.isEmpty()) {
      for(SetPage setPage :setPageList) {
        if(setPage.getChild() != null) {
          temp.add(setPage.getChild());
        }
        for(SimplePage simplePage : setPage.getPageList()) {
          if(simplePage.getName().equals(simpleName)) {
            return Optional.of(simplePage);
          }
        }
      }
      
      setPageList = temp.stream().collect(Collectors.toList());
      temp.clear();
    }
    
    return Optional.empty();
  }
  
  /*
   * inner function for recursion of retrieve data.
   */
  private static void retrieveDataFromFile(Data data, String directoryPath, SetPage setPage) {
    File directory = new File(directoryPath);
    for(File file : directory.listFiles()) {
      if(file.isDirectory()) {
        SetPage newSetPage = new  SetPage(file.getName());
        if(setPage != null) {
          setPage.setChild(newSetPage);
        }
        else {
          data.getList().add(newSetPage);
        }
        String subDirectoryPath = directoryPath + file.getName() + "/";
        retrieveDataFromFile(data, subDirectoryPath, newSetPage);
      }
      else if(file.getName().endsWith(".html")) {
        SimplePage newSimplePage = new SimplePage(file.getName());
        try(InputStream input = new FileInputStream(directoryPath + file.getName())) {
          int byteData = 0;
          String stringData = "";
          
          // Store to data content
          while(byteData != -1){
            byteData = input.read();
            stringData = stringData + (char)byteData;
          };
          
          newSimplePage.setPageContent(stringData);
          setPage.AddPage(newSimplePage);
        }
        catch(IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
}
