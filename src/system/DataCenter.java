/**
 * A collection of all required data.
 * @author Yu-Shuan
 */
package system;

import javafx.stage.Stage;
import system.data.CSSXMLsettings;
import system.data.Category;
import system.data.CategoryCollection;
import system.data.CategoryPage;
import system.data.PageCollection;
import system.data.SettingItem;
import system.data.Settings;
import system.data.SinglePage;
import model.utility.DataHelpers.CategoryDataHelper;
import model.utility.DataHelpers.PageDataHelper;
import model.utility.DataHelpers.SettingDataHelper;

/**
 * A collection of all required data of Web site.
 * It serves functions combined with data objects and data helpers.
 *  */

public class DataCenter {
  
  /** it is used to calculate the size of view components. */
  private Stage window;
  
  private SettingDataHelper settingHelper;
  private PageDataHelper pageHelper;
  private CategoryDataHelper categoryHelper;
  
  private PageCollection pageCollection;
  private Settings settings;
  private CSSXMLsettings cssSettings;
  private CategoryCollection categories;
  
  private String localPreviewPagePath = ""; 
  
  public DataCenter(Stage window) {
    this.window = window;
  }
  
  public void init() {
    settingHelper = new SettingDataHelper();
    settings = settingHelper.read(null);
    
    pageHelper = new PageDataHelper(settings.getLocalPath());
    categoryHelper = new CategoryDataHelper(settings.getLocalPath());
    
    categories = categoryHelper.read(null);
    pageCollection = pageHelper.read(null);
    
    organize();
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
  
  public PageCollection getPageCollection() {
    return this.pageCollection;
  }
  
  public void setPageCollection(PageCollection data) {
    this.pageCollection = data;
  }
  
  public CategoryCollection getCategory() {
    return this.categories;
  }
  
  public void setCategory(CategoryCollection categories) {
    this.categories = categories;
  }
  
  public void updateCategory() {
    categories = categoryHelper.read(null);
  }
  
  public Boolean isCategoryExist(String categoryName) {
    if(categories.containsKey(categoryName)) {
      return true;
    }
    else {
      return false;
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
  
  public void updateSetting() {
    settingHelper.write(settings);
  }
  
  
  public void removePage(SinglePage page) {
    Category category = page.getCategory();
    if(page.getCategory() != null) {
      category.removePageFromList(page.getName());
      categoryHelper.write(category);
    }
    
    if(pageCollection.removePage(page)) {
      pageHelper.delete(page);
    }
   
    if(settings.removeItemFromMenu(page)) {
      settingHelper.write(settings);
    }
  }
  
  public void addPage(SinglePage page) {
    if(pageCollection.addNewPage(page)) {
      pageHelper.write(page.getName());
    }
  }
  
  public void updatePage(SinglePage page) {
    pageHelper.write(page);
  }
  
  public void addItem(String itemName, SinglePage page) {
    if(settings.addItemToMenu(itemName, page)) {
      settingHelper.write(settings);
    }
  }
  
  public void removeItem(SinglePage page) {
    if(settings.removeItemFromMenu(page)) {
      settingHelper.write(settings);
    }
  }
  
  public void addCategoryPage(SinglePage page, Category category) {
    category.addPageToList(page);
    categoryHelper.write(category);
  }
  
  public void removeCategoryPage(SinglePage page) {
    Category category = page.getCategory();
    category.removePageFromList(page.getName());
    categoryHelper.write(category);
  }
  
  public void addNewCategory(Category newCategory) {
    if(categories.addCategory(newCategory)) {
      categoryHelper.write(newCategory);
    }
  }
  
  public void removeCategory(Category newCategory) {
    if(categories.removeCategory(newCategory)) {
      categoryHelper.delete(newCategory.getName());
    }
  }
  
  
  
  
}
