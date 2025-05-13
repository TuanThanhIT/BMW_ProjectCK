package vn.iotstar.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Likes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "userID")
    private User user;

    @ManyToOne
    @JoinColumn(name = "MilkTeaID", referencedColumnName = "milkTeaID")
    private MilkTea milkTea;

    @Column(name = "LikedAt")
    private LocalDate likedAt; // Thời gian người dùng thích sản phẩm
}

