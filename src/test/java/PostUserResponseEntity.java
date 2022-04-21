public class PostUserResponseEntity {
    private boolean success;
    private UserData2 details;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public UserData2 getDetails() {
        return details;
    }

    public void setDetails(UserData2 details) {
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
