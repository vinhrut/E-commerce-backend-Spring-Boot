package CV.ecommerce.exception;

public enum ErrorCode {

    NO_DESCRIPTION(1001, "Description is required"),
    PRICE_VALID(1002, "Price must be greater than 0"),
    EMAIL_VALID(1002, "Email is not valid apppp"),
    VALIDATION_FAILED(1003, "Dữ liệu không hợp lệ"),
    METHOD_NOT_ALLOWED(1004, "Phương thức không được hỗ trợ"),
    INVALID_JSON(1005, "JSON không hợp lệ"),
    PARAM_MISSING(1006, "Thiếu tham số"),
    CONSTRAINT_VIOLATION(1007, "Ràng buộc không hợp lệ"),
    ACCESS_DENIED(1008, "Không có quyền truy cập"),
    ACCESS_DENIEDED(10088, "Không có quyền truy cập dau baby"),
    RESOURCE_NOT_FOUND(1009, "Không tìm thấy tài nguyên");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
