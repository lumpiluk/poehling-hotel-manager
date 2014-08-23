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
import java.util.ResourceBundle;

import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import util.Messages;
import data.Address;
import data.DataSupervisor;
import data.Person;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * @author lumpiluk
 *
 */
public class PersonPane extends AbstractControl {

	public static enum Mode {
		NEW, EDIT;
	}
	
	@FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

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
    
	/**
	 * @throws IOException
	 */
	public PersonPane() throws IOException {
		
	}
	
	/**
	 * @param dataSupervisor
	 * @param a the address this person will be assigned to in the database.
	 */
	public void initData(DataSupervisor dataSupervisor) {
		this.dataSupervisor = dataSupervisor;
		
		initInvalidationSupport();
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
	
	private Person personFromForm() {
		if (!evaluateForm()) {
			return null;
		}
		
		Person p = new Person(dataSupervisor.getConnection());
		p.setAddress(address);
		p.setTitle((String) titleCb.getSelectionModel().getSelectedItem());
		p.setSurnames(surnamesTb.getText());
		p.setFirstNames(firstNamesTb.getText());
		p.setBirthday(Date.valueOf(birthdayPicker.getValue()));
		p.setFoodMemo(foodMemoArea.getText());
		
		return p;		
	}
	
	private void initInvalidationSupport() {
		// surname must not be empty
		validationSupport.registerValidator(surnamesTb, true,
				Validator.createEmptyValidator(Messages.getString("Ui.Customer.Person.Validation.MissingSurname")));
	}
	
	@FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert titleCb != null : "fx:id=\"titleCb\" was not injected: check your FXML file 'PersonPane.fxml'.";
        assert foodMemoArea != null : "fx:id=\"foodMemoArea\" was not injected: check your FXML file 'PersonPane.fxml'.";
        assert birthdayPicker != null : "fx:id=\"birthdayPicker\" was not injected: check your FXML file 'PersonPane.fxml'.";
        assert surnamesTb != null : "fx:id=\"surnamesTb\" was not injected: check your FXML file 'PersonPane.fxml'.";
        assert foodMemoLbl != null : "fx:id=\"foodMemoLbl\" was not injected: check your FXML file 'PersonPane.fxml'.";
        assert firstNamesTb != null : "fx:id=\"firstNamesTb\" was not injected: check your FXML file 'PersonPane.fxml'.";

    }

}
