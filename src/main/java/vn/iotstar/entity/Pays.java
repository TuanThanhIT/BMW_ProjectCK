package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "Pays")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pays {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int payID;

    @Column(name = "PayMethod", columnDefinition = "nvarchar(255)")
    private String payMethod;
    
    private BigDecimal total;

    @OneToOne(mappedBy = "payment")
	@JsonBackReference
	private Order order;
}

