package CV.ecommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShippingInfoResponse {

    String fullName;
    String phone;
    String province;
    String district;
    String ward;
    String streetAddress;
    String note;
    boolean isDefault;
}
