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

	  Controller controller = new EditController();
	  controller.setDataCenter(dataCenter);
	  controller.init();
	  
		Scene scene = new Scene(controller.getView(), 1000, 600);
		primaryStage.setTitle("Content Management System For Non-CS users");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.getIcons().add(new Image(DataCenter.class.getResourceAsStream("icon.png")));
	}
	
	public static void main(String[] args) {
		launch(args);	
	}

}
