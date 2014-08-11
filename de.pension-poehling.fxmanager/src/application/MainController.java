/**
 * Sample Skeleton for 'MainWindow.fxml' Controller Class
 */

package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.customControls.CalendarPane;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Region;

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

    @FXML // fx:id="LeftMenuGroup"
    private ToggleGroup LeftMenuGroup; // Value injected by FXMLLoader

    @FXML // fx:id="btnCalendar3"
    private ToggleButton btnCalendar3; // Value injected by FXMLLoader

    @FXML // fx:id="btnCalendar2"
    private ToggleButton btnCalendar2; // Value injected by FXMLLoader

    @FXML // fx:id="btnCalendar"
    private ToggleButton btnCalendar; // Value injected by FXMLLoader

    @FXML // fx:id="leftMenu"
    private Accordion leftMenu; // Value injected by FXMLLoader

    @FXML // fx:id="btnRoomPlan"
    private ToggleButton btnRoomPlan; // Value injected by FXMLLoader

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() throws IOException {
        assert mainMenu != null : "fx:id=\"mainMenu\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert leftPane != null : "fx:id=\"leftPane\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert appBody != null : "fx:id=\"appBody\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert LeftMenuGroup != null : "fx:id=\"LeftMenuGroup\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert btnCalendar3 != null : "fx:id=\"btnCalendar3\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert btnCalendar2 != null : "fx:id=\"btnCalendar2\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert btnCalendar != null : "fx:id=\"btnCalendar\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert leftMenu != null : "fx:id=\"leftMenu\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert btnRoomPlan != null : "fx:id=\"btnRoomPlan\" was not injected: check your FXML file 'MainWindow.fxml'.";

        Region calendarPane = new CalendarPane(); // TODO: handle exceptions!
        
        appBody.getItems().add(1, calendarPane);
        
    }
}
