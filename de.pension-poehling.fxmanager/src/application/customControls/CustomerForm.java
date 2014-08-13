/**
 * 
 */
package application.customControls;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.net.URL;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;

import util.Messages;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;

/**
 * @author lumpiluk
 *
 */
public class CustomerForm extends AbstractControl {

	private final Pattern emailRegex = Pattern.compile(
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	
	public CustomerForm() throws IOException {
		super();
	}
	
	@FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="tabNewPerson"
    private Tab tabNewPerson; // Value injected by FXMLLoader

    @FXML // fx:id="tfMobile"
    private TextField tfMobile; // Value injected by FXMLLoader

    @FXML // fx:id="tfPostboxTown"
    private TextField tfPostboxTown; // Value injected by FXMLLoader

    @FXML // fx:id="cbPrivate"
    private CheckBox cbPrivate; // Value injected by FXMLLoader

    @FXML // fx:id="cbCountry"
    private ComboBox<?> cbCountry; // Value injected by FXMLLoader

    @FXML // fx:id="tfPostboxZip"
    private TextField tfPostboxZip; // Value injected by FXMLLoader

    @FXML // fx:id="btnEditPerson"
    private Button btnEditPerson; // Value injected by FXMLLoader

    @FXML // fx:id="tfTown"
    private TextField tfTown; // Value injected by FXMLLoader

    @FXML // fx:id="memoField"
    private TextField memoField; // Value injected by FXMLLoader

    @FXML // fx:id="cbDeceased"
    private CheckBox cbDeceased; // Value injected by FXMLLoader

    @FXML // fx:id="tfFax"
    private TextField tfFax; // Value injected by FXMLLoader

    @FXML // fx:id="lvPeople"
    private ListView<?> lvPeople; // Value injected by FXMLLoader

    @FXML // fx:id="tfZip"
    private TextField tfZip; // Value injected by FXMLLoader

    @FXML // fx:id="btnAddPerson"
    private Button btnAddPerson; // Value injected by FXMLLoader

    @FXML // fx:id="tfPhone"
    private TextField tfPhone; // Value injected by FXMLLoader

    @FXML // fx:id="tfWebsite"
    private TextField tfWebsite; // Value injected by FXMLLoader

    @FXML // fx:id="tfEmail"
    private TextField tfEmail; // Value injected by FXMLLoader

    @FXML // fx:id="tfStreet"
    private TextField tfStreet; // Value injected by FXMLLoader

    @FXML // fx:id="customerFormGrid"
    private GridPane customerFormGrid; // Value injected by FXMLLoader

    @FXML // fx:id="btnRemovePerson"
    private Button btnRemovePerson; // Value injected by FXMLLoader

    @FXML // fx:id="tbSetAddressee"
    private ToggleButton tbSetAddressee; // Value injected by FXMLLoader

    @FXML // fx:id="tfPostbox"
    private TextField tfPostbox; // Value injected by FXMLLoader

    @FXML // fx:id="cbState"
    private ComboBox<String> cbState; // Value injected by FXMLLoader

    @FXML // fx:id="tabExistingPerson"
    private Tab tabExistingPerson; // Value injected by FXMLLoader

    @FXML // fx:id="tpPerson"
    private TabPane tpPerson; // Value injected by FXMLLoader
    
    @FXML // fx:id="tpPerson"
    private ScrollPane scrollPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="btnNewAddress"
    private Button btnNewAddress; // Value injected by FXMLLoader

    @FXML // fx:id="btnRemoveCustomer"
    private Button btnRemoveCustomer; // Value injected by FXMLLoader

    @FXML // fx:id="btnEditCustomer"
    private Button btnEditCustomer; // Value injected by FXMLLoader
    
    private final ValidationSupport validationSupport = new ValidationSupport();

    /**
     * Registers validators from ControlsFX on the input fields so that
     * the user will know whether all inputs are correct.
     */
    public void initValidationSupport() {
    	// email field
    	validationSupport.registerValidator(tfEmail, (Control c, String s) ->
			ValidationResult.fromErrorIf(tfEmail,
					Messages.getString("Ui.Customer.emailValidation"),
					s == null || s.trim().equals("") || emailRegex.matcher(s).matches())
		);
    	
    	
    }
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert tabNewPerson != null : "fx:id=\"tabNewPerson\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfMobile != null : "fx:id=\"tfMobile\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfPostboxTown != null : "fx:id=\"tfPostboxTown\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert cbPrivate != null : "fx:id=\"cbPrivate\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert cbCountry != null : "fx:id=\"cbCountry\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfPostboxZip != null : "fx:id=\"tfPostboxZip\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert btnEditPerson != null : "fx:id=\"btnEditPerson\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfTown != null : "fx:id=\"tfTown\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert memoField != null : "fx:id=\"memoField\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert cbDeceased != null : "fx:id=\"cbDeceased\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfFax != null : "fx:id=\"tfFax\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert lvPeople != null : "fx:id=\"lvPeople\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfZip != null : "fx:id=\"tfZip\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert btnAddPerson != null : "fx:id=\"btnAddPerson\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfPhone != null : "fx:id=\"tfPhone\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfWebsite != null : "fx:id=\"tfWebsite\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfEmail != null : "fx:id=\"tfEmail\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfStreet != null : "fx:id=\"tfStreet\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert customerFormGrid != null : "fx:id=\"customerFormGrid\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert btnRemovePerson != null : "fx:id=\"btnRemovePerson\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tbSetAddressee != null : "fx:id=\"tbSetAddressee\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfPostbox != null : "fx:id=\"tfPostbox\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert cbState != null : "fx:id=\"cbState\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tabExistingPerson != null : "fx:id=\"tabExistingPerson\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tpPerson != null : "fx:id=\"tpPerson\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert scrollPane != null : "fx:id=\"scrollPane\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert btnNewAddress != null : "fx:id=\"btnNewAddress\" was not injected: check your FXML file 'CustomerPane.fxml'.";
        assert btnRemoveCustomer != null : "fx:id=\"btnRemoveCustomer\" was not injected: check your FXML file 'CustomerPane.fxml'.";
        assert btnEditCustomer != null : "fx:id=\"btnEditCustomer\" was not injected: check your FXML file 'CustomerPane.fxml'.";

        scrollPane.setFitToWidth(true);        
        
        initValidationSupport();
    }
	
}
