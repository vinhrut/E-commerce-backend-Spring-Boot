package CV.ecommerce.mapper;

import CV.ecommerce.dto.response.ShippingInfoResponse;
import CV.ecommerce.entity.ShippingInfo;
import org.springframework.stereotype.Component;

@Component
public class ShippingInfoMapper {
    public ShippingInfoResponse toResponse(ShippingInfo info) {
        return ShippingInfoResponse.builder()
                .fullName(info.getFullName())
                .phone(info.getPhone())
                .province(info.getProvince())
                .district(info.getDistrict())
                .ward(info.getWard())
                .streetAddress(info.getStreetAddress())
                .note(info.getNote())
                .isDefault(info.getIsDefault())
                .build();
    }
}
