/*
 * This class represents a simple page that has customized content.
*/
package system.data;

public class SimplePage extends Page {

  private String pageName = "";
  private String pageContent = "";
  private SetPage parent;
  boolean changeState = false;
  
  public SimplePage(String name) {
    super(name);
    this.pageName = name;
  }
  
  public void setParent(SetPage parent) {
    this.parent = parent;
  }
  
  public SetPage getParent() {
    return this.parent;
  }
  
  public String getPageName(){
    return this.pageName;
  }
  
  public void setPageName(String pageName){
	this.pageName = pageName;
  }
  
  public String getPageContent(){
    return this.pageContent;
  }
	  
  public void setPageContent(String pageContent){
    this.pageContent = pageContent;
  }
  
  public void setChangeState(boolean changeState) {
    this.changeState = changeState;
  }
  
  public boolean getChangeState() {
    return this.changeState;
  }
		
}
