package CV.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shipping_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String ward;

    @Column(nullable = false)
    private String streetAddress;

    @Column
    private String note;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    @Column(name = "deleted")
    private Boolean deleted = false;
}
