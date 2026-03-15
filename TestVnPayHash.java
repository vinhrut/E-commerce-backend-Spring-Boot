import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TestVnPayHash {
    public static void main(String[] args) {
        String tmnCode = "02G3POH4";
        String secretKey = "7X52YL9AZ2FWWXTXLEXV96E5NLAZQHDI";
        String vnpPayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        String returnUrl = "http://localhost:8080/api/payment/vnpay-return";

        String[] orderInfos = {
            "Payment for order: order_1"
        };
        
        for (String orderInfo : orderInfos) {
            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", tmnCode);
            vnp_Params.put("vnp_Amount", "150000000");
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", "12345678");
            vnp_Params.put("vnp_OrderInfo", orderInfo);
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", returnUrl);
            vnp_Params.put("vnp_IpAddr", "127.0.0.1");
            vnp_Params.put("vnp_CreateDate", "20260306162414");
            vnp_Params.put("vnp_ExpireDate", "20260306163914");

            System.out.println(buildUrl(vnp_Params, secretKey, vnpPayUrl, 1)); // RAW
            System.out.println(buildUrl(vnp_Params, secretKey, vnpPayUrl, 2)); // GitHub US-ASCII
            System.out.println(buildUrl(vnp_Params, secretKey, vnpPayUrl, 3)); // UTF-8 %20
            System.out.println(buildUrl(vnp_Params, secretKey, vnpPayUrl, 4)); // US-ASCII %20
            System.out.println(buildUrl(vnp_Params, secretKey, vnpPayUrl, 5)); // RAW Value, US-ASCII Key
        }
    }

    public static String buildUrl(Map<String, String> params, String secretKey, String vnpPayUrl, int method) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                try {
                    String valUtf8 = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()).replace("+", "%20");
                    String keyUtf8 = URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()).replace("+", "%20");
                    String valAscii = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString());
                    String keyAscii = URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString());
                    String valAscii20 = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()).replace("+", "%20");
                    String keyAscii20 = URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()).replace("+", "%20");
                    
                    if (method == 1) { // Raw Hash
                        hashData.append(fieldName).append('=').append(fieldValue);
                        query.append(keyAscii).append('=').append(valAscii);
                    } else if (method == 2) { // GitHub US-ASCII
                        hashData.append(fieldName).append('=').append(valAscii);
                        query.append(keyAscii).append('=').append(valAscii);
                    } else if (method == 3) { // UTF-8 %20
                        hashData.append(keyUtf8).append('=').append(valUtf8);
                        query.append(keyUtf8).append('=').append(valUtf8);
                    } else if (method == 4) { // US-ASCII %20
                        hashData.append(keyAscii20).append('=').append(valAscii20);
                        query.append(keyAscii20).append('=').append(valAscii20);
                    } else if (method == 5) { // Raw Hash, URL Query US-ASCII %20
                        hashData.append(fieldName).append('=').append(fieldValue);
                        query.append(keyAscii20).append('=').append(valAscii20);
                    }

                    if (i < fieldNames.size() - 1) {
                        query.append('&');
                        hashData.append('&');
                    }
                } catch (Exception e) {}
            }
        }
        String queryUrl = query.toString();
        String secureHash = hmacSHA512(secretKey, hashData.toString());
        return "METHOD " + method + ": " + vnpPayUrl + "?" + queryUrl + "&vnp_SecureHash=" + secureHash;
    }

    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKeySpec);
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : result) sb.append(String.format("%02x", b & 0xff));
            return sb.toString();
        } catch (Exception ex) { return ""; }
    }
}
