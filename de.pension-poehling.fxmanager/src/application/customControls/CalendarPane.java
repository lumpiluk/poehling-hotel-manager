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

package application.customControls;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import util.Messages;
import util.Messages.ErrorType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;

public class CalendarPane extends AbstractControl {

	private CustomCalendar customCalendar;
	
	private Calendar monthToDisplay = new GregorianCalendar();
	
	@FXML // fx:id="layoutBox"
    private VBox layoutBox; // Value injected by FXMLLoader
	
	@FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="todayBtn"
    private Button todayBtn; // Value injected by FXMLLoader

    @FXML // fx:id="priorYearBtn"
    private Button priorYearBtn; // Value injected by FXMLLoader

    @FXML // fx:id="priorMonthBtn"
    private Button priorMonthBtn; // Value injected by FXMLLoader

    @FXML // fx:id="nextYearBtn"
    private Button nextYearBtn; // Value injected by FXMLLoader

    @FXML // fx:id="nextMonthBtn"
    private Button nextMonthBtn; // Value injected by FXMLLoader

    @FXML // fx:id="contentPane"
    private ScrollPane contentPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="monthLbl"
    private Label monthLbl; // Value injected by FXMLLoader

    @FXML // fx:id="yearLbl"
    private Label yearLbl; // Value injected by FXMLLoader
    
    @FXML // fx:id="tbMonthPicker"
    private ToolBar tbMonthPicker; // Value injected by FXMLLoader

    //private ListSpinner<String> monthSpinner; // TODO?
    
    public CalendarPane() throws IOException {
    	
    }
    
    @FXML
    void todayBtnAction(ActionEvent event) {
    	monthToDisplay = new GregorianCalendar();
    	updateLabels();
    	customCalendar.setMonth(monthToDisplay);
    }
    
    private void updateLabels() {
    	monthLbl.setText(Messages.getMonth(monthToDisplay.get(Calendar.MONTH)));
    	yearLbl.setText(Integer.toString(monthToDisplay.get(Calendar.YEAR)));
    }

    @FXML
    void priorMonthBtnAction(ActionEvent event) {
    	monthToDisplay.add(Calendar.MONTH, -1);
    	updateLabels();
    	customCalendar.setMonth(monthToDisplay);
    }

    @FXML
    void nextMonthBtnAction(ActionEvent event) {
    	monthToDisplay.add(Calendar.MONTH, 1);
    	updateLabels();
    	customCalendar.setMonth(monthToDisplay);
    }

    @FXML
    void priorYearBtnAction(ActionEvent event) {
    	monthToDisplay.add(Calendar.YEAR, -1);
    	updateLabels();
    	customCalendar.setMonth(monthToDisplay);
    }

    @FXML
    void nextYearBtnAction(ActionEvent event) {
    	monthToDisplay.add(Calendar.YEAR, 1);
    	updateLabels();
    	customCalendar.setMonth(monthToDisplay);
    }


    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert todayBtn != null : "fx:id=\"todayBtn\" was not injected: check your FXML file 'CalendarPane.fxml'.";
        assert priorYearBtn != null : "fx:id=\"priorYearBtn\" was not injected: check your FXML file 'CalendarPane.fxml'.";
        assert priorMonthBtn != null : "fx:id=\"priorMonthBtn\" was not injected: check your FXML file 'CalendarPane.fxml'.";
        assert nextYearBtn != null : "fx:id=\"nextYearBtn\" was not injected: check your FXML file 'CalendarPane.fxml'.";
        assert nextMonthBtn != null : "fx:id=\"nextMonthBtn\" was not injected: check your FXML file 'CalendarPane.fxml'.";
        assert contentPane != null : "fx:id=\"contentPane\" was not injected: check your FXML file 'CalendarPane.fxml'.";
        assert yearLbl != null : "fx:id=\"yearLbl\" was not injected: check your FXML file 'CalendarPane.fxml'.";
        assert monthLbl != null : "fx:id=\"monthLbl\" was not injected: check your FXML file 'CalendarPane.fxml'.";
        assert tbMonthPicker != null : "fx:id=\"tbMonthPicker\" was not injected: check your FXML file 'CalendarPane.fxml'.";
        assert layoutBox != null : "fx:id=\"layoutBox\" was not injected: check your FXML file 'CalendarPane.fxml'.";
        
        //tbMonthPicker.getItems().add(2, element);
        
        updateLabels();
        
        // TODO: make dynamic!
        try {
        	String[] testRows = {"1","2","3","4","5","6","7","8","9","10","11","101", "102", "103", "114", "203","FeWo", "Appartement 1", "Appartement 2"};
        	customCalendar = (CustomCalendar) AbstractControl.getInstance(CustomCalendar.class);
        	customCalendar.initData(Arrays.asList(testRows));
        	
        	contentPane.setContent(customCalendar);
        } catch (IOException e) {
			Messages.showError(e, ErrorType.UI);
		}
    }
}
