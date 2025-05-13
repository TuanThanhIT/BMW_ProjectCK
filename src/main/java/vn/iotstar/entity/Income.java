package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "income") // Đặt tên bảng nhất quán
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "date", nullable = false) // Viết thường để nhất quán
    private LocalDate date;

    @Column(name = "value", nullable = false)
    private double value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branchid", referencedColumnName = "branchid", nullable = false) // Tên cột consistent
    private Branch branch;

    // Các hàm tạo đã được Lombok xử lý
}