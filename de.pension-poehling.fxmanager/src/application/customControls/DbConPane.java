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
import java.util.ResourceBundle;

import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;

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
	
	/**
	 * @throws IOException
	 */
	public DbConPane() throws IOException {
		super();
	}
	
	@FXML
    void btnNewDbClicked(ActionEvent event) {
		
    }

    @FXML
    void btnImportDbClicked(ActionEvent event) {

    }
    
    private void tryOpenDefaultDb() {
    	String dbFilePath = System.getProperty("user.home")
				+ "/.HotelManager/";
		File dbFile = new File(dbFilePath);
		if (!dbFile.exists()) {
			lblStatus.setText(Messages.getString("Ui.DbConnection.NoDefaultDbFound.Status.text"));
			return;
		}
		lblStatus.setText(Messages.getString("Ui.DbConnection.Opening.Status.text"));
    }
	
	@FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnNewDb != null : "fx:id=\"btnNewDb\" was not injected: check your FXML file 'DbConPane.fxml'.";
        assert btnImportDb != null : "fx:id=\"btnImportDb\" was not injected: check your FXML file 'DbConPane.fxml'.";
        assert lblStatus != null : "fx:id=\"lblStatus\" was not injected: check your FXML file 'DbConPane.fxml'.";

        tryOpenDefaultDb();
        
    }

}
