package data;


import util.Utilities;

import java.sql.Timestamp;

public class Task {
    private int id;
    private String fbPostId;
    private String complaint;
    private String userId;
    private String employeeId;
    private String loc;
    private Utilities.JobType type;
    private Timestamp timestamp;
    private boolean isDone;
    private boolean isProcessed;
    private boolean isError;
    private int feedback;
    private boolean isLate;

    public Task(String fbPostId, String complaint, String userId, String loc, Utilities.JobType type, Timestamp timestamp) {
        this.fbPostId = fbPostId;
        this.complaint = complaint;
        this.userId = userId;
        this.loc = loc;
        this.type = type;
        this.timestamp = timestamp;
        this.isDone = false;
        this.isError = false;
        this.isProcessed = false;
        this.isLate = false;
        this.feedback = -2;
        this.employeeId = "";
    }


    public Task(String fbPostId, String complaint, String userId, String loc, Utilities.JobType type) {
        this.fbPostId = fbPostId;
        this.complaint = complaint;
        this.userId = userId;
        this.loc = loc;
        this.type = type;
        this.timestamp = null;
        this.isDone = false;
        this.isError = false;
        this.isProcessed = false;
        this.isLate = false;
        this.feedback = -2;
        this.employeeId = "";
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

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public Utilities.JobType getType() {
        return type;
    }

    public void setType(Utilities.JobType type) {
        this.type = type;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public boolean isLate() {
        return isLate;
    }

    public void setLate(boolean late) {
        isLate = late;
    }

    public int getFeedback() {
        return feedback;
    }

    public void setFeedback(int feedback) {
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return getId() + ": " + getComplaint() + " (" + isProcessed() + ")";
    }
}
