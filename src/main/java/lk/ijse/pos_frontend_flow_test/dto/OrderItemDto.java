package lk.ijse.pos_frontend_flow_test.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private String orderId;
    private String itemId;
    private int itemCount;
    private double unitPrice;
    private double total;
}
