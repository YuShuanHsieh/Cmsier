package controller;

import system.DataCenter;

public class ControllerFactory {
  
  public enum Id{
    ADMIN, PREVIEW, UPLOAD
  }
  
  public static Controller create(Id controllerId, DataCenter dataCenter, Controller parent) {
    Controller controller = createController(controllerId, dataCenter);
    controller.setParent(parent);
    controller.init();
    return controller;
  }
  
  public static Controller create(Id controllerId, DataCenter dataCenter) {
    Controller controller = createController(controllerId, dataCenter);
    controller.init();
    return controller;
  }
  
  private static Controller createController(Id controllerId, DataCenter dataCenter) {
    switch(controllerId) {
    case ADMIN:
      return new AdminController(dataCenter);
    case PREVIEW:
      return new PreviewController(dataCenter);
    case UPLOAD:
      return new UploadController(dataCenter);
    default:
      return null;
    }
  }
}
