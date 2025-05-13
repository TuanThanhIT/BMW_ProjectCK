package vn.iotstar.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iotstar.entity.Rate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipperModel {
    private int shipID;
    private int branchID;
    private int deliveryID;

    private Integer userId;
    private String userName;
    private String deliveryName;
    private BigDecimal rateValue;
    private String address;

	private Boolean isEdit = false;

}
