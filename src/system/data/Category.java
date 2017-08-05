package system.data;

import java.util.LinkedList;
import java.util.Optional;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Category {
  
  String name;
  /** Use LinkedList for sorting purpose. */
  LinkedList<CategoryPage> pageList = new LinkedList<CategoryPage>();
  
  public Category() {
    
  }
  
  public Category(String categoryName) {
    this.name = categoryName;
  }
  
  public void setPageList(LinkedList<CategoryPage> pageList) {
    this.pageList = pageList;
  }
  
  @XmlElementWrapper(name = "pageList")
  @XmlElement(name = "pageItem")
  public LinkedList<CategoryPage> getPageList() {
    return this.pageList;
  }
  
  public void setName(String categoryName) {
    this.name = categoryName;
  }
  
  @XmlElement
  public String getName() {
    return this.name;
  }
  
  /** Need to verify existence before adding. */
  public void addPageToList(SinglePage page) {    
    Optional<CategoryPage> element = getElement(page.getName());
    if(element.isPresent()) {
      upldateElement(element.get(), page);
    }
    else {
      addElement(page);
    }
  }  

  public Optional<CategoryPage> getElement(String fileName){
    for(CategoryPage listItem : pageList) {
      if(listItem.getFileName().equals(fileName)) {
       return Optional.of(listItem);
      }
    }
    return Optional.empty();
  }
  
  public void upldateElement(CategoryPage targetElement ,SinglePage page) {
    targetElement.setTitle(page.getTitle());
    targetElement.setDirectory(page.getDirectory());
  }
  
  private void addElement(SinglePage page) {
    CategoryPage newCategoryPage = new CategoryPage();
    newCategoryPage.setFileName(page.getName());
    newCategoryPage.setDirectory(page.getDirectory());
    newCategoryPage.setTitle(page.getTitle());  
    pageList.add(newCategoryPage);
  }
  
  private void removeElement(CategoryPage targetElement) {
    pageList.remove(targetElement);
  }
  
  public Boolean removePageFromList(String filename) {
    Optional<CategoryPage> element = getElement(filename);
    if(element.isPresent()) {
      removeElement(element.get());
      return true;
    }
    else {
      return false;
    }
  }
  
  public void removePageFromList(CategoryPage targetPage) {
    pageList.remove(targetPage);
  }
  
  @Override
  public String toString() {
    return name;
  }
   
}
