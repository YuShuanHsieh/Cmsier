package system.data;

import java.util.HashMap;

@SuppressWarnings("serial")
public class PageCollection extends HashMap<String, SinglePage> {

  public Boolean isPageNameExist(String newPageName) {
    
    for(String pageName : this.keySet()) {
      if(pageName.equals(newPageName)) return true;
    }
    return false;
  }
  
  public Boolean addNewPage(SinglePage singlePage) {
    if(isPageNameExist(singlePage.getName())) {
      return false;
    }
    else {
      this.put(singlePage.getName(), singlePage);
      return true;
    }
  }
  
  public Boolean removePage(SinglePage targetPage) {
    if(this.containsValue(targetPage)) {
      this.remove(targetPage.getName());
      return true;
    }
    return false;
  }
  
  public Boolean has(String fileName) {
    return this.containsKey(fileName);
  }

}
