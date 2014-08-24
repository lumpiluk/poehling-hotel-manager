/**
 * 
 */
package application.customControls;

import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.Observable;
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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

/**
 * @author lumpiluk
 *
 */
public class CustomerForm extends AbstractForm {

	/** Items used in people list view of current address. */
	public class PersonItem {
		private Person person;
		private BooleanProperty marked = new SimpleBooleanProperty(false);
		private ReadOnlyStringWrapper name = new ReadOnlyStringWrapper();
		
		public PersonItem(Person p, boolean addressee) {
			this.person = p;
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
		
		public StringProperty nameProperty() { return name; }
		
		private void updateText() {
			StringBuilder sb = new StringBuilder();
			if (marked.get())
				sb.append("Addressat: ");
			sb.append(person.getTitle());
			sb.append(" ");
			sb.append(person.getFirstNames());
			sb.append(" ");
			sb.append(person.getSurnames());
			name.set(sb.toString());
		}
	}
	
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

    @FXML // fx:id="memoArea"
    private TextArea memoArea; // Value injected by FXMLLoader

    @FXML // fx:id="cbDeceased"
    private CheckBox cbDeceased; // Value injected by FXMLLoader

    @FXML // fx:id="tfFax"
    private TextField tfFax; // Value injected by FXMLLoader

    @FXML // fx:id="peopleTable"
    private TableView<?> peopleTable; // Value injected by FXMLLoader
    
    @FXML // fx:id="btnPersonOk"
    private Button btnPersonOk; // Value injected by FXMLLoader
    
    @FXML // fx:id="btnPersonCancel"
    private Button btnPersonCancel; // Value injected by FXMLLoader

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
    
    @FXML // fx:id="peopleTools"
    private ToolBar peopleTools; // Value injected by FXMLLoader
    
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
    	Control[] formDisableControls = { peopleTools, tfStreet, tfZip, tfTown,
    			cbState, cbCountry, tfPostbox, tfPostboxZip, tfPostboxTown,
    			tfPhone, tfFax, tfMobile, tfEmail, tfWebsite, memoArea,
        		clvCustomerFlags };
    	for (Control c : formDisableControls) {
    		c.setDisable(value);
    	}
    	
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
    		changePersonMode(Mode.DISPLAY);
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
    
    private void changePersonMode(Mode m) {
    	peopleTools.getItems().clear();
    	switch (m) {
    	case DISPLAY:
    		peopleTools.getItems().addAll(btnAddPerson, btnRemovePerson, btnEditPerson);
    		break;
    	case ADD:
    		btnPersonOk.setText(Messages.getString("Ui.Customer.People.Ok.New"));
    		peopleTools.getItems().addAll(btnPersonOk, btnPersonCancel);
    		break;
    	case EDIT:
    		btnPersonOk.setText(Messages.getString("Ui.Customer.People.Ok.Edit"));
    		peopleTools.getItems().addAll(btnPersonOk, btnPersonCancel);
    		break;
    	}
    	personPane.setCurrentMode(m);
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
    	if (getAddresseeFromListItems() == null) {
    		Dialogs.create()
    			.title(Messages.getString("Ui.Customer.Validation.DialogTitle"))
    			.message(Messages.getString("Ui.Customer.Validation.MissingAddressee"))
    			.style(DialogStyle.NATIVE)
    			.showWarning();
    		return false;
    	}
    	
    	return true;
    }
    
    private Person getAddresseeFromListItems() {
    	for (PersonItem pi : getPeopleTable().getItems()) {
    		if (pi.markedProperty().get()) {
    			return pi.getPerson();
    		}
    	}
    	return null;
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
    	a.setAddressee(getAddresseeFromListItems());
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
    	a.setAdded(new Date(Calendar.getInstance().getTimeInMillis()));

    	return a;
    }
    
    @FXML
    void btnNewAddressClicked(ActionEvent event) {
    	changeMode(Mode.ADD);
    }

    @FXML
    void btnRemoveAddressClicked(ActionEvent event) {

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
    	}
    	// TODO: reload / update table
    }

    @FXML
    void btnCustomerCancelClicked(ActionEvent event) {
    	changeMode(Mode.DISPLAY); // TODO reload selected customer from table
    }
    
    @FXML
    void btnCustomersSearch(ActionEvent event) {
    	String[] selectedFlags = new String[ccbFlagsFilter.getCheckModel().getSelectedItems().size()];
    	ccbFlagsFilter.getCheckModel().getSelectedItems().toArray(selectedFlags);
    	dataSupervisor.searchAddresses(tfSearchQuery.getText(), selectedFlags);
    }
    
    @FXML
    void btnAddPersonClicked(ActionEvent event) {
    	changePersonMode(Mode.ADD);
    }

    @FXML
    void btnRemovePersonClicked(ActionEvent event) {
    	// TODO!
    }

    @FXML
    void btnEditPersonClicked(ActionEvent event) {
    	changePersonMode(Mode.EDIT);
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
    	switch (personPane.getCurrentMode()) {
    	case ADD:
    		Person p = personPane.personFromForm();
    		PersonItem pi = new PersonItem(p, getPeopleTable().getItems().isEmpty()); // automagically mark as addressee if this is the first item
    		getPeopleTable().getItems().add(pi);
    		break;
    	case EDIT:
    		personPane.updatePerson(getPeopleTable().getSelectionModel()
    				.getSelectedItem().getPerson());
    		break;
		default:
			break;
    	}
    	changePersonMode(Mode.DISPLAY);
    }

    @FXML
    void btnPersonCancelClicked(ActionEvent event) {
    	changePersonMode(Mode.DISPLAY);
    }
    
    @SuppressWarnings("unchecked")
	private TableView<Address> getCustomersTable() {
    	return (TableView<Address>) customersTable;
    }
    
    private TableView<PersonItem> getPeopleTable() {
    	return (TableView<PersonItem>) peopleTable;
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
    
    @SuppressWarnings("unchecked")
	private void initCustomersTable() {
    	TableColumn<Address, String> addresseeCol = new TableColumn<Address, String>(Messages.getString("Ui.Customers.Table.addresseeCol"));
    	addresseeCol.setCellValueFactory(new PropertyValueFactory<Address, String>("addresseeString"));
    	
    	TableColumn<Address, String> addressCol = new TableColumn<Address, String>(Messages.getString("Ui.Customers.Table.addressCol"));
    	addressCol.setCellValueFactory(new PropertyValueFactory<Address, String>("fullAddressString"));
    	
    	TableColumn<Address, String> phoneCol = new TableColumn<Address, String>(Messages.getString("Ui.Customers.Table.phoneCol"));
    	phoneCol.setCellValueFactory(new PropertyValueFactory<Address, String>("phone"));
    	
    	TableColumn<Address, String> mobileCol = new TableColumn<Address, String>(Messages.getString("Ui.Customers.Table.mobileCol"));
    	phoneCol.setCellValueFactory(new PropertyValueFactory<Address, String>("cellphone"));
    	
    	TableColumn<Address, String> addedDateCol = new TableColumn<Address, String>(Messages.getString("Ui.Customers.Table.addedDateCol"));
    	addressCol.setCellValueFactory(new PropertyValueFactory<Address, String>("addedString"));
    	
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
        	customerFormGrid.add(personPane, 0, 3);
        	GridPane.setColumnSpan(personPane, 2);
        } catch (IOException e) {
			Messages.showError(e, ErrorType.UI);
		}
        initPeopleTable();
        
        // add flags check list view to according titled pane
        tpFlags.setContent(clvCustomerFlags);
        // add checked combo box to tool bar
        customersToolBox.getChildren().add(1, ccbFlagsFilter);
        
        initCustomersTable();
        
        scrollPane.setFitToWidth(true);
        
        initValidationSupport();
        
        changePersonMode(Mode.DISPLAY);
        changeMode(Mode.DISPLAY);
    }
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
    	assert customersTable != null : "fx:id=\"customersTable\" was not injected: check your FXML file 'CustomerForm.fxml'.";
    	assert tabNewPerson != null : "fx:id=\"tabNewPerson\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfMobile != null : "fx:id=\"tfMobile\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfPostboxTown != null : "fx:id=\"tfPostboxTown\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert cbPrivate != null : "fx:id=\"cbPrivate\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert cbCountry != null : "fx:id=\"cbCountry\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfPostboxZip != null : "fx:id=\"tfPostboxZip\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert btnEditPerson != null : "fx:id=\"btnEditPerson\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfTown != null : "fx:id=\"tfTown\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert memoArea != null : "fx:id=\"memoArea\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert cbDeceased != null : "fx:id=\"cbDeceased\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfFax != null : "fx:id=\"tfFax\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert peopleTable != null : "fx:id=\"peopleTable\" was not injected: check your FXML file 'CustomerForm.fxml'.";
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
        assert scrollPane != null : "fx:id=\"scrollPane\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert btnNewAddress != null : "fx:id=\"btnNewAddress\" was not injected: check your FXML file 'CustomerPane.fxml'.";
        assert btnRemoveCustomer != null : "fx:id=\"btnRemoveCustomer\" was not injected: check your FXML file 'CustomerPane.fxml'.";
        assert btnEditCustomer != null : "fx:id=\"btnEditCustomer\" was not injected: check your FXML file 'CustomerPane.fxml'.";
        assert customersToolBox != null : "fx:id=\"customersToolBox\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert customerToolBox != null : "fx:id=\"customerToolBox\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tpFlags != null : "fx:id=\"tpFlags\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert peopleTools != null : "fx:id=\"peopleTools\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert btnCustomerOk != null : "fx:id=\"btnCustomerOk\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert btnCustomerCancel != null : "fx:id=\"btnCustomerCancel\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert tfSearchQuery != null : "fx:id=\"tfSearchQuery\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert btnCustomersSearch != null : "fx:id=\"btnCustomersSearch\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert btnPersonCancel != null : "fx:id=\"btnPersonCancel\" was not injected: check your FXML file 'CustomerForm.fxml'.";
        assert btnPersonOk != null : "fx:id=\"btnPersonOk\" was not injected: check your FXML file 'CustomerForm.fxml'.";
    }
	
}
