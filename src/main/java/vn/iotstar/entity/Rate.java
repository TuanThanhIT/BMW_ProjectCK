package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Rate")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int rateID;

    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "userID")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "MilkTeaID", referencedColumnName = "milkTeaID")
    private MilkTea milkTea;

    @Column(name = "Comment", columnDefinition = "nvarchar(max)")
    private String comment;
    
    @Column(name = "PostTime")
    private LocalDate postTime;

    @Column(name = "RateValue")
    private BigDecimal rateValue;
    
    @Override
    public String toString() {
        return "Rate{" +
                "rateID=" + rateID +
                ", milkTeaID=" + (milkTea != null ? milkTea.getMilkTeaID() : "null") +
                ", rating=" + rateValue +
                '}';
    }

}

