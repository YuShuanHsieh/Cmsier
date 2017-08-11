package unitTest;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;
import model.GenerateModel;
import model.GenerateModel.GENERATE;
import model.utility.DataHelper;
import model.utility.XmlHelper;
import system.data.Category;
import system.data.PageCollection;
import system.data.Settings;
import system.data.SinglePage;
import org.apache.commons.io.FileUtils;
import org.mockito.mock.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import system.DataCenter;

import javafx.stage.Stage;

public class GenerateTest {
  
  private Settings settings;
  private GenerateModel model;
  private PageCollection testCollection;
  private Map<String, Category> categories;

  @Before
  public void setUp() {
    
    Stage mockStage = mock(Stage.class);
    
    settings = XmlHelper.retrieveSettingFromFile();
    DataCenter dataCenter = new DataCenter(mockStage);
    dataCenter.setSettings(settings);
    
    categories = XmlHelper.retrieveCatetoryFromXML(settings.getLocalPath());
    dataCenter.setCategory(categories);
    
    //testCollection = DataHelper.retrievePage(settings.getLocalPath()+ "edit/");
    
    model = new GenerateModel();
    model.setDataCenter(dataCenter);
    model.init();
 
  }
  /*
  @Test
  public void generateSinglePageTest() {
    Collection<SinglePage> page = testCollection.values(); 
    SinglePage test = page.iterator().next();
    model.generateSinglePage(GENERATE.draft, test);
    
    File result = new File(settings.getLocalPath() + "web/" + test.getDirectory() + "/" + test.getName());
    assertTrue(result.exists());
    
    String resultContent = "";
    try {
      resultContent = FileUtils.readFileToString(result, "UTF-8");
    }
    catch(Exception exception) {
      exception.printStackTrace();
    }
    
    assertTrue(resultContent.contains(test.getContent()));
  }
  */
  @Test
  public void generateSingleCategoryPageTest() {
    for(Category item: categories.values()) {
      model.generateSingleCategoryPage(GENERATE.draft, item);
      File identify = new File(settings.getLocalPath() + "web/list/"+item.getName()+".html");
      assertTrue(identify.exists());
    }
  }
  
  
}
