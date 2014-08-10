package tests.uitests;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import util.DateComparator;
import application.CustomCalendar;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CustomCalendarTest extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		String[] rows = {"1","2","3","4","5","6","7","8","9","10","11","101", "102", "103", "114", "203","FeWo", "Appartement 1", "Appartement 2"};
		
		CustomCalendar<String> root = new CustomCalendar<String>(
				Arrays.asList(rows));
		
		Calendar start = new GregorianCalendar(2014, 7, 4);
		Calendar end = new GregorianCalendar(2014, 7, 12);
		root.placeMarker("Familie Stratmann", 2, start, end);
		
		Calendar start2 = new GregorianCalendar(2014, 7, 10);
		Calendar end2 = new GregorianCalendar(2014, 7, 13);
		root.placeMarker("Herr Mustermann", 7, start2, end2);
		
		System.out.println(DateComparator.dayBefore(start, end));
		System.out.println(DateComparator.monthBefore(start, end));
		System.out.println(DateComparator.monthInRange(Calendar.getInstance(), start, end));
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass()
				.getResource("/CustomCalendar.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("CustomCalendar Component Test");
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
