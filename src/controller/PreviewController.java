package controller;

import model.GenerateModel;
import view.PreviewView;

/*
 * @Author Yu-Shuan
 * */

public class PreviewController extends Controller {
  
  private GenerateModel generateModel;

  public PreviewController() {
    view = new PreviewView();
  }
  
  @Override
  public void init(){
    generateModel = new GenerateModel();
    attached(view, generateModel);
    
    view.init();
    generateModel.init();
    generateModel.generatePreviewPage();
    
    PreviewView CastView = (PreviewView)view;
    CastView.setResultConverter(button -> {
      return null;
    });
    
    view.showPane();
  }
}
