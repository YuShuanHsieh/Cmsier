/**
 * A collection of all required data.
 * @author Yu-Shuan
 */
package system;

import java.util.HashMap;
import java.util.Map;
import javafx.stage.Stage;
import model.utility.XmlHelper;
import system.data.CSSXMLsettings;
import system.data.Category;
import system.data.CategoryPage;
import system.data.PageCollection;
import system.data.SettingItem;
import system.data.Settings;

public class DataCenter {
  
  /** it is used to calculate the size of view components. */
  private Stage window;
  
  private PageCollection pageCollection;
  private Settings settings;
  private CSSXMLsettings cssSettings;
  private Map<String, Category> categories;
  private String localPreviewPagePath = ""; 
  
  public DataCenter(Stage window) {
    this.window = window;
    categories = new HashMap<String, Category>();
  }

  public Stage getWindow() {
    return this.window;
  }
  
  public void setlocalPreviewPagePath(String localPreviewPagePath) {
    this.localPreviewPagePath = localPreviewPagePath;
  }
  
  public String getlocalPreviewPagePath() {
    return this.localPreviewPagePath;
  }
  
  public void setCSSSettings(CSSXMLsettings cssSettings) {
    this.cssSettings = cssSettings;
  }
  
  public CSSXMLsettings getCSSSettings() {
    return this.cssSettings;
  }
  
  public Settings getSettings() {
    return this.settings;
  }
  
  public void setSettings(Settings settings) {
    this.settings = settings;
  }
  
  public PageCollection getData() {
    return this.pageCollection;
  }
  
  public void setData(PageCollection data) {
    this.pageCollection = data;
  }
  
  public Map<String, Category> getCategory() {
    return this.categories;
  }
  
  public void setCategory(Map<String, Category> categories) {
    this.categories = categories;
  }
  
  public Boolean isCategoryExist(String categoryName) {
    if(categories.containsKey(categoryName)) {
      return true;
    }
    else {
      return false;
    }
  }
  
  public void removePageFromCategory(String fileName) {
    for(Category category : categories.values()) {
      if(category.removePageFromList(fileName)) {
        XmlHelper.writeCategoryToXML(category, settings.getLocalPath()+"category/");
      }
    }
  }

  public void organize() {
    for(SettingItem menuItem : settings.getMenu()) {
      if(pageCollection.containsKey(menuItem.getFileName())) {
        pageCollection.get(menuItem.getFileName()).setIsOnMenu(true);
      }
    }
    
    for(Category categoryItem : categories.values()) {
      for(CategoryPage categoryPage : categoryItem.getPageList()) {
        if(pageCollection.containsKey(categoryPage.getFileName())) {
          pageCollection.get(categoryPage.getFileName()).setTitle(categoryPage.getTitle());
          pageCollection.get(categoryPage.getFileName()).setCategory(categoryItem);
        }
      }
    }
  }
  
   
}
