/* 
 * Copyright 2014 Lukas Stratmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tests.uitests;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import util.DateComparator;
import application.customControls.CustomCalendar;
import application.customControls.CustomCalendar.CalendarMarker;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CustomCalendarTest extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		String[] rows = {"1","2","3","4","5","6","7","8","9","10","11","101", "102", "103", "114", "203","FeWo", "Appartement 1", "Appartement 2"};
		
		CustomCalendar root = new CustomCalendar(Arrays.asList(rows));
		
		Calendar start = new GregorianCalendar(2014, 7, 4);
		Calendar end = new GregorianCalendar(2014, 8, 12);
		CalendarMarker m1 = root.createMarker("Familie Stratmann", 2, start, end);
		
		Calendar start2 = new GregorianCalendar(2014, 7, 10);
		Calendar end2 = new GregorianCalendar(2014, 7, 13);
		CalendarMarker m2 = root.createMarker("Herr Mustermann", 7, start2, end2);
		
		System.out.println(DateComparator.dayBefore(start, end));
		System.out.println(DateComparator.monthBefore(start, end));
		System.out.println(DateComparator.monthInRange(Calendar.getInstance(), start, end));
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass()
				.getResource("/CustomCalendar.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("CustomCalendar Component Test");
		primaryStage.show();
		
		// change start of m1 after 5s
		Thread thread = new Thread(new Task<Void>() {
			@Override
			public Void call() {
				System.out.println("waiting");
				try {
					Thread.sleep(5000);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							m1.setStart(new GregorianCalendar(2014, 7, 7));
						}
					});
					System.out.println("changed start");
				} catch (InterruptedException e) {	}
				return null;
			}
		});
		thread.setDaemon(true);
		thread.start();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
