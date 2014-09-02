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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import data.DataSupervisor;
import data.HotelData;

/**
 * @author lumpiluk
 *
 */
public class SimpleTablesView extends AbstractControl {
	
	@FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="tbFlags"
    private ToggleButton tbFlags; // Value injected by FXMLLoader

    @FXML // fx:id="btnItemCancel"
    private Button btnItemCancel; // Value injected by FXMLLoader

    @FXML // fx:id="tbCountries"
    private ToggleButton tbCountries; // Value injected by FXMLLoader

    @FXML // fx:id="btnAddItem"
    private Button btnAddItem; // Value injected by FXMLLoader

    @FXML // fx:id="btnRemoveItem"
    private Button btnRemoveItem; // Value injected by FXMLLoader

    @FXML // fx:id="btnItemOk"
    private Button btnItemOk; // Value injected by FXMLLoader

    @FXML // fx:id="tfItem"
    private TextField tfItem; // Value injected by FXMLLoader

    @FXML // fx:id="toggleGroup"
    private ToggleGroup toggleGroup; // Value injected by FXMLLoader

    @FXML // fx:id="tbTitles"
    private ToggleButton tbTitles; // Value injected by FXMLLoader

    @FXML // fx:id="tbStates"
    private ToggleButton tbStates; // Value injected by FXMLLoader
    
    @FXML // fx:id="tableView"
    private TableView<?> tableView; // Value injected by FXMLLoader
	
	private DataSupervisor dataSupervisor;
	
	/**
	 * @throws IOException
	 */
	public SimpleTablesView() throws IOException {
		// TODO Auto-generated constructor stub
	}
	
	public void initData(DataSupervisor dataSupervisor) {
		this.dataSupervisor = dataSupervisor;
		
		toggleGroup.selectedToggleProperty().addListener(
				(observable, oldVal, newVal) -> {
					// make toggleButtons persistent within group: don't allow no button to be selected!
			if (newVal == null) {
				oldVal.setSelected(true);
				return;
			} else if (oldVal == null || oldVal == newVal) {
				return;
			}
			
		});
	}
	
	public void initTableView() {
		
	}
	
	public void openTable(final String tableName) {
		
	}
	
	@FXML
    void btnAddItemClicked(ActionEvent event) {

    }

    @FXML
    void btnRemoveItemClicked(ActionEvent event) {

    }

    @FXML
    void btnItemOkClicked(ActionEvent event) {

    }

    @FXML
    void btnItemCancelClicked(ActionEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert tbFlags != null : "fx:id=\"tbFlags\" was not injected: check your FXML file 'SimpleTablesView.fxml'.";
        assert btnItemCancel != null : "fx:id=\"btnItemCancel\" was not injected: check your FXML file 'SimpleTablesView.fxml'.";
        assert tbCountries != null : "fx:id=\"tbCountries\" was not injected: check your FXML file 'SimpleTablesView.fxml'.";
        assert btnAddItem != null : "fx:id=\"btnAddItem\" was not injected: check your FXML file 'SimpleTablesView.fxml'.";
        assert btnRemoveItem != null : "fx:id=\"btnRemoveItem\" was not injected: check your FXML file 'SimpleTablesView.fxml'.";
        assert btnItemOk != null : "fx:id=\"btnItemOk\" was not injected: check your FXML file 'SimpleTablesView.fxml'.";
        assert tfItem != null : "fx:id=\"tfItem\" was not injected: check your FXML file 'SimpleTablesView.fxml'.";
        assert toggleGroup != null : "fx:id=\"toggleGroup\" was not injected: check your FXML file 'SimpleTablesView.fxml'.";
        assert tbTitles != null : "fx:id=\"tbTitles\" was not injected: check your FXML file 'SimpleTablesView.fxml'.";
        assert tbStates != null : "fx:id=\"tbStates\" was not injected: check your FXML file 'SimpleTablesView.fxml'.";
        assert tableView != null : "fx:id=\"tableView\" was not injected: check your FXML file 'SimpleTablesView.fxml'.";
    }

}
