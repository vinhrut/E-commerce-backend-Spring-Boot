package CV.ecommerce.dto.request.shippingInfo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShippingInfoRequest {

    @NotBlank(message = "FULL_NAME_REQUIRED")
    String fullName;

    @NotBlank(message = "PHONE_REQUIRED")
    @Pattern(regexp = "^(0[3-9]\\d{8})$", message = "PHONE_INVALID_FORMAT")
    String phone;

    @NotBlank(message = "PROVINCE_REQUIRED")
    String province;

    @NotBlank(message = "DISTRICT_REQUIRED")
    String district;

    @NotBlank(message = "WARD_REQUIRED")
    String ward;

    @NotBlank(message = "STREET_ADDRESS_REQUIRED")
    String streetAddress;

    String note;
    Boolean isDefault;
}
