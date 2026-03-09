package main.java.com.ubo.tp.message.datamodel;

public class AuthResult {
    private boolean isSuccess;
    private String message;

    public AuthResult(boolean isSuccess,String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public boolean isSuccess() { return isSuccess; }
    public String getMessage() { return message; }
}
