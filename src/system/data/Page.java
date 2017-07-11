package system.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public abstract class Page {

  protected String name;
  
  public Page(){
    
  }
  
  public Page(String name){
    this.name = name;
  }
  
  @XmlElement(name="name")
  public String getName(){
    return this.name;
  }
  
  @Override
  public String toString()  {
      return this.name;
  }
  
}
