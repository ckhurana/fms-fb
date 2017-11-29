package util;

import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Comment;
import com.restfb.types.FacebookType;
import com.restfb.types.Post;
import com.restfb.types.User;
import data.Employee;
import data.Task;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class Utilities {

    public static boolean isValidTimeDiff(Timestamp timestamp, Timestamp createdTime, long timeMargin) {
        return true;
    }

    public enum JobType {
        ELECTRICIAN,
        SWEEPER,
        GARDENER,
        CARPENTER,
        MASON,
        UNKNOWN
    }

    public static JobType getJobType(String t) {
        try {
            JobType j = JobType.valueOf(t);
            return j;
        } catch (Exception e) {
            println("ERROR: Unsupported Job Type!");
            return JobType.UNKNOWN;
        }
    }

    public static void println(Object o) {
        System.out.println(o.toString());
    }

//    public static void print(Object o) {
//        System.out.print(o.toString());
//    }


    public static boolean addComment(FacebookClient facebookClient, Post post, String msg) {
        Comment comment = facebookClient.publish(post.getId() + "/comments", Comment.class, Parameter.with("message", msg));
        if (comment != null)
            return true;
        else
            return false;
    }

    public static boolean addComment(FacebookClient facebookClient, String postId, String msg) {
        Comment comment = facebookClient.publish(postId + "/comments", Comment.class, Parameter.with("message", msg));
        if (comment != null)
            return true;
        else
            return false;
    }

    public static boolean addComment(FacebookClient facebookClient, String postId, String msg, String employeeId) {
        User user = facebookClient.fetchObject(employeeId, User.class, Parameter.with("fields", "id, name"));
        ArrayList<User> tags = new ArrayList<>();
        tags.add(user);
        Comment comment = facebookClient.publish(postId + "/comments", Comment.class, Parameter.with("message", msg), Parameter.with("to", tags));
        if (comment != null)
            return true;
        else
            return false;
    }

}
