package model;

import system.Statement;
import view.PreviewView;

public class PreviewModel extends Model {

  @Override
  public void init() {
    String preViewPagePath = dataCenter.getData().getCurrentPageLocalPath();
    view.updateStatement(PreviewView.UPDATE_LOADPAGE, Statement.success(preViewPagePath));
  }

  

}
