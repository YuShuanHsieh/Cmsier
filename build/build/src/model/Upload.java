package model;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import data.SetPage;
import data.SimplePage;
import controller.Controller;

public class Upload implements Model {	
  private final FTPClient ftp;
  private final String host = "files.000webhost.com";
  private final String username = "cherriesweb";
  private final String password = "abcd3350368";
  private Controller controller;
	
  public Upload() {
    ftp = new FTPClient();
  }
  
  @Override
  public void init(){
    
  }
  
  @Override
  public void setController(Controller controller) {
    this.controller = controller;
  }
	
  public void connect() {
    try{
      ftp.connect(host, 21);
      ftp.login(username, password);
      System.out.println(ftp.getReplyString());
    } 
    catch(IOException e){
        e.printStackTrace();
    }
  }
  
  public String getContent(String fileName) {
    String content = "";
    try{
      InputStream input = ftp.retrieveFileStream(fileName);
      content = IOUtils.toString(input, StandardCharsets.UTF_8);
    }
    catch(IOException e) {
      System.err.println(e.getMessage());
    }
	return content;
  }
	
  public void disconnect(){
    try{
      ftp.logout();
      ftp.disconnect();
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }
	
  public void download(){
    String initialPath = "page/";
    downloadFile(initialPath);
  }
	
  public void upload(){
    String initialPath = "./page/";
	ftp.enterLocalPassiveMode();
	uploadFile(initialPath);
  }
	
  private void uploadFile(String pathName){
    File directory = new File(pathName);
	File[] files = directory.listFiles();
	try{
	  // Warning: the path is a problem.
	  System.out.println(ftp.cwd("/" + pathName));
	  if(ftp.cwd("/" + pathName) == 550){
	    ftp.makeDirectory("/" + pathName);
	  }
	}
	catch(Exception e){
	  e.printStackTrace();
	}
	for(File file : files){
	  if(file.isDirectory()){
		if(file.getName(  ).matches("[a-zA-Z0-9\\-]*")){
		  uploadFile(pathName + file.getName() + "/");
		}
	  }
	  else{
		try{
		  // Warning: the path is a problem.
		  FileInputStream uploadfile = new FileInputStream(pathName + file.getName());
		  if(!ftp.storeFile("/" + pathName + file.getName(), uploadfile)){ 
		    System.err.println(ftp.getReplyString());
			  System.err.println("upload files error");
		  }		
		}
		catch(Exception e) {
		  e.printStackTrace();
		}
	  }
	}
  }
	
  private void downloadFile(String pathName){
    FTPFile[] files = {};
	  try{
	    //Create a folder if it does not exist in the local path.
	    if(!Files.exists(Paths.get(pathName))){
	      Files.createDirectories(Paths.get(pathName));
	    }
	    SetPage newSetPage = new SetPage();
	    setListsPageValue(pathName, newSetPage);
	    this.controller.getSystemManager().getData().getList().add(newSetPage);
	    files = ftp.listFiles(pathName);
	    
	    for(FTPFile file : files) {
	      if(file.isDirectory()){
	        // filter directories related system default setting.(./..)
		      if(file.getName().matches("[a-zA-Z0-9\\-]*")){
		        downloadFile(pathName + file.getName() + "/");
		      }
	      }
	      else if(file.isFile()){
	        try{
		        OutputStream storedfile = new FileOutputStream(pathName + file.getName());
		        ftp.retrieveFile(pathName + file.getName(), storedfile);
		        SimplePage newSimplePage= new SimplePage(file.getName());
		        newSetPage.AddPage(newSimplePage);
		      }
		      catch(IOException e){
		        e.printStackTrace();
		      }
	      }
	    }
	  }
	  catch(IOException e){
	    e.printStackTrace();
	  }
  }
	
  public void createNewPage(String path, String pageName){
    try{
      Files.createFile(Paths.get(path + pageName));
    }
    catch(Exception e) {
      System.err.println("This file name has existed.");
    }
  }
    
  private void setListsPageValue(String pathName, SetPage newListsPage){
    String[] folderArrays = pathName.split("/");
    newListsPage.setListsPageName(folderArrays[folderArrays.length - 1]);
    
    if(folderArrays.length - 2 < 0){
      return;
    }
    
    for(SetPage setPage: this.controller.getSystemManager().getData().getList()) {
      if(setPage.getName() == folderArrays[folderArrays.length - 2]) {
        newListsPage.setParent(setPage);
        return;
      }
    }
  }
	
  public static void main(String[] args) {
    // test area.
  }
  
}
