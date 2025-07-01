package CV.ecommerce.controller;

import CV.ecommerce.dto.response.APIResponse;
import CV.ecommerce.dto.response.DashboardSummaryResponse;
import CV.ecommerce.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/summary")
    public APIResponse<DashboardSummaryResponse> getDashboardSummary() {
        return new APIResponse<>(1000, "Dashboard summary success", dashboardService.getDashboardSummary());
    }
}
