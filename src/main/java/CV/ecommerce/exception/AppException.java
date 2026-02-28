package CV.ecommerce.exception;

public class AppException extends RuntimeException {

    private final Integer code;
    private final String message;

    // Constructor dùng ErrorCode enum
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    // Constructor dùng int + message
    public AppException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
