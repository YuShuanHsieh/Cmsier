package model.test;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import model.component.Templatetor;

public class TemplatetorTest {

  static Templatetor templateTest;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    templateTest = new Templatetor("test/test.css", "test/target.css");
  }

  @Test
  public void test() throws Exception {
    assertTrue(templateTest.setMarkNotation('<', '>'));
    
    assertTrue(templateTest.addKeyAndContent("title", "#333333"));
    assertTrue(templateTest.addKeyAndContent("subtitle", "#000000"));
    
    templateTest.run();
  }

  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(TemplatetorTest.class);
    
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }
 
    System.out.println(result.wasSuccessful());
  }
}
