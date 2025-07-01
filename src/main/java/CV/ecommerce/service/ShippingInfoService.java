package CV.ecommerce.service;

import CV.ecommerce.dto.request.shippingInfo.ShippingInfoRequest;
import CV.ecommerce.entity.ShippingInfo;
import CV.ecommerce.entity.User;
import CV.ecommerce.exception.AppException;
import CV.ecommerce.repository.ShippingInfoRepository;
import CV.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShippingInfoService {

    private final ShippingInfoRepository shippingInfoRepository;
    private final UserRepository userRepository;

    public ShippingInfo create(String userId, ShippingInfoRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(404, "User not found"));

        // Nếu là địa chỉ mặc định, reset các địa chỉ khác
        if (request.getIsDefault()) {
            List<ShippingInfo> existing = shippingInfoRepository.findByUserId(userId);
            existing.forEach(info -> info.setIsDefault(false));
            shippingInfoRepository.saveAll(existing);
        }

        ShippingInfo info = ShippingInfo.builder()
                .user(user)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .province(request.getProvince())
                .district(request.getDistrict())
                .ward(request.getWard())
                .streetAddress(request.getStreetAddress())
                .note(request.getNote())
                .isDefault(request.getIsDefault())
                .deleted(false)
                .build();

        shippingInfoRepository.save(info);

        if (getByUserId(userId).size() > 5) {
            for (ShippingInfo shippingInfo : getByUserId(userId)) {
                if (!shippingInfo.getIsDefault()) {
                    delete(shippingInfo.getId());
                }
            }
        }

        return info;
    }

    public List<ShippingInfo> getAllByUserId(String userId) {
        return shippingInfoRepository.findByUserId(userId);
    }

    public List<ShippingInfo> getByUserId(String userId) {
        List<ShippingInfo> infos = shippingInfoRepository.findByUserId(userId);
        List<ShippingInfo> infoReturn = new ArrayList<>();
        for (ShippingInfo info : infos) {
            if (Boolean.FALSE.equals(info.getDeleted())) {
                infoReturn.add(info);
            }
        }
        return infoReturn;
    }

    public ShippingInfo update(Long id, ShippingInfoRequest request) {
        ShippingInfo info = shippingInfoRepository.findById(id)
                .orElseThrow(() -> new AppException(404, "Shipping info not found"));

        info.setFullName(request.getFullName());
        info.setPhone(request.getPhone());
        info.setProvince(request.getProvince());
        info.setDistrict(request.getDistrict());
        info.setWard(request.getWard());
        info.setStreetAddress(request.getStreetAddress());
        info.setNote(request.getNote());

        // Xử lý default
        if (request.getIsDefault()) {
            List<ShippingInfo> existing = shippingInfoRepository.findByUserId(info.getUser().getId());
            existing.forEach(i -> i.setIsDefault(false));
            shippingInfoRepository.saveAll(existing);
            info.setIsDefault(request.getIsDefault());
        }

        info.setIsDefault(request.getIsDefault());

        return shippingInfoRepository.save(info);
    }

    public void delete(Long id) {
        ShippingInfo info = shippingInfoRepository.findById(id)
                .orElseThrow(() -> new AppException(404, "Shipping info not found"));

        info.setDeleted(true);
        shippingInfoRepository.save(info);
    }
}
