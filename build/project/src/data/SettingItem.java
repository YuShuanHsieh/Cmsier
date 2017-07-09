package data;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "MenuItem")
public class SettingItem {

  String name;
  String targetURL;

  public void setName(String name) {
    this.name = name;
  }
  
  public void setTargetURL(String targetURL) {
    this.targetURL = targetURL;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getTargetURL() {
    return this.targetURL;
  }
 
}
