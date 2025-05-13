package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Branch_MilkTea")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchMilkTea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // ID duy nhất cho mỗi dòng trong bảng trung gian

    @ManyToOne
    @JoinColumn(name = "BranchID", referencedColumnName = "branchID")
    private Branch branch; // Tham chiếu đến bảng Branch

    @ManyToOne
    @JoinColumn(name = "MilkTeaID", referencedColumnName = "milkTeaID")
    private MilkTea milkTea; // Tham chiếu đến bảng MilkTea

    @Column(name = "SellQuantity")
    private int sellQuantity; // Số lượng đã bán

    @Column(name = "StockQuantity")
    private int stockQuantity; // Số lượng trong kho

	public BranchMilkTea(Branch branch, MilkTea milkTea, int stockQuantity) {
		super();
		this.branch = branch;
		this.milkTea = milkTea;
		this.stockQuantity = stockQuantity;
	}
    
    
}