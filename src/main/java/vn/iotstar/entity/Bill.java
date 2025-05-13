package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "Bill")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int billID;

    @ManyToOne
    @JoinColumn(name = "OrderID", referencedColumnName = "orderID")
    private Order order;

    @Column(name = "Date")
    private Date date;

    @Column(name = "Cost", precision = 18, scale = 0)
    private BigDecimal cost;

    @Column(name = "ShipFee", precision = 18, scale = 0)
    private BigDecimal shipFee;

    @Column(name = "Total", precision = 18, scale = 0)
    private BigDecimal total;
}
