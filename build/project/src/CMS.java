import controller.EditController;
import controller.Controller;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import system.SystemManager;

public class CMS extends Application {
	
  @Override
	public void start(Stage primaryStage) throws Exception {
	  SystemManager systemManager = new SystemManager(primaryStage);

	  Controller controller = new EditController();
	  systemManager.register(controller);
	  systemManager.addPane(controller);
	  controller.init();
	  
		Scene scene = new Scene(systemManager.getPane(), 1000, 600);
		primaryStage.setTitle("Content Management System For Non-CS users");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.getIcons().add(new Image(SystemManager.class.getResourceAsStream("icon.png")));
		primaryStage.getIcons().add(new Image(SystemManager.class.getResourceAsStream("icon.icns")));
		
	}
	
	public static void main(String[] args) {
		launch(args);	
	}

}
