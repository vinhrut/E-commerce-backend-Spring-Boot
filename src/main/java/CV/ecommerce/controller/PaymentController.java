package CV.ecommerce.controller;

import CV.ecommerce.configuration.VnPayConfig;
import CV.ecommerce.dto.response.PaymentResponse;
import CV.ecommerce.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final VnPayConfig vnPayConfig;

    @PreAuthorize("@securityService.isOrderOwner(#orderId) or hasAuthority('ADMIN')")
    @PostMapping("/create-url/{orderId}")
    public PaymentResponse createPaymentUrl(@PathVariable String orderId, HttpServletRequest request) {
        String clientIp = vnPayConfig.getIpAddress(request);
        return paymentService.createPaymentForOrder(orderId, clientIp);
    }

    @GetMapping("/vnpay-return")
    public PaymentResponse handleVnpayReturn(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        for (Enumeration<String> en = request.getParameterNames(); en.hasMoreElements();) {
            String key = en.nextElement();
            String value = request.getParameter(key);
            if (value != null && !value.isEmpty()) {
                params.put(key, value);
            }
        }

        return paymentService.handleVnpayReturn(params);
    }
}
