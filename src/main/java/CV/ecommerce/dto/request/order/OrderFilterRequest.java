package CV.ecommerce.dto.request.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderFilterRequest {
    private String status;
    private String userId;
    private String fromDate;
    private String toDate;
    private Double minTotalAmount;
    private Double maxTotalAmount;

}
