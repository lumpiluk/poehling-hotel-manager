/**
 * 
 */
package application.customControls;

import java.awt.ScrollPane;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import util.Messages;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

/**
 * @author lumpiluk
 *
 */
public class CustomerPane extends AbstractControl {

	@FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnNewAddress"
    private Button btnNewAddress; // Value injected by FXMLLoader

    @FXML // fx:id="btnRemoveCustomer"
    private Button btnRemoveCustomer; // Value injected by FXMLLoader

    @FXML // fx:id="btnEditCustomer"
    private Button btnEditCustomer; // Value injected by FXMLLoader

    @FXML // fx:id="contentPane"
    private VBox contentPane; // Value injected by FXMLLoader

    /** 
     * Using a ListView rather than ScrollPane for the CustomerForm to make
     * vertical scrolling easier.
     */
    private ListView<Control> lvForm;
    
    private CustomerForm customerForm;
    
    /**
	 * @throws IOException
	 */
	public CustomerPane() throws IOException {
		super();
	}
    
    @FXML
    void btnNewAddressClicked(ActionEvent event) {

    }

    @FXML
    void btnRemoveAddressClicked(ActionEvent event) {

    }

    @FXML
    void btnEditAddressClicked(ActionEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnNewAddress != null : "fx:id=\"btnNewAddress\" was not injected: check your FXML file 'CustomerPane.fxml'.";
        assert btnRemoveCustomer != null : "fx:id=\"btnRemoveCustomer\" was not injected: check your FXML file 'CustomerPane.fxml'.";
        assert btnEditCustomer != null : "fx:id=\"btnEditCustomer\" was not injected: check your FXML file 'CustomerPane.fxml'.";
        assert contentPane != null : "fx:id=\"contentPane\" was not injected: check your FXML file 'CustomerPane.fxml'.";

        try {
	        customerForm = new CustomerForm();
	        customerForm.load();
	        //ScrollPane sp = new ScrollPane();
	        //sp.
	        //lvForm = new ListView<Control>();
	        //lvForm.getItems().add(customerForm);
	        //contentPane.getChildren().add(1, lvForm);
        } catch (IOException e) {
        	Messages.showError(e, Messages.ErrorType.UI);
        }
        
        
    }
	
	

}
