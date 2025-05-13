package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "Cart")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartID;

    @Column(name = "TotalCost")
    private BigDecimal totalCost;

    @Column(name = "TotalProduct")
    private int totalProduct;

    // Mối quan hệ với Order (1:N)
    @OneToMany(mappedBy = "cart")
    private List<Order> orders;

    // Mối quan hệ với MilkTea thông qua CartMilkTea (N:M)
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL,orphanRemoval = true)
	@JsonManagedReference
    private List<CartMilkTea> milkTeas;
    @OneToOne
    @JoinColumn(name = "userID")
    private User user;
    
}

