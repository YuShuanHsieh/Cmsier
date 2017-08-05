package model;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import model.utility.XmlHelper;
import org.junit.Test;
import org.junit.Before;
import java.io.File;

import system.data.Category;
import system.data.SinglePage;

public class CategoryModel extends Model {
  
  private LinkedList<Category> categories = new LinkedList<Category>();
  private XmlHelper xmlHelper = new XmlHelper();
  
  public CategoryModel() {
    
  }
  
  /*
  @Before
  public void setUpTest() {
    Category normal = new Category("normal");
    normal.addPageToList(new SinglePage("N_pageName1"));
    normal.addPageToList(new SinglePage("N_pageName2"));
    
    Category code = new Category("code");
    code.addPageToList(new SinglePage("C_pageName1"));
    code.addPageToList(new SinglePage("C_pageName2"));
    
    categories.add(normal);
    categories.add(code);
  }
  
  @Test
  public void generateXMLTest() {
    for(Category category : categories) {
      xmlHelper.writeCategoryToXML(category, "/Users/yu-shuan/Documents/");
      assertTrue(new File("/Users/yu-shuan/Documents/" + category.getName() + ".xml").exists());
    }
  }
  
  */

}
