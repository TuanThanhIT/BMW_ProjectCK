package vn.iotstar.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    private int userID;
    private String fullName;
    private Date date;
    private String address;
    private String phone;
    private String email;
    private String userName;
    private String password;
    private boolean active;
    private String image;
    private int roleID;
	private Boolean isEdit = false;

}
