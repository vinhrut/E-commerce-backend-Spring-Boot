package CV.ecommerce.configuration;

import CV.ecommerce.dto.response.APIResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.MACVerifier;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.signerKey}")
    private String SIGN_KEY;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            if (!jwsObject.verify(new MACVerifier(SIGN_KEY.getBytes()))) {
                writeUnauthorizedResponse(response, "Token không hợp lệ");
                return;
            }

            var payload = jwsObject.getPayload().toJSONObject();
            String userId = (String) payload.get("sub");
            String scope = (String) payload.get("scope");

            long exp = (Long) payload.get("exp");
            long now = System.currentTimeMillis() / 1000;
            if (now > exp) {
                writeUnauthorizedResponse(response, "Token đã hết hạn");
                return;
            }

            List<SimpleGrantedAuthority> authorities = Arrays.stream(scope.split(" "))
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            var authToken = new UsernamePasswordAuthenticationToken(userId, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (Exception e) {
            writeUnauthorizedResponse(response, "Token không hợp lệ");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        APIResponse<Object> apiResponse = APIResponse.builder()
                .code(1009)
                .message(message)
                .data(null)
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}
