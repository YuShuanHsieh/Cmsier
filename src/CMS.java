import controller.EditController;
import controller.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import system.DataCenter;

public class CMS extends Application {
	
  @Override
	public void start(Stage primaryStage) throws Exception {
	  DataCenter dataCenter = new DataCenter(primaryStage);
	  dataCenter.init();
	  
	  Controller controller = new EditController(dataCenter);
	  controller.init();
	  
	  /** In this project, it uses fixed size of window.*/
		Scene scene = new Scene(controller.getView(), 1000, 750);
		primaryStage.setTitle("Content Management System For Non-CS users");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.getIcons().add(new Image(DataCenter.class.getResourceAsStream("icon.png")));
	}
	
	public static void main(String[] args) {
		launch(args);	
	}

}
