package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.format.annotation.NumberFormat;

@Entity
@Table(name = "Delivery")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int deliveryID;

    @Column(name = "DeliveryName", columnDefinition = "nvarchar(255)")  // Specifying length for DeliveryName
    private String deliveryName;

    @Column(name = "DeliveryType", columnDefinition = "nvarchar(255)")  // Specifying length for DeliveryType
    private String deliveryType;

    @Column(name = "ExtraCost")
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal extraCost;  // Change from double to BigDecimal for precision

    // Mối quan hệ với Shipper (1:N)
    @OneToMany(mappedBy = "delivery")
    private List<Shipper> shippers;

    @Override
    public String toString() {
        return "Delivery{" +
                "deliveryID=" + deliveryID +
                '}';
    }

}

