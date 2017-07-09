/*
 * 15/6/2017 Comment:
 * This class contains a set of Simple class.
 * 
 */
package data;
import java.util.LinkedList;
import java.util.List;

public class SetPage extends Page {

  private SetPage parent = null;
  private SetPage child = null;
  private List<SimplePage> simplePageList;
  private String listsPageName;
  
  public SetPage(){
    super("");
    simplePageList = new LinkedList<SimplePage>();
  }
  
  public SetPage(String listsPageName){
    super(listsPageName);
    this.listsPageName = listsPageName;
    simplePageList = new LinkedList<SimplePage>();
  }
  
  public void setChild(SetPage child){
    this.child = child;
    child.setParent(this);
  }
  
  public void setParent(SetPage parent){
    this.parent = parent;
  }
  
  public SetPage getChild(){
    return this.child;
  }
  
  public SetPage getParent(){
    return this.parent;
  }
  
  public String getListsPageName() {
    return this.listsPageName;
  }
  
  public void setListsPageName(String listsPageName) {
    this.name = listsPageName;
    this.listsPageName = listsPageName;
  }
  
  public List<SimplePage> getPageList(){
    return this.simplePageList;
  }
  
  public void AddPage(SimplePage simplePage){
    if(simplePageList.indexOf(simplePage) == -1) {
      simplePageList.add(simplePage);
      simplePage.setParent(this);
    }
    else{
      System.err.println("This page has existed: " + simplePage.getPageName());
    }
  }
  
  public void RemovePage(SimplePage simplePage){
    int index = simplePageList.indexOf(simplePage);
    if(index != -1) {
      simplePageList.remove(index);
    }
    else{
      System.err.println("This page does not exist" + simplePage.getPageName());
    }
  }
 
}
