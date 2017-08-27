package unitTest;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertTrue;
import model.utility.ImageFilter;
import model.utility.ImageUploader;
import static org.mockito.Mockito.*;
import system.data.Settings;

public class ImageFilterTest {

  private long startTime;
  private long endTime;
  private ImageFilter imageFilter;
  private ImageUploader imageUploader;
  private final String testContent = "<img src=\"file:///Users/yu-shuan/Documents/CMS/test/list.png\" style=\"max-width: 100%;\"></p><p style=\"text-align: left;\"><font face=\"Times New Roman\">To determine this project would be friendlier to Non-CS<img src=\"file:///Users/yu-shuan/Documents/CMS/test/user.png\" style=\"max-width: 100%;\"> users than existing content managements, in this section, it will compare the procedure of two systems. (Wordpress, which is the most popular system to build a personal Web site, is selected to represent existing content management systems). The comparison will concentrate on the process of installing, which causes the distinction between two systems. In addition, the problems users probably have will also happen in how to get started. In order to make the comparison precisely, a precondition is as follows</font>";
  private final String resultContent = "<img src=\"file:///Users/yu-shuan/Documents/CMS/upload/list.png\" style=\"max-width: 100%;\"></p><p style=\"text-align: left;\"><font face=\"Times New Roman\">To determine this project would be friendlier to Non-CS<img src=\"file:///Users/yu-shuan/Documents/CMS/upload/user.png\" style=\"max-width: 100%;\"> users than existing content managements, in this section, it will compare the procedure of two systems. (Wordpress, which is the most popular system to build a personal Web site, is selected to represent existing content management systems). The comparison will concentrate on the process of installing, which causes the distinction between two systems. In addition, the problems users probably have will also happen in how to get started. In order to make the comparison precisely, a precondition is as follows</font>";
  
  @Before
  public void setUp() {
    Settings mockSettings = mock(Settings.class);
    when(mockSettings.getLocalPath()).thenReturn("/Users/yu-shuan/Documents/CMS/");
    this.imageFilter = new ImageFilter(testContent,mockSettings);
    this.imageUploader = new ImageUploader(testContent,mockSettings); 
  }
  
  @Test
  public void storeTest() {
    startTime = System.nanoTime();
    assertTrue(imageFilter.store());
    endTime = System.nanoTime() - startTime;
    System.out.println("Test1:" + endTime);
  }
  
  @Test
  public void store2Test() {
    imageFilter.imageStore(testContent);
  }
  
  @Test
  public void store3Test() {
    startTime = System.nanoTime();
    assertTrue(imageUploader.run().equals(resultContent));
    endTime = System.nanoTime() - startTime;
    System.out.println("Test3:" + endTime);
  }
  
  
}
