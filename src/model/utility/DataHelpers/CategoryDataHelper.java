package model.utility.DataHelpers;

import java.io.File;
import java.io.FilenameFilter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import system.SystemSettings;
import system.data.Category;
import system.data.CategoryCollection;
import system.data.SinglePage;

public class CategoryDataHelper implements DataHelper<CategoryCollection> {
  
  public String localCategoryPath;
  
  public CategoryDataHelper(String localPath) {
    this.localCategoryPath = localPath + SystemSettings.categoryPath;
  }

  public CategoryCollection read(String name) {
    CategoryCollection categories = new CategoryCollection();
    File directory = new File(localCategoryPath);
    
    try {
      JAXBContext context = JAXBContext.newInstance(Category.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      
      for(File xmlFile : directory.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File directory, String fileName) {
          if(fileName.contains(".DS_Store")) return false;
          else return true;}
        }
      )) 
        {
          Category category = (Category) unmarshaller.unmarshal(xmlFile);
          categories.put(category.getName(), category);
        }
    } 
    catch (JAXBException e) {
      e.printStackTrace();
    }
    
    if(categories.isEmpty()) {
      categories = setupDefaultCategories();
    }
    
    return categories;
  }
  
  public Boolean delete(String categoryName) {
    String filePath = localCategoryPath + categoryName + ".xml";
    File file = new File(filePath);
    if(file.exists()) {
      file.delete();
      return true;
    }
    return false;
  }
  
  public Boolean write(CategoryCollection categories) { 
    for(Category category : categories.values()) {
      write(category);
    }
    return true;
  }
  
  public Boolean write(Category category) {
    try {
      File XMLfile = new File(localCategoryPath + category.getName() + ".xml");
      if(!XMLfile.exists()){
        XMLfile.createNewFile();
      }
      JAXBContext context = JAXBContext.newInstance(Category.class);
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      marshaller.marshal(category, XMLfile);
      return true;
    } 
    catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
  
  private CategoryCollection setupDefaultCategories() {
    CategoryCollection categories = new CategoryCollection();
    Category defaultCategory = new Category("Uncategorized");
    Category defaultHiddenCategory = new Category("Hidden(Draft Model)");
    SinglePage singlepage = new SinglePage("index.html");
    singlepage.setDirectory("default");
    singlepage.setTitle("Welcome!");
    defaultCategory.addPageToList(singlepage);
    categories.put(defaultCategory.getName(), defaultCategory);
    categories.put(defaultHiddenCategory.getName(), defaultHiddenCategory);
   
    return categories;
  } 
  
}
