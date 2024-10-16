package lk.ijse.pos_frontend_flow_test.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route(value = "")
public class MainLayout extends AppLayout {

    public MainLayout() {
        VerticalLayout sideMenu = new VerticalLayout();

        // Navigation Links
        RouterLink customerViewLink = new RouterLink("Customers", CustomerView.class);
        RouterLink itemViewLink = new RouterLink("Items", ItemView.class);
        RouterLink orderViewLink = new RouterLink("Orders", OrderView.class);

        // Add links to the side menu
        sideMenu.add(customerViewLink, itemViewLink, orderViewLink);

        // Add the side menu to the layout
        addToDrawer(sideMenu);
    }
}
