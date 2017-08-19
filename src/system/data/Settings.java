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
  String footer;
  
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
  
  public void setFooter(String footer) {
    this.footer = footer;
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
  public String getFooter() {
    return this.footer;
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
  
  public Boolean isLocalPathExist() {
    if(localPath.trim().equals("") || localPath == null) {
      return false;
    }
    return true;
  }
  
  public Boolean addItemToMenu(String customizedName, SinglePage page) {
    for(SettingItem menuItem :menu) {
      if(menuItem.getFileName().equals(page.getName())) {
        return false;
      }
    }
    
    SettingItem newMenuItem = new SettingItem();
    newMenuItem.setFileName(page.getName());
    newMenuItem.setName(customizedName);
    newMenuItem.setTargetURL("./" + page.getDirectory() + "/" + page.getName());
    
    menu.add(newMenuItem);
    return true;
  }
  
  public Boolean removeItemFromMenu(SinglePage targetPage) {
    for(SettingItem menuItem :menu) {
      if(menuItem.getFileName().equals(targetPage.getName())) {
        menu.remove(menuItem);
        return true;
      }
    }
    return false;
  }
  
  public String getMenuItemName(String fileName) {
    for(SettingItem menuItem :menu) {
      if(menuItem.fileName.equals(fileName)) {
        return menuItem.name;
      }
    }
    
    return null;
  }
  
}
