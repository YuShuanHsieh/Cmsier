package data;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

// This class collect the pages.
public class Data {

  private List<SetPage> data;
  private String currentPagePath = "";
  
  public Data() {
    data = new LinkedList<SetPage>();
  }
  
  public void createTestData() {
    SetPage root1 = new SetPage("root1");
    SetPage root2 = new SetPage("root2");
    SetPage root3 = new SetPage("root3");
    
    SimplePage data1 = new SimplePage("data1.html");
    data1.setPageContent("data1.html");
    SimplePage data2 = new SimplePage("data2.html");
    data2.setPageContent("data2.html");
    SimplePage data3 = new SimplePage("data3.html");
    data3.setPageContent("data3.html");
    SimplePage data4 = new SimplePage("data4.html");
    data4.setPageContent("data4.html");
    
    root2.setChild(root3);
    
    root1.AddPage(data1);
    root1.AddPage(data2);
    root2.AddPage(data3);
    root3.AddPage(data4);
    
    data.add(root1);
    data.add(root2);
  }
  
  public void setCurrentPageLocalPath(String path) {
    this.currentPagePath = path;
  }
  
  public String getCurrentPageLocalPath() {
    return this.currentPagePath;
  }
  
  public List<SetPage> getList() {
    return this.data;
  }
  
  //  Retrieve data from local path.
  
  public void retrieveData() {
   String defaultPathURL = "./page/";
   retrieveDataFromPath(defaultPathURL, null);
  }
  
  public void retrieveDataFromPath(String pathURL, SetPage setPage) {
    File curentDirectory = new File(pathURL);
    for(File file : curentDirectory.listFiles()) {
      if(file.isDirectory()) {
        SetPage newSetPage = new  SetPage(file.getName());
        if(setPage != null) {
          setPage.setChild(newSetPage);
        }
        else {
          this.data.add(newSetPage);
        }
        retrieveDataFromPath(pathURL + file.getName() + "/", newSetPage);
      }
      else if(file.getName().endsWith(".html")) {
        SimplePage newSimplePage = new SimplePage(file.getName());
        try(InputStream input = new FileInputStream(pathURL + file.getName())) {
          int byteData = 0;
          String stringData = "";
          
          // Store to data content
          while(byteData != -1){
            byteData = input.read();
            stringData = stringData + (char)byteData;
          };
          
          newSimplePage.setPageContent(stringData);
          setPage.AddPage(newSimplePage);
        }
        catch(IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  public boolean isExistingPage(int type, String fileName) {
    
    List<SetPage> pageList = data;
    List<SetPage> temp = new LinkedList<SetPage>();
    
    while(!pageList.isEmpty()) {
      for(SetPage setPage : pageList) {
        if(setPage.getChild() != null) {
          temp.add(setPage.getChild());
        }
      
        switch(type){
         case 1:
           if(setPage.getName().equals(fileName)){
             return true;
           }
           break;
         
         case 2:
           for(SimplePage simplePage : setPage.getPageList()){
             if(simplePage.getName().equals(fileName)){
               return true;
             }
           }
           break;
         default:
           break;
        }
      }
      pageList = temp.stream().collect(Collectors.toList());
      temp.clear();
    }
    
    return false;
  }
}
