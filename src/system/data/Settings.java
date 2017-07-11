package system.data;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import system.data.SettingItem;

@XmlRootElement(name = "Settings")
public class Settings {

  String title;
  String subTitle;
  String publish;
  String localPath;
  String layout;
  
  @XmlElementWrapper(name = "Menu")
  @XmlElement(name = "MenuItem")
  List<SettingItem> menu;
  
  public void setmenuList(List<SettingItem> menu) {
    this.menu = menu;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }

  public void setSubTitle(String subTitle) {
    this.subTitle = subTitle;
  }
  
  public void setPublish(String publish) {
    this.publish = publish;
  }
  
  public void setLocalPath(String localPath) {
    this.localPath = localPath;
  }
  
  public void setLayout(String layout) {
    this.layout = layout;
  }
  
  @XmlElement
  public String getTitle() {
    return this.title;
  }
  
  @XmlElement
  public String getSubTitle() {
    return this.subTitle;
  }
  
  @XmlElement
  public String getPublish() {
    return this.publish;
  }
  
  @XmlElement
  public String getLocalPath() {
    return this.localPath;
  }
  
  @XmlElement
  public String getLayout() {
    return this.layout;
  }
  
  public List<SettingItem> getMenu() {
    return this.menu;
  }
  
  public void addItemToMenu(SettingItem menuItem) {
    for(SettingItem existingItem : menu) {
      if(existingItem.getTargetURL().equals(menuItem.getTargetURL())) {
       existingItem.setName(menuItem.getName());
       return; 
      }
    }
    menu.add(menuItem);
  }
}
