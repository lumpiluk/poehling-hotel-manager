/**
 * Sample Skeleton for 'MainWindow.fxml' Controller Class
 */

package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import util.Messages;
import util.Messages.ErrorType;
import application.customControls.CalendarPane;
import application.customControls.CustomerForm;
import application.customControls.CustomerPane;
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

    @FXML // fx:id="leftMenuGroup"
    private ToggleGroup leftMenuGroup; // Value injected by FXMLLoader
    
    private ChangeListener<Toggle> leftMenuGroupListener = new ChangeListener<Toggle>() {
		@Override
		public void changed(ObservableValue<? extends Toggle> observable,
				Toggle oldValue, Toggle newValue) {
			if (newValue == null || oldValue == newValue) {
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
			}
			
			if (newValue == btnCalendar && !appBody.getItems().contains(calendarPane)) {
				showCalendarView();
			} else if (newValue == btnRoomPlan) {
				showRoomPlanView();
			} else if (newValue == btnCustomers) {
				showCustomerView();
			} else if (newValue == btnInvoices) {
				showInvoiceView();
			}
			
		}
    };

    @FXML // fx:id="btnInvoices"
    private ToggleButton btnInvoices; // Value injected by FXMLLoader

    @FXML // fx:id="btnCustomers"
    private ToggleButton btnCustomers; // Value injected by FXMLLoader

    @FXML // fx:id="btnCalendar"
    private ToggleButton btnCalendar; // Value injected by FXMLLoader

    @FXML // fx:id="leftMenu"
    private Accordion leftMenu; // Value injected by FXMLLoader

    @FXML // fx:id="btnRoomPlan"
    private ToggleButton btnRoomPlan; // Value injected by FXMLLoader
    
    @FXML // fx:id="bookingsTitledPane
    private TitledPane bookingsTitledPane; // Value injected by FXMLLoader

    // custom controls to be toggled by buttons within the leftMenuGroup
    private CalendarPane calendarPane;
    
    //private CustomerView customerView;
    private CustomerForm customerForm; // temporary
    
    private void hideCalendarView() {
    	appBody.getItems().remove(calendarPane);
    }
    
    private void hideRoomPlanView() {
    	
    }
    
    private void hideCustomerView() {
    	
    }
    
    private void hideInvoiceView() {
    	
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
    	try {
			customerForm = new CustomerForm();
			customerForm.load();
	    	appBody.getItems().add(1, customerForm);
	    	appBody.setDividerPositions(0.0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void showInvoiceView() {
    	
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
        assert leftMenu != null : "fx:id=\"leftMenu\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert btnRoomPlan != null : "fx:id=\"btnRoomPlan\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert bookingsTitledPane != null : "fx:id=\"bookingsTitledPane\" was not injected: check your FXML file 'MainWindow.fxml'.";

        // init custom controls
        try {
	        calendarPane = new CalendarPane();
			calendarPane.load();
			
			//customerView = new CustomerView();
			//customerView.load();
	    } catch (IOException e) {
			Messages.showError(e, ErrorType.UI);
		}
        
        leftMenuGroup.selectedToggleProperty().addListener(leftMenuGroupListener);
        showCalendarView(); // TODO: load from settings what to show first?
        //System.out.println(com.sun.javafx.runtime.VersionInfo.getRuntimeVersion());
        
        leftMenu.setExpandedPane(bookingsTitledPane);
        
    }
}
