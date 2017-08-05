package system.data;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FtpSettings {

  private String host;
  private String account;
  private String password;
  
  public void setHost(String host){
    this.host = host;
  }
  
  public void setAccount(String account){
    this.account = account;
  }
  
  public void setPassword(String password){
    this.password = password;
  }
  
  @XmlElement
  public String getHost(){
    return this.host;
  }
  
  @XmlElement
  public String getAccount(){
    return this.account;
  }
  
  @XmlElement
  public String getPassword(){
    return this.password;
  }
  
}
