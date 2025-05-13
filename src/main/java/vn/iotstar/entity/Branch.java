package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

import groovy.transform.ToString;

@Entity
@Table(name = "Branch")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branchid")
    private int branchID;

    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "userID")
    private User user;
    
    
    @Column(columnDefinition = "nvarchar(max)")
    private String branchName;
    
    @Column(name = "Images")
    private String images;

    @Column(name = "Address", columnDefinition = "nvarchar(max)")
    private String address;

    @Column(name = "Description", columnDefinition = "nvarchar(max)")
    private String description;

    @Column(name = "Introduction", columnDefinition = "nvarchar(255)")
    private String introduction;
    
    @Column(name = "OpenTime")
    private LocalTime openTime;

    @Column(name = "CloseTime")
    private LocalTime closeTime;

    @Column(name = "Active", nullable = false)
    private boolean active = true;


    // Mối quan hệ N:M với MilkTea thông qua bảng trung gian
    @ManyToMany
    @JoinTable(
        name = "Branch_MilkTea",  // Tên bảng trung gian
        joinColumns = @JoinColumn(name = "BranchID"),
        inverseJoinColumns = @JoinColumn(name = "MilkTeaID")
    )
    private List<MilkTea> milkTeas;

    // Các getter và setter khác

    public List<MilkTea> getMilkTeas() {
        return milkTeas;
    }

    public void setMilkTeas(List<MilkTea> milkTeas) {
        this.milkTeas = milkTeas;
    }
    
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    private List<BranchMilkTea> branchMilkTeas;
    
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    private List<Income> incomes;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    private List<Shipper> shippers;
    
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    private List<Order> orders;

}