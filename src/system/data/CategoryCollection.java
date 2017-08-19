package system.data;

import java.util.HashMap;

public class CategoryCollection extends HashMap<String, Category> {
  
  
  public Boolean addCategory(Category category) {
    
    if(!this.containsKey(category.getName())) {
      this.put(category.getName(), category);
      return true;
    }
    else {
      return false;
    }
    
  }
  
  public Boolean removeCategory(Category category) {
    if(this.containsKey(category.getName())) {
      this.remove(category.getName());
      return true;
    }
    else {
      return false;
    }
  }
  
  
}
