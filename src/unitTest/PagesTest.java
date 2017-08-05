package unitTest;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import system.data.SinglePage;
import system.data.Settings;
import system.data.Category;
import system.data.PageCollection;
import model.utility.XmlHelper;
import model.utility.DataHelper;

public class PagesTest {

  private Settings settings;
  private SinglePage testPage;
  private String editlocalPath;
  private PageCollection testCollection;
  private Map<String, Category> categories;
  
  @Before
  public void setUp() {
    settings = XmlHelper.retrieveSettingFromFile();
    testPage = new SinglePage("test.html");
    testPage.setContent("Test content");
    testPage.setDirectory("default");
    editlocalPath = settings.getLocalPath() + "edit/";
    testCollection = DataHelper.retrievePage(editlocalPath);
    categories = XmlHelper.retrieveCatetoryFromXML(settings.getLocalPath());
  }
  
  @Test
  public void retrieveDataFromFileTest() {
    PageCollection pageCollection = DataHelper.retrievePage(editlocalPath);
    assertTrue(pageCollection.size() > 0);
  }
  
  @Test
  public void writeSinglePageToFile() {
    DataHelper.storePageToFile(editlocalPath, testPage);
    File testFile = new File(editlocalPath + testPage.getDirectory() + "/" + testPage.getName());
    assertTrue(testFile.exists());
  }
  
  @Test
  public void isPageNameExistTest() {
    assertTrue(testCollection.isPageNameExist("example.html"));
    assertTrue(!testCollection.isPageNameExist("example1.html"));
  }
  
  @Test
  public void addNewPageTest() {
    SinglePage test = new SinglePage("index.html");
    assertTrue(!testCollection.addNewPage(test));
    
    SinglePage text2 = new SinglePage("index2.html");
    assertTrue(testCollection.addNewPage(text2));
    assertTrue(!testCollection.addNewPage(text2));
  }
  
  @Test
  public void removePageTest() {
    SinglePage test = testCollection.get("text.html");
    assertTrue(testCollection.removePage(test));
    assertTrue(!testCollection.removePage(test));
    
    SinglePage test2 = new SinglePage("index.html");
    assertTrue(!testCollection.removePage(test2));
  }
  
}
