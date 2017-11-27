package data;

public class Task {
    private int id;
    private String fbPostId;
    private String message;
    private String userId;

    public Task(int id, String fbPostId, String message, String userId) {
        this.id = id;
        this.fbPostId = fbPostId;
        this.message = message;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFbPostId() {
        return fbPostId;
    }

    public void setFbPostId(String fbPostId) {
        this.fbPostId = fbPostId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
