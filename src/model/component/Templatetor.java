package model.component;

import java.io.File;
/*
 * It only supports files with characters.
*/
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Templatetor {

  private File templateFile;
  private File targetFile;
  private String keyWord = "";
  private Map<String, String> penddingContent;
  private char startNotation = '{'; //default setting
  private char endNotation = '}'; //default setting
    
  public Templatetor(String templateFilePath, String targetFilePath) throws IOException{
    
    templateFile = new File(templateFilePath);  
    targetFile = new File(targetFilePath);
    penddingContent = new HashMap<String, String>();
    
    if(!templateFile.exists()) {
      throw new NullPointerException("Cannot find template file.");
    }
    
    if(!targetFile.exists()) {
      targetFile.createNewFile();
    }
  }
  
  public boolean setMarkNotation(char startNotation, char endNotation) {
    if(startNotation == endNotation){
      return false;
    }
    this.startNotation = startNotation;
    this.endNotation = endNotation; 
    
    return true;
  }
  
  public boolean addKeyAndContent(String keyWord, String content) {
    String wrapKeyWord = Character.toString(startNotation) + keyWord + Character.toString(endNotation);
    if(!penddingContent.containsKey(wrapKeyWord)) {
      penddingContent.put(wrapKeyWord, content);
      return true;
    }
    else {
      return false;
    }
  }
  
  public boolean run() throws IOException {
    
    int charNumber = 0;
    boolean recordState = false;
    FileWriter writer = new FileWriter(targetFile);
    FileReader fileReader = new FileReader(templateFile);
    
    while(charNumber != -1) {
    
      charNumber = fileReader.read();
      
      if((char)charNumber == startNotation) {
        recordState = true;
      }
      
      if(charNumber!= -1 && !recordState) {
        writer.write(charNumber);
      }
      
      if(recordState) {
        keyWord = keyWord + Character.toString((char)charNumber);
      }
      
      if((char)charNumber == endNotation) {
        recordState = false;
        append(writer);
      }
    }
    writer.close();
    fileReader.close();
    
    return true;
  }
  
  private void append(FileWriter writer)throws IOException {
    if(penddingContent.containsKey(keyWord)) {
      writer.write(penddingContent.get(keyWord));
    }
    keyWord = "";
  } 
}
