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

package application;
	
import java.sql.SQLException;

import util.Messages;
import util.Messages.ErrorType;
import application.customControls.AbstractControl;
import application.customControls.MainWindow;
import data.DataSupervisor;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;


public class Main extends Application {
	
	private DataSupervisor dataSupervisor;
	
	EventHandler<WindowEvent> closingHandler = new EventHandler<WindowEvent>() {
		@Override
		public void handle(WindowEvent event) {
			if (dataSupervisor != null) {
				try {
					if (!dataSupervisor.getConnection().getAutoCommit()) {
						dataSupervisor.rollbackConcurrently();
					}
					dataSupervisor.closeDbConnection();
					System.out.println("Connection to database closed."); // TODO: remove debug output
				} catch (SQLException e) {
					Messages.showError(e, ErrorType.DB);
				}
			}
		}
	};
	
	@Override
	public void start(Stage primaryStage) {
		try {
			dataSupervisor = new DataSupervisor();
			
			MainWindow root = (MainWindow) AbstractControl.getInstance(MainWindow.class);
			root.initData(dataSupervisor);
			
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/Main.css")
					.toExternalForm());

			primaryStage.setTitle("Poehling HotelManager");
			primaryStage.setScene(scene);
			primaryStage.setOnCloseRequest(closingHandler);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
