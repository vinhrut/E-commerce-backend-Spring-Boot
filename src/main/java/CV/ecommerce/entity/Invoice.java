package CV.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(unique = true, nullable = false, length = 100)
    private String invoiceCode; // Ví dụ: INV-20250701-001

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private LocalDateTime issuedAt;

    @Column(length = 1000)
    private String filePath; // Link PDF lưu MinIO (có thể dài)

    @Column(length = 50)
    private String status; // GENERATED, SENT, DOWNLOADED...

    // --- Snapshot tại thời điểm xuất hóa đơn ---
    @Column(length = 100)
    private String billingName;

    @Column(length = 100)
    private String billingEmail;

    @Column(length = 20)
    private String billingPhone;

    @Column(length = 1000)
    private String shippingAddress;

    @Column(length = 50)
    private String paymentMethod;

    @Column(length = 50)
    private String paymentStatus; // SUCCESS, FAILED...

    private LocalDateTime paymentDate;
}
