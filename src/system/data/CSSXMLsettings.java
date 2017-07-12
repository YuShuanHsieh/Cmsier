package system.data;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CSSXMLsettings {

  private String name;
  private String headerColor;
  private String titleColor;
  private String subTitleColor;
  private String mainColor;
  private String contentColor;
  private String frameColor;
  
  public void setName(String name){
    this.name = name;
  }
  
  public void setHeaderColor(String headerBackground){
    this.headerColor = headerBackground;
  }
  
  public void setTitleColor(String titleColor){
    this.titleColor = titleColor;
  }
  
  public void setSubTitleColor(String subTitleColor){
    this.subTitleColor = subTitleColor;
  }
  
  public void setMainColor(String mainColor){
    this.mainColor = mainColor;
  }
  
  public void setContentColor(String contentColor){
    this.contentColor = contentColor;
  }
  
  public void setFrameColor(String frameColor){
    this.frameColor = frameColor;
  }
 
  @XmlElement
  public String getName(){
    return this.name;
  }
  
  @XmlElement
  public String getHeaderColor(){
    return this.headerColor;
  }
  
  @XmlElement
  public String getTitleColor(){
    return this.titleColor;
  }
  
  @XmlElement
  public String getSubTitleColor(){
    return this.subTitleColor;
  }
  
  @XmlElement
  public String getMainColor(){
    return this.mainColor;
  }
  
  @XmlElement
  public String getFrameColor(){
    return this.frameColor;
  }
  
  @XmlElement
  public String getContentColor(){
    return this.contentColor;
  }
  
  
}
