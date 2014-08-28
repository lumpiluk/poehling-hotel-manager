/**
 * 
 */
package application.customControls;

import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.net.URL;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.CheckListView;
import org.controlsfx.dialog.DialogStyle;
import org.controlsfx.dialog.Dialogs;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;

import data.Address;
import data.DataSupervisor;
import data.Person;
import util.Messages;
import util.Messages.ErrorType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * @author lumpiluk
 *
 */
public class CustomerForm extends AbstractForm {
	
	private final Pattern emailRegex = Pattern.compile(
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	
	private DataSupervisor dataSupervisor;
	
	/** Address currently shown or edited in the form. */
	private Address currentAddress; // TODO: update or use value wherever necessary (esp. when personPane is involved)
	
	public CustomerForm() throws IOException {
		super();
	}
	
	@FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="tfMobile"
    private TextField tfMobile; // Value injected by FXMLLoader

    @FXML // fx:id="tfPostboxTown"
    private TextField tfPostboxTown; // Value injected by FXMLLoader

    @FXML // fx:id="cbCountry"
    private ComboBox<?> cbCountry; // Value injected by FXMLLoader

    @FXML // fx:id="tfPostboxZip"
    private TextField tfPostboxZip; // Value injected by FXMLLoader

    @FXML // fx:id="btnEditPerson"
    private Button btnEditPerson; // Value injected by FXMLLoader

    @FXML // fx:id="tfTown"
    private TextField tfTown; // Value injected by FXMLLoader

    @FXML // fx:id="memoArea"
    private TextArea memoArea; // Value injected by FXMLLoader

    @FXML // fx:id="tfFax"
    private TextField tfFax; // Value injected by FXMLLoader

    @FXML // fx:id="tfZip"
    private TextField tfZip; // Value injected by FXMLLoader

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

    @FXML // fx:id="tfPostbox"
    private TextField tfPostbox; // Value injected by FXMLLoader

    @FXML // fx:id="cbState"
    private ComboBox<String> cbState; // Value injected by FXMLLoader

    @FXML // fx:id="tabExistingPerson"
    private Tab tabExistingPerson; // Value injected by FXMLLoader
    
    @FXML // fx:id="tpPerson"
    private ScrollPane scrollPane; // Value injected by FXMLLoader
    
    @FXML // fx:id="btnNewAddress"
    private Button btnNewAddress; // Value injected by FXMLLoader

    @FXML // fx:id="btnRemoveCustomer"
    private Button btnRemoveCustomer; // Value injected by FXMLLoader

    @FXML // fx:id="btnEditCustomer"
    private Button btnEditCustomer; // Value injected by FXMLLoader
    
    @FXML // fx:id="customersTable"
    private TableView<?> customersTable; // Value injected by FXMLLoader
    
    @FXML // fx:id="customersToolBox"
    private HBox customersToolBox; // Value injected by FXMLLoader
    
    @FXML // fx:id="customerToolBox"
    private ToolBar customerToolBox; // Value injected by FXMLLoader
    
    @FXML // fx:id="tpFlags"
    private TitledPane tpFlags; // Value injected by FXMLLoader
    
    @FXML // fx:id="btnCustomerOk"
    private Button btnCustomerOk; // Value injected by FXMLLoader
    
    @FXML // fx:id="btnCustomerCancel"
    private Button btnCustomerCancel; // Value injected by FXMLLoader
    
    @FXML // fx:id="tfSearchQuery"
    private TextField tfSearchQuery; // Value injected by FXMLLoade
    
    @FXML // fx:id="btnCustomersSearch"
    private Button btnCustomersSearch; // Value injected by FXMLLoader
    
    private CheckComboBox<String> ccbFlagsFilter;
    
    private CheckListView<String> clvCustomerFlags;
    
    private PersonPane personPane;
    
    private final ValidationSupport validationSupport = new ValidationSupport();
    
    private ObservableList<Address> customersTableData;
    
    /** 
     * Holds the current state of the control.
     * @see changeMode 
     */
    private Mode currentMode = Mode.DISPLAY;

    /**
     * Disable or enable the customer form while still allowing to open or
     * close titled panes or viewing people in the people list.
     * @param value Iff true, form will be disabled.
     */
    private void setFormDisable(boolean value) {
    	Control[] formDisableControls = { tfStreet, tfZip, tfTown,
    			cbState, cbCountry, tfPostbox, tfPostboxZip, tfPostboxTown,
    			tfPhone, tfFax, tfMobile, tfEmail, tfWebsite, memoArea,
        		clvCustomerFlags };
    	for (Control c : formDisableControls) {
    		c.setDisable(value);
    	}
    	
    	personPane.setFormDisable(value);
    	customersToolBox.setDisable(!value);
    	customersTable.setDisable(!value);
    }
    
    /** 
     * Clears all fields of the customer form.
     * Used by changeMode() when switching to Mode.ADD.
     */
    private void clearForm() {
    	tfStreet.clear();
    	tfZip.clear();
    	tfTown.clear();
    	cbState.getSelectionModel().clearSelection();
    	cbCountry.getSelectionModel().clearSelection();
    	tfPostbox.clear();
    	tfPostboxZip.clear();
    	tfPostboxTown.clear();
    	tfPhone.clear();
    	tfFax.clear();
    	tfMobile.clear();
    	tfEmail.clear();
    	tfWebsite.clear();
    	memoArea.clear();
    	clvCustomerFlags.getCheckModel().clearSelection();
    }
    
    /**
     * Set the current state of the control.<br />
     * DISPLAY: form disabled, just show customers selected in table<br />
	 * ADD: form enabled, after pressing OK new customer will be added<br />
	 * EDIT: form enabled, after pressing OK selected customer will be updated
     * @param m the mode to change to
     */
    private void changeMode(Mode m) {
    	customerToolBox.getItems().clear();
    	switch (m) {
    	case DISPLAY:
    		setFormDisable(true);
    		customerToolBox.getItems().addAll(btnNewAddress, btnRemoveCustomer, btnEditCustomer);
    		personPane.setCurrentMode(Mode.DISPLAY);
    		break;
    	case ADD:
    		clearForm();
    		setFormDisable(false);
    		btnCustomerOk.setText(Messages.getString("Ui.Customer.Ok.New"));
    		customerToolBox.getItems().addAll(btnCustomerOk, btnCustomerCancel);
    		break;
    	case EDIT:
    		setFormDisable(false);
    		btnCustomerOk.setText(Messages.getString("Ui.Customer.Ok.Edit"));
    		customerToolBox.getItems().addAll(btnCustomerOk, btnCustomerCancel);
    		break;
    	}
    	currentMode = m;
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
    	
    	// person list should contain a person marked as addressee
    	if (personPane.getAddresseeFromListItems() == null) {
    		Dialogs.create()
    			.title(Messages.getString("Ui.Customer.Validation.DialogTitle"))
    			.message(Messages.getString("Ui.Customer.Validation.MissingAddressee"))
    			.style(DialogStyle.NATIVE)
    			.showWarning();
    		return false;
    	}
    	
    	return true;
    }
    
    /**
     * Constructs a new Address from data entered into the form by the user
     * after making sure that said data is valid.
     * @return the new address
     */
    private Address addressFromForm() {
    	if (!evaluateForm()) {
    		return null;
    	}
    	
    	Address a = new Address(dataSupervisor.getConnection());
    	a.setAddressee(personPane.getAddresseeFromListItems());
    	//a.setAddition(...)
    	a.setStreet(this.tfStreet.getText());
    	a.setShortCountry((String) this.cbCountry.getSelectionModel().getSelectedItem());
    	a.setZipCode(this.tfZip.getText());
    	a.setTown(this.tfTown.getText());
    	a.setPostbox(this.tfPostbox.getText());
    	a.setPostboxZip(this.tfPostboxZip.getText());
    	a.setPostboxTown(this.tfPostboxTown.getText());
    	a.setState(this.cbState.getSelectionModel().getSelectedItem());
    	a.setPhone(this.tfPhone.getText());
    	a.setMobile(this.tfMobile.getText());
    	a.setFax(this.tfFax.getText());
    	a.setEmail(this.tfEmail.getText());
    	a.setWebsite(this.tfWebsite.getText());
    	a.setMemo(this.memoArea.getText());
    	for (String flag : clvCustomerFlags.getCheckModel().getSelectedItems()) {
    		a.getFlags().add(flag);
    	}
    	a.setCreated(new Date(Calendar.getInstance().getTimeInMillis()));

    	return a;
    }
    
    @FXML
    void btnNewAddressClicked(ActionEvent event) {
    	changeMode(Mode.ADD);
    }

    @FXML
    void btnRemoveAddressClicked(ActionEvent event) {
    	// TODO
    	Messages.showError("Not implemented yet.", Messages.ErrorType.DB);
    }

    @FXML
    void btnEditAddressClicked(ActionEvent event) {
    	changeMode(Mode.EDIT);
    }
    
    @FXML
    void btnCustomerOkClicked(ActionEvent event) {
    	switch (currentMode) {
    	case ADD:
    		dataSupervisor.insertConcurrently(this.addressFromForm());
    		changeMode(Mode.DISPLAY);
    		break;
    	case EDIT:
    		// TODO: needs id!
    		dataSupervisor.updateConcurrently(this.addressFromForm());
    		break;
		default:
			break;
    	}
    	// TODO: reload / update table
    }

    @FXML
    void btnCustomerCancelClicked(ActionEvent event) {
    	changeMode(Mode.DISPLAY); // TODO reload selected customer from table
    }
    
    private void performCustomerSearch(String query) {
    	String[] selectedFlags = new String[ccbFlagsFilter.getCheckModel().getSelectedItems().size()];
    	ccbFlagsFilter.getCheckModel().getSelectedItems().toArray(selectedFlags);
    	dataSupervisor.searchAddresses(query, selectedFlags);
    }
    
    @FXML
    void btnCustomersSearch(ActionEvent event) {
    	performCustomerSearch(tfSearchQuery.getText());
    }

    @FXML
    void tfSearchQueryAction(ActionEvent event) {
    	// TODO: search here or on key typed (see below)?
    	performCustomerSearch(tfSearchQuery.getText());
    }

    @FXML
    void tfSearchQueryTyped(KeyEvent event) {
    	//performCustomerSearch(tfSearchQuery.getText() + event.getCharacter());
    }
    
    @SuppressWarnings("unchecked")
	private TableView<Address> getCustomersTable() {
    	return (TableView<Address>) customersTable;
    }
    
    /**
     * Registers validators from ControlsFX on the input fields so that
     * the user will know whether all inputs are correct.
     */
    private void initValidationSupport() {
    	// fixed?: implement this once bug in ControlsFX has been fixed! (supposed to happen in 8.0.7)
    	// https://bitbucket.org/controlsfx/controlsfx/issue/285/validator-support-change-resize-behavior
    	
    	// email field
    	validationSupport.registerValidator(tfEmail, false, (Control c, String s) ->
			ValidationResult.fromErrorIf(tfEmail,
					Messages.getString("Ui.Customer.Validation.Email"),
					s != null && !s.trim().equals("") && !emailRegex.matcher(s).matches())
		);
    	
    	// TODO: finish
    	
    }
    
    @SuppressWarnings("unchecked")
	private void initCustomersTable() {
    	TableColumn<Address, String> addresseeCol = new TableColumn<Address, String>(Messages.getString("Ui.Customers.Table.addresseeCol"));
    	addresseeCol.setCellValueFactory(new PropertyValueFactory<Address, String>("addresseeString"));
    	
    	TableColumn<Address, String> addressCol = new TableColumn<Address, String>(Messages.getString("Ui.Customers.Table.addressCol"));
    	TableColumn<Address, String> streetCol = new TableColumn<Address, String>(Messages.getString("Ui.Customers.Table.streetCol"));
    	streetCol.setCellValueFactory(new PropertyValueFactory<Address, String>("street"));
    	TableColumn<Address, String> zipCodeCol = new TableColumn<Address, String>(Messages.getString("Ui.Customers.Table.zipCol"));
    	zipCodeCol.setCellValueFactory(new PropertyValueFactory<Address, String>("zipCode"));
    	TableColumn<Address, String> townCol = new TableColumn<Address, String>(Messages.getString("Ui.Customers.Table.townCol"));
    	townCol.setCellValueFactory(new PropertyValueFactory<Address, String>("town"));
    	//addressCol.setCellValueFactory(new PropertyValueFactory<Address, String>("fullAddressString"));
    	addressCol.getColumns().addAll(streetCol, zipCodeCol, townCol);
    	
    	TableColumn<Address, String> phoneCol = new TableColumn<Address, String>(Messages.getString("Ui.Customers.Table.phoneCol"));
    	phoneCol.setCellValueFactory(new PropertyValueFactory<Address, String>("phone"));
    	
    	TableColumn<Address, String> mobileCol = new TableColumn<Address, String>(Messages.getString("Ui.Customers.Table.mobileCol"));
    	mobileCol.setCellValueFactory(new PropertyValueFactory<Address, String>("mobile"));
    	
    	TableColumn<Address, String> addedDateCol = new TableColumn<Address, String>(Messages.getString("Ui.Customers.Table.addedDateCol"));
    	addedDateCol.setCellValueFactory(new PropertyValueFactory<Address, String>("createdString"));
    	
    	//TableColumn<Address, String> latestBookingCol = new TableColumn<Address, String>(Messages.getString("Ui.Customers.Table.latestBookingCol"));
        
    	getCustomersTable().getColumns().addAll(addresseeCol, addressCol, phoneCol, mobileCol, addedDateCol);
        customersTableData = dataSupervisor.getAddressSearchResultObservable();
        getCustomersTable().setItems(customersTableData);
        getCustomersTable().setPlaceholder(new Text(Messages.getString("Ui.Customers.Table.Placeholder.text")));
    }
    
    public void initData(DataSupervisor dataSupervisor) {
    	this.dataSupervisor = dataSupervisor;
    	
    	// Prepare lists of flags
    	clvCustomerFlags = new CheckListView<String>();
        clvCustomerFlags.getStyleClass().add("clvCustomerFlags");
    	clvCustomerFlags.setItems(dataSupervisor.getFlagsObservable());
    	
        ccbFlagsFilter = new CheckComboBox<String>();
        ccbFlagsFilter = new CheckComboBox<String>(
        		this.dataSupervisor.getFlagsObservable());
        
        // Create new person form
        try {
        	personPane = (PersonPane) AbstractControl.getInstance(PersonPane.class);
        	personPane.initData(dataSupervisor);
        	customerFormGrid.add(personPane, 0, 1);
        	GridPane.setColumnSpan(personPane, 2);
        } catch (IOException e) {
			Messages.showError(e, ErrorType.UI);
		}
        
        // add flags check list view to according titled pane
        tpFlags.setContent(clvCustomerFlags);
        // add check combo box to tool bar
        customersToolBox.getChildren().add(1, ccbFlagsFilter);
        
        initCustomersTable();
        
        scrollPane.setFitToWidth(true);
        
        initValidationSupport();
        
        changeMode(Mode.DISPLAY);
    }
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
    	assert customersTable != null : "fx:id=\"customersTable\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfMobile != null : "fx:id=\"tfMobile\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfPostboxTown != null : "fx:id=\"tfPostboxTown\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert cbCountry != null : "fx:id=\"cbCountry\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfPostboxZip != null : "fx:id=\"tfPostboxZip\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert btnEditPerson != null : "fx:id=\"btnEditPerson\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfTown != null : "fx:id=\"tfTown\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert memoArea != null : "fx:id=\"memoArea\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfFax != null : "fx:id=\"tfFax\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfZip != null : "fx:id=\"tfZip\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfPhone != null : "fx:id=\"tfPhone\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfWebsite != null : "fx:id=\"tfWebsite\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfEmail != null : "fx:id=\"tfEmail\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfStreet != null : "fx:id=\"tfStreet\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert customerFormGrid != null : "fx:id=\"customerFormGrid\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfPostbox != null : "fx:id=\"tfPostbox\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert cbState != null : "fx:id=\"cbState\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tabExistingPerson != null : "fx:id=\"tabExistingPerson\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert scrollPane != null : "fx:id=\"scrollPane\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert btnNewAddress != null : "fx:id=\"btnNewAddress\" was not injected: check your FXML file 'CustomerPane.fxml'.";
        assert btnRemoveCustomer != null : "fx:id=\"btnRemoveCustomer\" was not injected: check your FXML file 'CustomerPane.fxml'.";
        assert btnEditCustomer != null : "fx:id=\"btnEditCustomer\" was not injected: check your FXML file 'CustomerPane.fxml'.";
        assert customersToolBox != null : "fx:id=\"customersToolBox\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert customerToolBox != null : "fx:id=\"customerToolBox\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tpFlags != null : "fx:id=\"tpFlags\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert btnCustomerOk != null : "fx:id=\"btnCustomerOk\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert btnCustomerCancel != null : "fx:id=\"btnCustomerCancel\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfSearchQuery != null : "fx:id=\"tfSearchQuery\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert btnCustomersSearch != null : "fx:id=\"btnCustomersSearch\" was not injected: check your FXML file 'CustomerForm.fxml'.";
    }
	
}
