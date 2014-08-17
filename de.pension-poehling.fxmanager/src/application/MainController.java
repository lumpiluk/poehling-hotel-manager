/**
 * Sample Skeleton for 'MainWindow.fxml' Controller Class
 */

package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import data.DataSupervisor;
import util.Messages;
import util.Messages.ErrorType;
import application.customControls.AbstractControl;
import application.customControls.CalendarPane;
import application.customControls.CustomerForm;
import application.customControls.DbConPane;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class MainController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="mainMenu"
    private MenuBar mainMenu; // Value injected by FXMLLoader

    @FXML // fx:id="leftPane"
    private SplitPane leftPane; // Value injected by FXMLLoader

    @FXML // fx:id="appBody"
    private SplitPane appBody; // Value injected by FXMLLoader
    
    @FXML // fx:id="btnInvoices"
    private ToggleButton btnInvoices; // Value injected by FXMLLoader

    @FXML // fx:id="btnCustomers"
    private ToggleButton btnCustomers; // Value injected by FXMLLoader

    @FXML // fx:id="btnCalendar"
    private ToggleButton btnCalendar; // Value injected by FXMLLoader
    
    @FXML // fx:id="btnDbOpenCreate"
    private ToggleButton btnDbOpenCreate; // Value injected by FXMLLoader
    
    @FXML // fx:id="btnStaticData"
    private ToggleButton btnStaticData; // Value injected by FXMLLoader

    @FXML // fx:id="leftMenu"
    private Accordion leftMenu; // Value injected by FXMLLoader

    @FXML // fx:id="btnRoomPlan"
    private ToggleButton btnRoomPlan; // Value injected by FXMLLoader
    
    @FXML // fx:id="bookingsTitledPane
    private TitledPane bookingsTitledPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="databaseTitledPane"
    private TitledPane databaseTitledPane; // Value injected by FXMLLoader

    @FXML // fx:id="leftMenuGroup"
    private ToggleGroup leftMenuGroup; // Value injected by FXMLLoader
    
    private ChangeListener<Toggle> leftMenuGroupListener = new ChangeListener<Toggle>() {
		@Override
		public void changed(ObservableValue<? extends Toggle> observable,
				Toggle oldValue, Toggle newValue) {
			// make toggleButtons persistent within group: don't allow no button to be selected!
			if (newValue == null) {
				oldValue.setSelected(true);
				return;
			} else if (oldValue == null || oldValue == newValue) {
				return;
			}
			
			if (oldValue == btnCalendar) {
				hideCalendarView();
			} else if (oldValue == btnRoomPlan) {
				hideRoomPlanView();
			} else if (oldValue == btnCustomers) {
				hideCustomerView();
			} else if (oldValue == btnInvoices) {
				hideInvoiceView();
			} else if (oldValue == btnDbOpenCreate) {
				hideDbConnectionView();
			}
			
			if (newValue == btnCalendar && !appBody.getItems().contains(calendarPane)) {
				showCalendarView();
			} else if (newValue == btnRoomPlan) {
				showRoomPlanView();
			} else if (newValue == btnCustomers) {
				showCustomerView();
			} else if (newValue == btnInvoices) {
				showInvoiceView();
			} else if (newValue == btnDbOpenCreate) {
				showDbConnectionView();
			}
			
		}
    };

    // custom controls to be toggled by buttons within the leftMenuGroup
    private CalendarPane calendarPane;
    
    private CustomerForm customerForm; // TODO: change name?
    
    private DbConPane dbConnectionPane;
    
    private DataSupervisor dataSupervisor;
    
    private void hideCalendarView() {
    	appBody.getItems().remove(calendarPane);
    }
    
    private void hideRoomPlanView() {
    	
    }
    
    private void hideCustomerView() {
    	appBody.getItems().remove(customerForm);
    }
    
    private void hideInvoiceView() {
    	
    }
    
    private void hideDbConnectionView() {
    	appBody.getItems().remove(dbConnectionPane);
    }
    
    private void showCalendarView() {
		appBody.getItems().add(1, calendarPane);
		SplitPane.setResizableWithParent(leftPane, false);
		SplitPane.setResizableWithParent(calendarPane, true);
		appBody.setDividerPositions(0.0);
    }
    
    private void showRoomPlanView() {
    	
    }
    
    private void showCustomerView() {
    	appBody.setDividerPositions(0.0);
    	appBody.getItems().add(1, customerForm);
    	SplitPane.setResizableWithParent(leftPane, false);
		SplitPane.setResizableWithParent(customerForm, true);
    	
    }
    
    private void showInvoiceView() {
    	
    }
    
    private void showDbConnectionView() {
    	appBody.setDividerPositions(0.0);
    	appBody.getItems().add(1, dbConnectionPane);
    	SplitPane.setResizableWithParent(leftPane, false);
		SplitPane.setResizableWithParent(dbConnectionPane, true);
    }
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() throws IOException {
        assert mainMenu != null : "fx:id=\"mainMenu\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert leftPane != null : "fx:id=\"leftPane\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert appBody != null : "fx:id=\"appBody\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert leftMenuGroup != null : "fx:id=\"leftMenuGroup\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert btnInvoices != null : "fx:id=\"btnInvoices\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert btnCustomers != null : "fx:id=\"btnCustomers\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert btnCalendar != null : "fx:id=\"btnCalendar\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert btnDbOpenCreate != null : "fx:id=\"btnDbOpenCreate\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert btnStaticData != null : "fx:id=\"btnStaticData\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert leftMenu != null : "fx:id=\"leftMenu\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert btnRoomPlan != null : "fx:id=\"btnRoomPlan\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert bookingsTitledPane != null : "fx:id=\"bookingsTitledPane\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert databaseTitledPane != null : "fx:id=\"databaseTitledPane\" was not injected: check your FXML file 'MainWindow.fxml'.";

        dataSupervisor = new DataSupervisor();
        
        // init custom controls
        try {
	        dbConnectionPane = (DbConPane) AbstractControl.getInstance(DbConPane.class);
	        dbConnectionPane.initData(dataSupervisor);
	        //dbConnectionPane.load();
	        
        	
        	calendarPane = (CalendarPane) AbstractControl.getInstance(CalendarPane.class);
			//calendarPane.load();
			
			customerForm = (CustomerForm) AbstractControl.getInstance(CustomerForm.class);
			//customerForm.load();
			
			//customerView = new CustomerView();
			//customerView.load();
	    } catch (IOException e) {
			Messages.showError(e, ErrorType.UI);
		}
        
        leftMenuGroup.selectedToggleProperty().addListener(leftMenuGroupListener);
        showDbConnectionView(); // TODO: load from settings what to show first?
        //System.out.println(com.sun.javafx.runtime.VersionInfo.getRuntimeVersion());
        
        leftMenu.setExpandedPane(databaseTitledPane);
        
    }
}
