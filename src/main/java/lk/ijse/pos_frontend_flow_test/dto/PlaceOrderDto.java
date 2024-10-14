package lk.ijse.pos_frontend_flow_test.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceOrderDto {
    private String orderId;
    private String customerId;
    private Date orderDate;
    private double paid;
    private int discount;
    private double balance;
    private List<OrderItemDto> orderItems;
}
