package lk.ijse.pos_frontend_flow_test.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private String propertyId;
    private String name;
    private String email;
    private String address;
    private boolean availability;
}