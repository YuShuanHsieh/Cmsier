package unitTest;

import org.junit.Test;
import model.utility.DataHelpers.CSSDataHelper;
import system.data.CSSXMLsettings;
import org.junit.Before;
import static org.junit.Assert.assertTrue;

public class CSSDataHelperTest {
  
  private CSSDataHelper testHelper;
  private CSSXMLsettings result;
  private CSSXMLsettings testData = new CSSXMLsettings();
  
  @Before
  public void setUp() {
    testHelper = new CSSDataHelper("/Users/yu-shuan/Documents/CMS/");
    testData.setName("writeTest");
    testData.setHeaderColor("#F2F2F2");
    testData.setTitleColor("#F2F2F2");
    testData.setSubTitleColor("#F2F2F2");
    testData.setMainColor("#F2F2F2");
    testData.setContentColor("#F2F2F2");
    testData.setFrameColor("#F2F2F2");
  } 
  
  @Test
  public void readTest() {
    result = testHelper.read("blue");
    assertTrue(result.getName().equals("blue"));
  }
  
  @Test
  public void readDefaultTest() {
    result = testHelper.read("test");
    assertTrue(result.getHeaderColor().equals("#000000"));
  }
  
  @Test
  public void writeTest() {
    assertTrue(testHelper.write(testData));
  }

}
