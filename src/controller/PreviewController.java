package controller;

import model.PreviewModel;
import view.PreviewView;

public class PreviewController extends Controller {

  public PreviewController() {
    view = new PreviewView();
    model = new PreviewModel();
  }
  
  @Override
  public void init(){
    attached(view, model);
    view.init();
    model.init();
    
    PreviewView CastView = (PreviewView)view;
    CastView.setResultConverter(button -> {
      return null;
    });
    
    view.showPane();
  }
}
