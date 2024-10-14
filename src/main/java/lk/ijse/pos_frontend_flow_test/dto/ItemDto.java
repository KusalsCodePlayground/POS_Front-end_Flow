package lk.ijse.pos_frontend_flow_test.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private String propertyId;
    private String name;
    private String description;
    private double price;
    private int qty;
}
