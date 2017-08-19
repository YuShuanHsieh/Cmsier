package controller;

import javafx.scene.layout.Pane;
import model.GenerateModel;
import model.Model;
import system.DataCenter;
import view.PreviewView;
import view.View;

/**
 * @Author Yu-Shuan
 * */

public class PreviewController implements Controller {
  
  private PreviewView view;
  private DataCenter dataCenter;
  private GenerateModel generateModel;

  public PreviewController(DataCenter dataCenter) {
    this.dataCenter = dataCenter;
    view = new PreviewView();
    
    generateModel = new GenerateModel(dataCenter);
    attached(view, generateModel);
  }
  
  @Override
  public void init(){
    view.init();
    generateModel.init();
    generateModel.generatePreviewPage();
    
    PreviewView CastView = (PreviewView)view;
    CastView.setResultConverter(button -> {
      return null;
    });
    
    view.showPane();
  }
  
  @Override
  public Pane getView() {
    return view.getPane();
  }
  
  @Override
  public void attached(View view, Model model) {
    model.attach(view);
  }
  
  @Override
  public void setEvent(){
  }
  
  @Override
  public void setParent(Controller parent) {
  }
}
