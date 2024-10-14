package lk.ijse.pos_frontend_flow_test.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import lk.ijse.pos_frontend_flow_test.dto.PlaceOrderDto;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Date;

@Route(value = "orders", layout = MainLayout.class)
public class OrderView extends VerticalLayout {
    private TextField orderIdField;
    private TextField customerIdField;
    private TextField orderDateField;
    private TextField paidField;
    private TextField discountField;
    private TextField balanceField;
    private TextField searchField;
    private Button saveButton;
    private Button updateButton;
    private Button deleteButton;
    private Button searchButton;
    private Grid<PlaceOrderDto> orderGrid;

    public OrderView() {
        addClassName("order-view");

        orderIdField = new TextField("Order ID");
        customerIdField = new TextField("Customer ID");
        orderDateField = new TextField("Order Date (yyyy-MM-dd)");
        paidField = new TextField("Paid Amount");
        discountField = new TextField("Discount");
        balanceField = new TextField("Balance");
        searchField = new TextField("Search by Order ID");

        saveButton = new Button("Save", event -> saveOrder());
        updateButton = new Button("Update", event -> updateOrder());
        deleteButton = new Button("Delete", event -> deleteOrder());
        searchButton = new Button("Search", event -> searchOrderById());

        orderGrid = new Grid<>(PlaceOrderDto.class);
        orderGrid.addColumn(PlaceOrderDto::getOrderId).setHeader("Order ID");
        orderGrid.addColumn(PlaceOrderDto::getCustomerId).setHeader("Customer ID");
        orderGrid.addColumn(order -> order.getOrderDate().toString()).setHeader("Order Date");
        orderGrid.addColumn(PlaceOrderDto::getPaid).setHeader("Paid");
        orderGrid.addColumn(PlaceOrderDto::getDiscount).setHeader("Discount");
        orderGrid.addColumn(PlaceOrderDto::getBalance).setHeader("Balance");

        orderGrid.addSelectionListener(event -> {
            event.getFirstSelectedItem().ifPresent(this::populateForm);
        });

        HorizontalLayout searchLayout = new HorizontalLayout(searchField, searchButton);

        add(searchLayout, orderIdField, customerIdField, orderDateField, paidField, discountField, balanceField, saveButton, updateButton, deleteButton, orderGrid);
    }

    @PostConstruct
    public void init() {
        loadOrders();
    }

    private void saveOrder() {
        PlaceOrderDto newOrder = new PlaceOrderDto();
        newOrder.setOrderId(orderIdField.getValue());
        newOrder.setCustomerId(customerIdField.getValue());
        newOrder.setOrderDate(new Date()); // or parse from the field if needed
        newOrder.setPaid(Double.parseDouble(paidField.getValue()));
        newOrder.setDiscount(Integer.parseInt(discountField.getValue()));
        newOrder.setBalance(Double.parseDouble(balanceField.getValue()));

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity("http://localhost:8080/api/v1/order", newOrder, Void.class);
        Notification.show("Order saved successfully!");
        loadOrders();
    }

    private void updateOrder() {
        PlaceOrderDto selectedOrder = orderGrid.asSingleSelect().getValue();
        if (selectedOrder != null) {
            selectedOrder.setCustomerId(customerIdField.getValue());
            selectedOrder.setPaid(Double.parseDouble(paidField.getValue()));
            selectedOrder.setDiscount(Integer.parseInt(discountField.getValue()));
            selectedOrder.setBalance(Double.parseDouble(balanceField.getValue()));

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.put("http://localhost:8080/api/v1/order/" + selectedOrder.getOrderId(), selectedOrder);
            Notification.show("Order updated successfully!");
            loadOrders();
        }
    }

    private void deleteOrder() {
        PlaceOrderDto selectedOrder = orderGrid.asSingleSelect().getValue();
        if (selectedOrder != null) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.delete("http://localhost:8080/api/v1/order/" + selectedOrder.getOrderId());
            Notification.show("Order deleted successfully!");
            loadOrders();
        }
    }

    private void loadOrders() {
        RestTemplate restTemplate = new RestTemplate();
        PlaceOrderDto[] orders = restTemplate.getForObject("http://localhost:8080/api/v1/order", PlaceOrderDto[].class);
        orderGrid.setItems(Collections.emptyList());
        orderGrid.setItems(orders);
    }

    private void searchOrderById() {
        String orderId = searchField.getValue();
        if (orderId.isEmpty()) {
            Notification.show("Please enter an Order ID to search.");
            return;
        }

        RestTemplate restTemplate = new RestTemplate();
        PlaceOrderDto order = restTemplate.getForObject("http://localhost:8080/api/v1/order/" + orderId, PlaceOrderDto.class);
        if (order != null) {
            orderGrid.setItems(Collections.singletonList(order));
            populateForm(order);
            Notification.show("Order found: " + order.getOrderId());
        } else {
            Notification.show("No order found with ID: " + orderId);
            orderGrid.setItems(Collections.emptyList());
        }
    }

    private void populateForm(PlaceOrderDto order) {
        orderIdField.setValue(order.getOrderId());
        customerIdField.setValue(order.getCustomerId());
        orderDateField.setValue(order.getOrderDate().toString());
        paidField.setValue(String.valueOf(order.getPaid()));
        discountField.setValue(String.valueOf(order.getDiscount()));
        balanceField.setValue(String.valueOf(order.getBalance()));
    }
}