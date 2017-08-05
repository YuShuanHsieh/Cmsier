package system.data;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MenuItem")
public class SettingItem {

  String name;
  String targetURL;
  String fileName;

  public void setName(String name) {
    this.name = name;
  }
  
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
  
  public void setTargetURL(String targetURL) {
    this.targetURL = targetURL;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getFileName() {
    return this.fileName;
  }
  
  public String getTargetURL() {
    return this.targetURL;
  }
 
}
