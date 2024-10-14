package lk.ijse.pos_frontend_flow_test.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import lk.ijse.pos_frontend_flow_test.dto.ItemDto;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Route(value = "items", layout = MainLayout.class)
public class ItemView extends VerticalLayout {
    private TextField propertyIdField;
    private TextField nameField;
    private TextField descriptionField;
    private TextField priceField;
    private TextField qtyField;
    private TextField searchField;
    private Button saveButton;
    private Button updateButton;
    private Button deleteButton;
    private Button searchButton;
    private Grid<ItemDto> itemGrid;

    public ItemView() {
        addClassName("item-view");

        propertyIdField = new TextField("Property ID");
        nameField = new TextField("Name");
        descriptionField = new TextField("Description");
        priceField = new TextField("Price");
        qtyField = new TextField("Quantity");
        searchField = new TextField("Search by Property ID");

        saveButton = new Button("Save", event -> saveItem());
        updateButton = new Button("Update", event -> updateItem());
        deleteButton = new Button("Delete", event -> deleteItem());
        searchButton = new Button("Search", event -> searchItemById());

        itemGrid = new Grid<>(ItemDto.class);
        itemGrid.addColumn(ItemDto::getPropertyId).setHeader("Property ID");
        itemGrid.addColumn(ItemDto::getName).setHeader("Name");
        itemGrid.addColumn(ItemDto::getDescription).setHeader("Description");
        itemGrid.addColumn(ItemDto::getPrice).setHeader("Price");
        itemGrid.addColumn(ItemDto::getQty).setHeader("Quantity");

        itemGrid.addSelectionListener(event -> {
            event.getFirstSelectedItem().ifPresent(this::populateForm);
        });

        HorizontalLayout searchLayout = new HorizontalLayout(searchField, searchButton);
        add(searchLayout, propertyIdField, nameField, descriptionField, priceField, qtyField, saveButton, updateButton, deleteButton, itemGrid);
    }

    @PostConstruct
    public void init() {
        loadItems();
    }

    private void saveItem() {
        ItemDto newItem = new ItemDto(propertyIdField.getValue(), nameField.getValue(), descriptionField.getValue(), Double.parseDouble(priceField.getValue()), Integer.parseInt(qtyField.getValue()));
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity("http://localhost:8080/api/v1/item", newItem, Void.class);
        Notification.show("Item saved successfully!");
        loadItems();
    }

    private void updateItem() {
        ItemDto selectedItem = itemGrid.asSingleSelect().getValue();
        if (selectedItem != null) {
            selectedItem.setName(nameField.getValue());
            selectedItem.setDescription(descriptionField.getValue());
            selectedItem.setPrice(Double.parseDouble(priceField.getValue()));
            selectedItem.setQty(Integer.parseInt(qtyField.getValue()));
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.put("http://localhost:8080/api/v1/item/" + selectedItem.getPropertyId(), selectedItem);
            Notification.show("Item updated successfully!");
            loadItems();
        }
    }

    private void deleteItem() {
        ItemDto selectedItem = itemGrid.asSingleSelect().getValue();
        if (selectedItem != null) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.delete("http://localhost:8080/api/v1/item/" + selectedItem.getPropertyId());
            Notification.show("Item deleted successfully!");
            loadItems();
        }
    }

    private void loadItems() {
        RestTemplate restTemplate = new RestTemplate();
        ItemDto[] items = restTemplate.getForObject("http://localhost:8080/api/v1/item", ItemDto[].class);
        itemGrid.setItems(Collections.emptyList());
        itemGrid.setItems(items);
    }

    private void searchItemById() {
        String id = searchField.getValue();
        if (id.isEmpty()) {
            Notification.show("Please enter a Property ID to search.");
            return;
        }

        RestTemplate restTemplate = new RestTemplate();
        ItemDto item = restTemplate.getForObject("http://localhost:8080/api/v1/item/" + id, ItemDto.class);
        if (item != null) {
            itemGrid.setItems(Collections.singletonList(item));
            populateForm(item);
            Notification.show("Item found: " + item.getName());
        } else {
            Notification.show("No item found with Property ID: " + id);
            itemGrid.setItems(Collections.emptyList());
        }
    }

    private void populateForm(ItemDto item) {
        propertyIdField.setValue(item.getPropertyId());
        nameField.setValue(item.getName());
        descriptionField.setValue(item.getDescription());
        priceField.setValue(String.valueOf(item.getPrice()));
        qtyField.setValue(String.valueOf(item.getQty()));
    }
}
