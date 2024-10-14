package lk.ijse.pos_frontend_flow_test.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import lk.ijse.pos_frontend_flow_test.dto.CustomerDto;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Route(value = "customers", layout = MainLayout.class)
public class CustomerView extends VerticalLayout{
    private TextField nameField;
    private TextField emailField;
    private TextField addressField;
    private TextField searchField;
    private Button saveButton;
    private Button updateButton;
    private Button deleteButton;
    private Button searchButton;
    private Grid<CustomerDto> customerGrid;

    public CustomerView() {
        addClassName("customer-view");

        nameField = new TextField("Name");
        emailField = new TextField("Email");
        addressField = new TextField("Address");
        searchField = new TextField("Search by ID");

        saveButton = new Button("Save", event -> saveCustomer());
        updateButton = new Button("Update", event -> updateCustomer());
        deleteButton = new Button("Delete", event -> deleteCustomer());
        searchButton = new Button("Search", event -> searchCustomerById());

        customerGrid = new Grid<>(CustomerDto.class);
        customerGrid.addColumn(CustomerDto::getPropertyId).setHeader("ID");
        customerGrid.addColumn(CustomerDto::getName).setHeader("Name");
        customerGrid.addColumn(CustomerDto::getEmail).setHeader("Email");
        customerGrid.addColumn(CustomerDto::getAddress).setHeader("Address");

        customerGrid.addSelectionListener(event -> {
            event.getFirstSelectedItem().ifPresent(this::populateForm);
        });

        HorizontalLayout searchLayout = new HorizontalLayout(searchField, searchButton);

        add(searchLayout, nameField, emailField, addressField, saveButton, updateButton, deleteButton, customerGrid);
    }

    @PostConstruct
    public void init() {
        loadCustomers();
    }

    private void saveCustomer() {
        Notification.show("Customer save button clicked..");
        CustomerDto newCustomer = new CustomerDto(null, nameField.getValue(), emailField.getValue(), addressField.getValue(), true);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity("http://localhost:8080/api/v1/customer", newCustomer, Void.class);
        Notification.show("Customer saved successfully!");
        loadCustomers();
    }

    private void updateCustomer() {
        CustomerDto selectedCustomer = customerGrid.asSingleSelect().getValue();
        if (selectedCustomer != null) {
            selectedCustomer.setName(nameField.getValue());
            selectedCustomer.setEmail(emailField.getValue());
            selectedCustomer.setAddress(addressField.getValue());
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.put("http://localhost:8080/api/v1/customer/" + selectedCustomer.getPropertyId(), selectedCustomer);
            Notification.show("Customer updated successfully!");
            loadCustomers();
        }
    }

    private void deleteCustomer() {
        CustomerDto selectedCustomer = customerGrid.asSingleSelect().getValue();
        if (selectedCustomer != null) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.delete("http://localhost:8080/api/v1/customer/" + selectedCustomer.getPropertyId());
            Notification.show("Customer deleted successfully!");
            loadCustomers();
        }
    }

    private void loadCustomers() {
        RestTemplate restTemplate = new RestTemplate();
        CustomerDto[] customers = restTemplate.getForObject("http://localhost:8080/api/v1/customer", CustomerDto[].class);
        customerGrid.setItems(Collections.emptyList());
        customerGrid.setItems(customers);
    }

    private void searchCustomerById() {
        String id = searchField.getValue();
        if (id.isEmpty()) {
            Notification.show("Please enter an ID to search.");
            return;
        }

        RestTemplate restTemplate = new RestTemplate();
        CustomerDto customer = restTemplate.getForObject("http://localhost:8080/api/v1/customer/" + id, CustomerDto.class);
        if (customer != null) {
            customerGrid.setItems(Collections.singletonList(customer));
            populateForm(customer);
            Notification.show("Customer found: " + customer.getName());
        } else {
            Notification.show("No customer found with ID: " + id);
            customerGrid.setItems(Collections.emptyList());
        }
    }

    private void populateForm(CustomerDto customer) {
        nameField.setValue(customer.getName());
        emailField.setValue(customer.getEmail());
        addressField.setValue(customer.getAddress());
    }
}
