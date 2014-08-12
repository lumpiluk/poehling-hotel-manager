package application;
	
import util.Messages;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			VBox root = (VBox)FXMLLoader.load(
					getClass().getResource("/MainWindow.fxml"),
					Messages.getBundle());
			
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/Main.css")
					.toExternalForm());
			scene.getStylesheets().add(getClass()
					.getResource("/CustomCalendar.css").toExternalForm());
			primaryStage.setTitle("Poehling HotelManager");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
