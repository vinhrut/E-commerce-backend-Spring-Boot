package CV.ecommerce.controller;

import CV.ecommerce.dto.request.shippingInfo.ShippingInfoRequest;
import CV.ecommerce.dto.response.APIResponse;
import CV.ecommerce.dto.response.ShippingInfoResponse;
import CV.ecommerce.entity.ShippingInfo;
import CV.ecommerce.mapper.ShippingInfoMapper;
import CV.ecommerce.service.ShippingInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipping-info")
@RequiredArgsConstructor
public class ShippingInfoController {

    private final ShippingInfoService shippingInfoService;
    private final ShippingInfoMapper shippingInfoMapper;

    @PostMapping(value = "/{userId}", consumes = "application/json")
    @PreAuthorize("#userId == authentication.name or hasAuthority('ADMIN')")
    public APIResponse<ShippingInfoResponse> create(
            @PathVariable String userId,
            @RequestBody @Valid ShippingInfoRequest request) {

        ShippingInfo info = shippingInfoService.create(userId, request);
        return new APIResponse<>(1000, "Create shipping info success", shippingInfoMapper.toResponse(info));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("#userId == authentication.name or hasAuthority('ADMIN')")
    public APIResponse<List<ShippingInfoResponse>> getByUser(@PathVariable String userId) {
        List<ShippingInfo> infos = shippingInfoService.getByUserId(userId);
        return new APIResponse<>(1000, "Get shipping info by user success",
                infos.stream().map(shippingInfoMapper::toResponse).toList());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public APIResponse<ShippingInfoResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid ShippingInfoRequest request) {

        ShippingInfo info = shippingInfoService.update(id, request);
        return new APIResponse<>(1000, "Update shipping info success", shippingInfoMapper.toResponse(info));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public APIResponse<?> delete(@PathVariable Long id) {
        shippingInfoService.delete(id);
        return new APIResponse<>(1000, "Delete shipping info success", null);
    }
}
