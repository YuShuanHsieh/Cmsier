package system.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CategoryPage {

  private String fileName;
  private String directory;
  private String title;
  private String subtitle;
  
  CategoryPage(){
    
  }
  
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
  
  @XmlElement
  public String getFileName() {
    return this.fileName;
  }
  
  public void setDirectory(String directory) {
    this.directory = directory;
  }
  
  @XmlElement
  public String getDirectory() {
    return this.directory;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }
  
  @XmlElement
  public String getTitle() {
    return this.title;
  }
  
  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }
  
  @XmlElement
  public String getSubTitle() {
    return this.subtitle;
  }
  
}
