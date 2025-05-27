package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "RefreshToken")
@Data
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "Token", nullable = false, unique = true)
    private String token;

    @Column(name = "Username", nullable = false)
    private String username;

    @Column(name = "ExpiryDate", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;

    @Column(name = "Revoked", nullable = false)
    private boolean revoked;

    @Column(name = "CreatedAt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    private String ip;
    private String userAgent;
}
