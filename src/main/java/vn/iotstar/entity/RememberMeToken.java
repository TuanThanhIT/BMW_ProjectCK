package vn.iotstar.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "remember_me_tokens")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RememberMeToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Dùng IDENTITY thay cho AUTO_INCREMENT
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;
}
