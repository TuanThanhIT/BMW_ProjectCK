package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "Users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userID;

    @Column(name = "FullName", nullable = false, columnDefinition = "nvarchar(max)")
    private String fullName;

    @Column(name = "Date", nullable = false)
    private Date date;

    @Column(name = "Address", nullable = false, columnDefinition = "nvarchar(max)")
    private String address;

    @Column(name = "Phone", nullable = false, length = 10)
    private String phone;

    @Column(name = "Email", nullable = false, columnDefinition = "nvarchar(max)")
    private String email;

    @Column(name = "Username", nullable = false, columnDefinition = "nvarchar(max)")
    private String userName;

    @Column(name = "Password", nullable = false, columnDefinition = "nvarchar(max)")
    private String password;

    @Column(name = "Active", nullable = false)
    private boolean active;

    @Column(name = "Image", columnDefinition = "nvarchar(max)")
    private String image;

    @Column(name = "RoleID", nullable = false)
    private int roleID;

    // Mối quan hệ với Order (1:N)
    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    // Mối quan hệ với Rate (1:N)
    @OneToMany(mappedBy = "user")
    private List<Rate> rates;

    // Mối quan hệ với Shipper (1:N)
    @OneToMany(mappedBy = "user")
    private List<Shipper> shippers;

    // Mối quan hệ với Role (N:1)
    @ManyToOne
    @JoinColumn(name = "RoleID", referencedColumnName = "roleID", insertable = false, updatable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Rate> userMilkTeaRates;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Like> userMilkTeaLikes;
    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", fullName='" + fullName + '\'' +
                '}';
    }
    
    // Implement cho spring
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));
	}
	@Override
	public String getUsername() {
		return userName;
	}
	@Override
	public boolean isEnabled() {
	    return active;
	}
//	@Override
//	public boolean isAccountNonLocked() {
//	    return !locked;
//	}
}
