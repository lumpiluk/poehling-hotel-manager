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
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import util.Messages;
import data.Address;
import data.DataSupervisor;
import data.Person;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

/**
 * Please note: Any changes to the database made in this custom control are
 * expected to be committed later using commitConcurrently().
 * @see CustomerForm
 * @author lumpiluk
 *
 */
public class PersonPane extends AbstractForm {
	
	/** Items used in people list view of current address. */
	public class PersonItem {
		private Person person;
		private BooleanProperty marked = new SimpleBooleanProperty(false);
		private ReadOnlyStringWrapper name = new ReadOnlyStringWrapper();
		
		public PersonItem(Person p, boolean addressee) {
			this.person = p;
			marked.set(addressee);
			marked.addListener((event) -> {
				if (marked.get()) {
					// ensure only one person in the table is marked as listener
					for (PersonItem pi : getPeopleTable().getItems()) {
						if (pi != this && pi.markedProperty().get()) {
							pi.markedProperty().set(false);
						}
					}
				}
			});
			updateText();
		}

		public Person getPerson() { return person; }	
		
		// used by PropertyValueFactory for peopleTable		
		public BooleanProperty markedProperty() { return marked; }
		
		public ReadOnlyStringProperty nameProperty() { return name; }
		
		private void updateText() {
			StringBuilder sb = new StringBuilder();
			if (person.getTitle() != null) {
				sb.append(person.getTitle());
				sb.append(" ");
			}
			if (person.getFirstNames() != null) {
				sb.append(person.getFirstNames());
				sb.append(" ");
			}
			sb.append(person.getSurnames());
			name.set(sb.toString());
		}
	}
	
	@FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    
    @FXML // fx:id="grid"
    private GridPane grid; // Value injected by FXMLLoader
    
    @FXML // fx:id="peopleTable"
    private TableView<?> peopleTable; // Value injected by FXMLLoader
    
    @FXML // fx:id="peopleTools"
    private ToolBar peopleTools; // Value injected by FXMLLoader

    @FXML // fx:id="btnAddPerson"
    private Button btnAddPerson; // Value injected by FXMLLoader
    
    @FXML // fx:id="btnEditPerson"
    private Button btnEditPerson; // Value injected by FXMLLoader

    @FXML // fx:id="btnPersonOk"
    private Button btnPersonOk; // Value injected by FXMLLoader

    @FXML // fx:id="btnRemovePerson"
    private Button btnRemovePerson; // Value injected by FXMLLoader

    @FXML // fx:id="tbSetAddressee"
    private ToggleButton tbSetAddressee; // Value injected by FXMLLoader
    
    @FXML // fx:id="btnPersonCancel"
    private Button btnPersonCancel; // Value injected by FXMLLoader
    
    @FXML // fx:id="titleCb"
    private ComboBox<?> titleCb; // Value injected by FXMLLoader

    @FXML // fx:id="foodMemoArea"
    private TextArea foodMemoArea; // Value injected by FXMLLoader

    @FXML // fx:id="birthdayPicker"
    private DatePicker birthdayPicker; // Value injected by FXMLLoader

    @FXML // fx:id="surnamesTb"
    private TextField surnamesTb; // Value injected by FXMLLoader

    @FXML // fx:id="foodMemoLbl"
    private Label foodMemoLbl; // Value injected by FXMLLoader

    @FXML // fx:id="firstNamesTb"
    private TextField firstNamesTb; // Value injected by FXMLLoader
    
    private Address address;
    
    private DataSupervisor dataSupervisor;
    
    private ValidationSupport validationSupport = new ValidationSupport();
    
    private Mode currentMode = Mode.DISPLAY;
    
	/**
	 * @throws IOException
	 */
	public PersonPane() throws IOException {
		
	}
	
	@SuppressWarnings("unchecked")
	private TableView<PersonItem> getPeopleTable() {
    	return (TableView<PersonItem>) peopleTable;
    }
	
	public Mode getCurrentMode() {
		return currentMode;
	}
	
	public void setCurrentMode(Mode value) {
		peopleTools.getItems().clear();
		switch (value) {
		case DISPLAY:
			peopleTools.getItems().addAll(btnAddPerson, btnRemovePerson,
    				btnEditPerson, tbSetAddressee);
			setFormDisable(true);
			peopleTable.setDisable(false);
			break;
		case ADD:
			btnPersonOk.setText(Messages.getString("Ui.Customer.People.Ok.New"));
    		peopleTools.getItems().addAll(btnPersonOk, btnPersonCancel);
			setFormDisable(false);
			peopleTable.setDisable(true);
			break;
		case EDIT:
			btnPersonOk.setText(Messages.getString("Ui.Customer.People.Ok.Edit"));
    		peopleTools.getItems().addAll(btnPersonOk, btnPersonCancel);
			setFormDisable(false);
			peopleTable.setDisable(true);
			break;
		}
		currentMode = value;
	}
	
	/**
	 * @param dataSupervisor
	 * @param a the address this person will be assigned to in the database.
	 */
	public void initData(DataSupervisor dataSupervisor) {
		this.dataSupervisor = dataSupervisor;

		initPeopleTable();
		initInvalidationSupport();
		setCurrentMode(Mode.DISPLAY);
	}
	
	public void setFormDisable(boolean value) {
		titleCb.setDisable(value);
		surnamesTb.setDisable(value);
		firstNamesTb.setDisable(value);
		birthdayPicker.setDisable(value);
		foodMemoArea.setDisable(value);
		//peopleTools.setDisable(value);
	}
	
	/** @param value the address this person will be assigned to in the database. */
	public void setAddress(Address value) {
		this.address = value;
	}
	
	private boolean evaluateForm() {
		// invalid if ValidationSupport still has any errors
    	if (!validationSupport.getValidationResult().getErrors().isEmpty()) {
    		Dialogs.create()
				.title(Messages.getString("Ui.Customer.Validation.DialogTitle"))
				.message(Messages.getString("Ui.Customer.Validation.ValidationFailed"))
				.style(DialogStyle.NATIVE)
				.showWarning();
    		System.out.println(validationSupport.getValidationResult().getErrors());
    		return false;
    	}
		
		return true;
	}
	
	public Person personFromForm() {
		if (!evaluateForm()) {
			return null;
		}
		
		Person p = new Person(dataSupervisor.getConnection());
		updatePerson(p);		
		return p;		
	}
	
	public void updatePerson(Person p) {
		p.setAddress(address); // TODO: what if not yet existent?
		p.setTitle((String) titleCb.getSelectionModel().getSelectedItem());
		p.setSurnames(surnamesTb.getText());
		p.setFirstNames(firstNamesTb.getText());
		if (birthdayPicker.getValue() != null)
			p.setBirthday(Date.valueOf(birthdayPicker.getValue()));
		p.setFoodMemo(foodMemoArea.getText());
		
		dataSupervisor.updateConcurrently(p);
	}
	
	private void initInvalidationSupport() {
		// surname must not be empty
		validationSupport.registerValidator(surnamesTb, true,
				Validator.createEmptyValidator(Messages.getString("Ui.Customer.Person.Validation.MissingSurname")));
	}
	
	public Person getAddresseeFromListItems() {
    	for (PersonItem pi : getPeopleTable().getItems()) {
    		if (pi.markedProperty().get()) {
    			return pi.getPerson();
    		}
    	}
    	return null;
    }
	
	@FXML
    void btnAddPersonClicked(ActionEvent event) {
		setCurrentMode(Mode.ADD);
    }

    @FXML
    void btnRemovePersonClicked(ActionEvent event) {
    	if (getPeopleTable().getSelectionModel().isEmpty()) {
    		return;
    	}
    	
    	// delete from database
    	dataSupervisor.deleteConcurrently(
    			getPeopleTable().getSelectionModel()
    			.getSelectedItem().getPerson()); // TODO: delete all at when inserting or updating address, then insert only the ones in the table
    	// delete from TableView
    	getPeopleTable().getItems().remove(
    			getPeopleTable().getSelectionModel().getSelectedItem());
    }

    @FXML
    void btnEditPersonClicked(ActionEvent event) {
    	if (getPeopleTable().getSelectionModel().isEmpty()) {
    		return;
    	}
    	setCurrentMode(Mode.EDIT);
    }

    @FXML
    void tbSetAddresseeToggled(ActionEvent event) {
    	PersonItem selection = getPeopleTable().getSelectionModel().getSelectedItem();
    	if (selection != null) {
    		selection.markedProperty().set(!selection.markedProperty().get());
    	}
    }

    @FXML
    void btnPersonOkClicked(ActionEvent event) {
    	switch (getCurrentMode()) {
    	case ADD:
    		Person p = personFromForm();
    		dataSupervisor.insertConcurrently(p);
    		PersonItem pi = new PersonItem(p, getPeopleTable().getItems().isEmpty()); // automagically mark as addressee if this is the first item
    		getPeopleTable().getItems().add(pi);
    		break;
    	case EDIT:
    		updatePerson(getPeopleTable().getSelectionModel()
    				.getSelectedItem().getPerson());
    		break;
		default:
			break;
    	}
    	setCurrentMode(Mode.DISPLAY);
    }

    @FXML
    void btnPersonCancelClicked(ActionEvent event) {
    	setCurrentMode(Mode.DISPLAY);
    }
    
    @SuppressWarnings("unchecked")
	private void initPeopleTable() {
    	TableColumn<PersonItem, Boolean> markerCol = new TableColumn<PersonItem, Boolean>(
    			Messages.getString("Ui.Customers.People.Table.markerCol"));
    	markerCol.setCellValueFactory(new PropertyValueFactory<PersonItem, Boolean>("marked"));
    	
    	TableColumn<PersonItem, String> nameCol = new TableColumn<PersonItem, String>(
    			Messages.getString("Ui.Customers.People.Table.nameCol"));
    	nameCol.setCellValueFactory(new PropertyValueFactory<PersonItem, String>("name"));
    	
    	getPeopleTable().getColumns().clear();
    	getPeopleTable().getColumns().addAll(markerCol, nameCol);
    }
	
	@FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
		assert grid != null : "fx:id=\"grid\" was not injected: check your FXML file 'PersonPane.fxml'.";
		assert titleCb != null : "fx:id=\"titleCb\" was not injected: check your FXML file 'PersonPane.fxml'.";
        assert foodMemoArea != null : "fx:id=\"foodMemoArea\" was not injected: check your FXML file 'PersonPane.fxml'.";
        assert birthdayPicker != null : "fx:id=\"birthdayPicker\" was not injected: check your FXML file 'PersonPane.fxml'.";
        assert surnamesTb != null : "fx:id=\"surnamesTb\" was not injected: check your FXML file 'PersonPane.fxml'.";
        assert foodMemoLbl != null : "fx:id=\"foodMemoLbl\" was not injected: check your FXML file 'PersonPane.fxml'.";
        assert firstNamesTb != null : "fx:id=\"firstNamesTb\" was not injected: check your FXML file 'PersonPane.fxml'.";

        // use ISO date format for birthday picker
        birthdayPicker.setConverter(new StringConverter<LocalDate>() {
        	private final String PATTERN = "yyyy-MM-dd";
        	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(PATTERN);

            { // why does this work? Suggested by Javadoc of setConverter()...
                birthdayPicker.setPromptText(PATTERN.toLowerCase());
            }

            @Override public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
    }

}
