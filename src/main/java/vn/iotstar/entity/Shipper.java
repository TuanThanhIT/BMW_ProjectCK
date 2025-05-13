package vn.iotstar.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Shipper")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Shipper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int shipID;

    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "userID")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "RateID", referencedColumnName = "rateID")
    private Rate rate;

    @ManyToOne
    @JoinColumn(name = "DeliveryID", referencedColumnName = "deliveryID")
    private Delivery delivery;
    
    @ManyToOne
    @JoinColumn(name = "BranchID", referencedColumnName = "branchID")
    private Branch branch;

    @Override
    public String toString() {
        return "Shipper{" +
                "shipperID=" + shipID +
                '}';
    }

}

