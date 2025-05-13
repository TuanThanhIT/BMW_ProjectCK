package vn.iotstar.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iotstar.enums.OrderStatus;

@Entity
@Table(name = "Orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int orderID;
	@Column(columnDefinition = "nvarchar(max)")
	private String shipAddress;
		
	@ManyToOne
	@JoinColumn(name = "UserID", referencedColumnName = "userID")
	private User user;
	@ManyToOne
    @JoinColumn(name = "BranchID", referencedColumnName = "branchID")
    private Branch branch; // Kết nối với bảng Branch

	@ManyToOne
	@JoinColumn(name = "CartID", referencedColumnName = "cartID")
	private Cart cart; // Một đơn hàng thuộc về một giỏ hàng

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "payID")
	@JsonManagedReference
	private Pays payment;

	@Override
	public String toString() {
		return "Order{" + "orderID=" + orderID + ", userID=" + (user != null ? user.getUserID() : "null") + '}';
	}
}
