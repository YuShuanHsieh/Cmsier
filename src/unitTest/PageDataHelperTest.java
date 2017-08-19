package unitTest;

import org.junit.Test;
import model.utility.DataHelpers.PageDataHelper;
import system.data.PageCollection;
import system.data.SinglePage;
import org.junit.Before;
import static org.junit.Assert.assertTrue;

import java.io.File;

public class PageDataHelperTest {

  private String testLocalPath = "/Users/yu-shuan/Documents/CMS/";
  private PageDataHelper helper;
  private SinglePage testPage;
  private PageCollection testCollection;
  private String testPageName = "unitTest";
  
  @Before
  public void setUp() {
    helper = new PageDataHelper(testLocalPath);
    testPage = new SinglePage("test.html");
    testPage.setDirectory("page");
    testPage.setContent("Unit test content.");
  }
  
  @Test
  public void readTest() {
    testCollection = helper.read(null);
    
    assertTrue(testCollection.containsKey("test2.html"));
    assertTrue(testCollection.containsKey("test3.html"));
    assertTrue(!testCollection.containsKey("test4.html"));
  }
  
  @Test
  public void writeNewPageTest() {
    assertTrue(helper.write(testPageName));
    
    File result = new File(testLocalPath + "edit/page/" + testPageName + ".html");
    assertTrue(result.exists());
  }
  
  @Test
  public void writeExistingPageTest() {
    assertTrue(helper.write(testPage));
  }
  
  @Test
  public void deleteTest() {
    assertTrue(helper.delete(testPageName));
    
    File result = new File(testLocalPath + "edit/page/" + testPageName + ".html");
    assertTrue(!result.exists());
  }
  
}
