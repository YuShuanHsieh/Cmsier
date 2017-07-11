package system.data;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CSSXMLsettings {

  private String name;
  private String headerBackground;
  private String titleColor;
  private String subTitleColor;
  
  public void setName(String name){
    this.name = name;
  }
  
  public void setHeaderBackground(String headerBackground){
    this.headerBackground = headerBackground;
  }
  
  public void setTitleColor(String titleColor){
    this.titleColor = titleColor;
  }
  
  public void setSubTitleColor(String subTitleColor){
    this.subTitleColor = subTitleColor;
  }
  
  @XmlElement
  public String getName(){
    return this.name;
  }
  
  @XmlElement
  public String getHeaderBackground(){
    return this.headerBackground;
  }
  
  @XmlElement
  public String getTitleColor(){
    return this.titleColor;
  }
  
  @XmlElement
  public String getSubTitleColor(){
    return this.subTitleColor;
  }
  
  
}
