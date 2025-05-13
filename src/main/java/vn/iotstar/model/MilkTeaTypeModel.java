package vn.iotstar.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MilkTeaTypeModel {
    private int milkTeaTypeID;
    private String milkTeaTypeName;
	private Boolean isEdit = false;
}
