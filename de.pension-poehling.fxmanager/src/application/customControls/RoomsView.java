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
import java.util.ResourceBundle;

import util.Messages;
import data.DataSupervisor;
import data.Room;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;

/**
 * @author lumpiluk
 *
 */
public class RoomsView extends AbstractForm {
	
	@FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="cbLift"
    private CheckBox cbLift; // Value injected by FXMLLoader

    @FXML // fx:id="btnRemoveRoom"
    private Button btnRemoveRoom; // Value injected by FXMLLoader

    @FXML // fx:id="btnEditRoom"
    private Button btnEditRoom; // Value injected by FXMLLoader

    @FXML // fx:id="taView"
    private TextArea taView; // Value injected by FXMLLoader

    @FXML // fx:id="tfFloor"
    private TextField tfFloor; // Value injected by FXMLLoader

    @FXML // fx:id="cbType"
    private ComboBox<?> cbType; // Value injected by FXMLLoader

    @FXML // fx:id="btnRoomOk"
    private Button btnRoomOk; // Value injected by FXMLLoader

    @FXML // fx:id="btnRoomCancel"
    private Button btnRoomCancel; // Value injected by FXMLLoader

    @FXML // fx:id="cbArea"
    private TextField tfArea; // Value injected by FXMLLoader

    @FXML // fx:id="tfName"
    private TextField tfName; // Value injected by FXMLLoader

    @FXML // fx:id="cbBalcony"
    private CheckBox cbBalcony; // Value injected by FXMLLoader

    @FXML // fx:id="btnNewRoom"
    private Button btnNewRoom; // Value injected by FXMLLoader
    
    @FXML // fx:id="roomsTable"
    private TableView<?> roomsTable; // Value injected by FXMLLoader
    
    @FXML // fx:id="roomToolBar"
    private ToolBar roomToolBar; // Value injected by FXMLLoader
    
    @FXML // fx:id="tfPhone"
    private TextField tfPhone; // Value injected by FXMLLoader
    
    
    private DataSupervisor dataSupervisor;
    
    private Mode currentMode = Mode.DISPLAY;
    
    private Room currentRoom;
    

    /**
	 * @throws IOException
	 */
	public RoomsView() throws IOException {
		super();
	}
	
	public void initData(DataSupervisor dataSupervisor) {
		this.dataSupervisor = dataSupervisor;
		
		initRoomTable();
		changeMode(Mode.DISPLAY);
	}
	
	@SuppressWarnings("unchecked")
	private void initRoomTable() {
		getRoomsTable().getColumns().clear();
		int i = 0;
		for (TableColumn<?, String> col : (new Room(dataSupervisor.getConnection())).createTableColumns()) {
			// set column title
			col.setText(Messages.getString("Ui.Rooms.Table."
					+ (new Room(dataSupervisor.getConnection()))
					.getPropertyIdentifiers()[i]));
			
			// add column to table
			getRoomsTable().getColumns().add((TableColumn<Room, String>)col);
			i++;
		}
		
		getRoomsTable().setItems(dataSupervisor.getRoomsObservable());
		// Insertion into and deletion from database will be handled in
		// dataSupervisor.
		
		getRoomsTable().getSelectionModel().selectedItemProperty()
				.addListener((observable, oldVal, newVal) -> {
					Platform.runLater(() -> { // is this really necessary? There used to be an Error without it... (see below, cf. CustomerForm)
			        	if (newVal != null)
			        		this.loadRoomIntoForm(newVal);
			        	this.btnEditRoom.setDisable(newVal == null); // EXCEPTION: "not on fx application thread"
			        	this.btnRemoveRoom.setDisable(newVal == null);
		        	});
				});
	}
	
	@SuppressWarnings("unchecked")
	private TableView<Room> getRoomsTable() { return (TableView<Room>) roomsTable; }
	
	@SuppressWarnings("unchecked")
	private ComboBox<String> getCbType() { return (ComboBox<String>) cbType; }
    
	private void setFormDisable(final boolean value) {
		Control[] formDisableControls = { cbLift, taView, tfFloor, cbType,
				tfArea, tfName, cbBalcony };
    	for (Control c : formDisableControls) {
    		c.setDisable(value);
    	}
	}
	
	private void clearForm() {
		cbLift.setSelected(false);
		taView.clear();
		tfFloor.clear();
		cbType.getSelectionModel().clearSelection();
		tfArea.clear();
		tfName.clear();
		cbBalcony.setSelected(false);
	}
	
	/**
     * Set the current state of the control.<br />
     * DISPLAY: form disabled, just show rooms selected in table<br />
	 * ADD: form enabled, after pressing OK new room will be added<br />
	 * EDIT: form enabled, after pressing OK selected room will be updated
     * @param m the mode to change to
     */
    private void changeMode(Mode m) {
    	roomToolBar.getItems().clear();
    	switch (m) {
    	case DISPLAY:
    		if (getRoomsTable().getSelectionModel().isEmpty()) {
        		clearForm();
        	} else {
        		loadRoomIntoForm(getRoomsTable().getSelectionModel().getSelectedItem());
        	}
    		setFormDisable(true);
    		roomToolBar.getItems().addAll(btnNewRoom, btnRemoveRoom, btnEditRoom);
    		break;
    	case ADD:
    		clearForm();
    		setFormDisable(false);
    		btnRoomOk.setText(Messages.getString("Ui.Rooms.btnRoomOk.New"));
    		roomToolBar.getItems().addAll(btnRoomOk, btnRoomCancel);
    		break;
    	case EDIT:
    		setFormDisable(false);
    		btnRoomOk.setText(Messages.getString("Ui.Rooms.btnRoomOk.Edit"));
    		roomToolBar.getItems().addAll(btnRoomOk, btnRoomCancel);
    		break;
    	}
    	currentMode = m;
    }
    
    private void roomFromForm() {
    	if (currentRoom == null)
    		currentRoom = new Room(dataSupervisor.getConnection());
    	currentRoom.setName(tfName.getText());
    	currentRoom.setAccessibleByLift(cbLift.isSelected());
    	try {
    		currentRoom.setArea(Double.parseDouble(tfArea.getText()));
    	} catch (NumberFormatException e) { }
    	currentRoom.setBalcony(cbBalcony.isSelected());
    	try {
    		currentRoom.setFloor(Integer.parseInt(tfFloor.getText()));
    	} catch (NumberFormatException e) { }
    	currentRoom.setPhone(tfPhone.getText());
    	currentRoom.setType(getCbType().getSelectionModel().getSelectedItem());
    	currentRoom.setView(taView.getText());
    }
    
    private void loadRoomIntoForm(final Room room) {
    	this.currentRoom = room;
    	tfName.setText(room.getName());
    	cbLift.setSelected(room.isAccessibleByLift());
    	tfArea.setText(String.format("%f", room.getArea()));
    	cbBalcony.setSelected(room.hasBalcony());
    	tfFloor.setText(String.format("%d", room.getFloor()));
    	tfPhone.setText(room.getPhone());
    	getCbType().getSelectionModel().select(room.getType());
    	taView.setText(room.getView());
    }
	
    @FXML
    void btnNewRoomClicked(ActionEvent event) {
    	changeMode(Mode.ADD);
    }

    @FXML
    void btnRemoveRoomClicked(ActionEvent event) {
    	if (!getRoomsTable().getSelectionModel().isEmpty()) {
    		getRoomsTable().getItems().removeAll(
    				getRoomsTable().getSelectionModel().getSelectedItems());
    	}
    	currentRoom = null;
    	changeMode(Mode.DISPLAY);
    }

    @FXML
    void btnEditRoomClicked(ActionEvent event) {
    	changeMode(Mode.EDIT);
    }

    @FXML
    void btnRoomOkClicked(ActionEvent event) {
    	roomFromForm();
    	switch (currentMode) {
    	case ADD:
    		getRoomsTable().getItems().add(currentRoom);
    		break;
    	case EDIT:
    		dataSupervisor.updateConcurrently(currentRoom);
    		break;
    	default: break;
    	}
    	currentRoom = null;
    	changeMode(Mode.DISPLAY);
    }

    @FXML
    void btnRoomCancelClicked(ActionEvent event) {
    	currentRoom = null;
    	changeMode(Mode.DISPLAY);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert cbLift != null : "fx:id=\"cbLift\" was not injected: check your FXML file 'RoomsView.fxml'.";
        assert btnRemoveRoom != null : "fx:id=\"btnRemoveRoom\" was not injected: check your FXML file 'RoomsView.fxml'.";
        assert btnEditRoom != null : "fx:id=\"btnEditRoom\" was not injected: check your FXML file 'RoomsView.fxml'.";
        assert taView != null : "fx:id=\"taView\" was not injected: check your FXML file 'RoomsView.fxml'.";
        assert tfFloor != null : "fx:id=\"tfFloor\" was not injected: check your FXML file 'RoomsView.fxml'.";
        assert cbType != null : "fx:id=\"cbType\" was not injected: check your FXML file 'RoomsView.fxml'.";
        assert btnRoomOk != null : "fx:id=\"btnRoomOk\" was not injected: check your FXML file 'RoomsView.fxml'.";
        assert btnRoomCancel != null : "fx:id=\"btnRoomCancel\" was not injected: check your FXML file 'RoomsView.fxml'.";
        assert tfArea != null : "fx:id=\"cbArea\" was not injected: check your FXML file 'RoomsView.fxml'.";
        assert tfName != null : "fx:id=\"tfName\" was not injected: check your FXML file 'RoomsView.fxml'.";
        assert cbBalcony != null : "fx:id=\"cbBalcony\" was not injected: check your FXML file 'RoomsView.fxml'.";
        assert btnNewRoom != null : "fx:id=\"btnNewRoom\" was not injected: check your FXML file 'RoomsView.fxml'.";
        assert roomsTable != null : "fx:id=\"roomsTable\" was not injected: check your FXML file 'RoomsView.fxml'.";
        assert roomToolBar != null : "fx:id=\"roomToolBar\" was not injected: check your FXML file 'RoomsView.fxml'.";
        assert tfPhone != null : "fx:id=\"tfPhone\" was not injected: check your FXML file 'RoomsView.fxml'.";
    }

}
