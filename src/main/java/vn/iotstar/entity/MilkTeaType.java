package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "MilkTeaType")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MilkTeaType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int milkTeaTypeID;

    @Column(name = "MilkTeaTypeName", columnDefinition = "nvarchar(255)")
    private String milkTeaTypeName;

    // Mối quan hệ 1:N với MilkTea
    @OneToMany(mappedBy = "milkTeaType")
    private List<MilkTea> milkTeas;
    
    // thêm để sửa stack overflow
	@Override
	public String toString() {
	    return "MilkTeaType{" +
	            "milkTeaTypeID=" + milkTeaTypeID +
	            ", milkTeaTypeName='" + milkTeaTypeName + '\'' +
	            ", milkTeasCount=" + (milkTeas != null ? milkTeas.size() : "null") + // Chỉ hiển thị số lượng MilkTea
	            '}';
	}


    
}

