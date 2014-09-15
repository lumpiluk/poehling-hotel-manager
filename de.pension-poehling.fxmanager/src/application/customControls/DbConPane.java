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

/**
 * 
 */
package application.customControls;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import org.controlsfx.control.action.Action;

import data.DataSupervisor;
import util.Messages;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Provides the user options to import or create a database, shows notification
 * if default database could not be found. 
 * @author lumpiluk
 */
public class DbConPane extends AbstractControl {
	
	/** 
	 * Handles updates from DataSupervisor when trying to connect to a
	 * database.
	 */
	private Observer connectionStateObserver = new Observer() {

		@Override public void update(Observable o, Object arg) {
			DataSupervisor.ConnectionStatus newStatus =
					((DataSupervisor.ObservableConnectionState)o).getStatus();
			switch (newStatus) {
			case CONNECTION_ESTABLISHED:
				// enable controls
				mainWindow.enableControls();
				lblStatus.setText(Messages.getString("Ui.DbConnection.Status.ConnectionEstablished.text"));
				break;

				
				
			default:
				break;
			}
		}
		
	};
	
	@FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnNewDb"
    private Button btnNewDb; // Value injected by FXMLLoader

    @FXML // fx:id="btnImportDb"
    private Button btnImportDb; // Value injected by FXMLLoader

    @FXML // fx:id="lblStatus"
    private Label lblStatus; // Value injected by FXMLLoader
    
    private DataSupervisor dataSupervisor;
    
    private MainWindow mainWindow;
    
	/**
	 * Default constructor, only needed by JavaFX/FXML.
	 * Please use a constructor with DataSupervisor parameter instead!
	 * @throws IOException
	 */
	public DbConPane() throws IOException {
		super();
	}
	
	@FXML
    void btnNewDbClicked(ActionEvent event) {
		if (!checkForSupervisor())
    		return;
    	String dbFilePath = System.getProperty("user.home")
				+ "/.HotelManager/testDB.sqlite";
		File dbFile = new File(dbFilePath);
		if (dbFile.exists()) {
			/*Action dlgAction = Dialogs.create()
				.title(Messages.getString("Ui.DbConnection.FileAlreadyExistsConfirmation.Title"))
				.message(String.format(Messages.getString("Ui.DbConnection.FileAlreadyExistsConfirmation"),
						dbFile.getPath()))
				.style(DialogStyle.NATIVE)
				.showConfirm();
			if (dlgAction == Dialog.Actions.YES || dlgAction == Dialog.Actions.OK) {
				dbFile.delete();
			}*/ // TODO find alternative. Dialogs is deprecated and has been moved to OpenJFX
			dbFile.delete();
		}
		
		dbFile.getParentFile().mkdirs(); // e.g. ~/.HotelManager/ out of above String...
		openDb(dbFile, true);
    }

    @FXML
    void btnImportDbClicked(ActionEvent event) {

    }
    
    private boolean checkForSupervisor() {
    	if (dataSupervisor == null) {
    		Messages.showError(Messages.getString("Ui.DbConnection.Status.Error.NoDataSupervisor"),
    				Messages.ErrorType.DB);
    		return false;
    	}
    	return true;
    }
    
    private void openDb(File dbFile, boolean newDb) {
    	System.out.println("Connecting to " + dbFile.getAbsolutePath());
		lblStatus.setText(Messages.getString("Ui.DbConnection.Opening.Status.text"));
		
		// connect
		dataSupervisor.connectToDbConcurrently(dbFile, newDb, connectionStateObserver);
    }
    
    private void tryOpenDefaultDb() {
    	if (!checkForSupervisor())
    		return;
    	
    	String dbFilePath = System.getProperty("user.home")
				+ "/.HotelManager/testDB.sqlite";
		File dbFile = new File(dbFilePath);
    	if (!dbFile.exists()) {
			lblStatus.setText(Messages.getString("Ui.DbConnection.NoDefaultDbFound.Status.text"));
			return;
		}
    	
		openDb(dbFile, false);
    }
	
    public void initData(MainWindow main, DataSupervisor dataSupervisor) {
    	this.mainWindow = main;
    	this.dataSupervisor = dataSupervisor;
    	tryOpenDefaultDb();
    }
    
	@FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnNewDb != null : "fx:id=\"btnNewDb\" was not injected: check your FXML file 'DbConPane.fxml'.";
        assert btnImportDb != null : "fx:id=\"btnImportDb\" was not injected: check your FXML file 'DbConPane.fxml'.";
        assert lblStatus != null : "fx:id=\"lblStatus\" was not injected: check your FXML file 'DbConPane.fxml'.";
        
    }

}
