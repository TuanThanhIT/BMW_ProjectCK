package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "MilkTea")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MilkTea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int milkTeaID;

    @ManyToOne
    @JoinColumn(name = "TypeMilkTeaID", referencedColumnName = "milkTeaTypeID")
    private MilkTeaType milkTeaType;
    
    @ManyToMany(mappedBy = "milkTeas")  // mappedBy tương ứng với trường trong Branch
    private List<Branch> branches;  // Mối quan hệ N:M với Branch

    @Column(name = "MilkTeaName", columnDefinition = "nvarchar(255)")
    private String milkTeaName;

    @Column(name = "Image")
    private String image;

    @Column(name = "Price")
    private BigDecimal price;

    @Column(name = "DiscountPrice")
    private BigDecimal discountPrice;

    @Column(name = "Description", columnDefinition = "nvarchar(max)")
    private String description;

    @Column(columnDefinition = "nvarchar(max)")
    private String introduction;
    
    @Transient
    private boolean isFavorited; // Không lưu thuộc tính này vào database

    // Getter và Setter cho isFavorited
    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean isFavorited) {
        this.isFavorited = isFavorited;
    }

    // Các getter và setter khác

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }
    
    @OneToMany(mappedBy = "milkTea", cascade = CascadeType.ALL)
    private List<BranchMilkTea> branchMilkTeas;
    
    @OneToMany(mappedBy = "milkTea", cascade = CascadeType.ALL)
    private List<Rate> userMilkTeaRates;
    
    @OneToMany(mappedBy = "milkTea", cascade = CascadeType.ALL)
    private List<Like> userMilkTeaLikes;
   
    @Override
    public String toString() {
        return "MilkTea{" +
                "milkTeaID=" + milkTeaID +
                ", milkTeaName='" + milkTeaName + '\'' +
                ", price=" + price +
                ", milkTeaTypeID=" + (milkTeaType != null ? milkTeaType.getMilkTeaTypeID() : "null") +
                '}';
    }



}