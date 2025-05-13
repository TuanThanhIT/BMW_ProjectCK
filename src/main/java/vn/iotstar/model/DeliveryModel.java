package vn.iotstar.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryModel {

    private int deliveryID;
    private String deliveryName;
    private String deliveryType;
    private BigDecimal extraCost;  // Change from double to BigDecimal for precision

	private Boolean isEdit = false;

}
