package unitTest;

import org.junit.Test;

import model.UploadModel;
import org.junit.Before;
import static org.junit.Assert.assertTrue;

public class UploadModelTest {
  
  private UploadModel model = new UploadModel(); 
  
  @Before
  public void setUp() {
    model.connectToWebServer("files.000webhost.com", "cherriesweb", "abcd3350368");
  }
  
  @Test
  public void uploadSingleDirectoryTest() {
    assertTrue(model.uploadSingleDirectory("./", "web/",""));
  }

}
