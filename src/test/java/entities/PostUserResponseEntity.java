package entities;

public class PostUserResponseEntity {
    private boolean success;
    private UserDataEntity details;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public UserDataEntity getDetails() {
        return details;
    }

    public void setDetails(UserDataEntity details) {
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
