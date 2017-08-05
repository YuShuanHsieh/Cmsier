package system.data;

public class SinglePage {
  
  private String name = "";
  private String title = "";
  private String directory = "";
  private String subtitle = "";
  private String content = "";
  private Category category = null;
  private Boolean isOnMenu = false;
  
  public SinglePage() {
  }
  
  public SinglePage(String pageName) {
    this.name = pageName;
  }
  
  public SinglePage(String pageName, String title) {
    this.name = pageName;
    this.title = title;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getTitle() {
    return this.title;
  }
  
  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }
  
  public String getSubtitle() {
    return this.subtitle;
  }
  
  public void setDirectory(String directory) {
    this.directory = directory;
  }
  
  public String getDirectory() {
    return this.directory;
  }
  
  public void setContent(String content) {
    this.content = content;
  }
  
  public String getContent() {
    return this.content;
  }
  
  public void setCategory(Category category) {
    this.category = category;
  }
  
  public Category getCategory() {
    return this.category;
  }
  
  
  public void setIsOnMenu(Boolean value) {
    this.isOnMenu = value;
  }
  
  public Boolean isOnMenu() {
    return this.isOnMenu;
  }
  
  public String toString() {
    return this.name;
  }
  
  
}
