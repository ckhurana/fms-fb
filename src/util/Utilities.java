package util;

import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Comment;
import com.restfb.types.FacebookType;
import com.restfb.types.Post;
import com.restfb.types.User;
import data.Employee;
import data.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Utilities {

    public static boolean isValidTimeDiff(Timestamp postTimestamp, Timestamp doneTimestamp, long timeMargin, String diffIn) {
        long diff = postTimestamp.getTime() - doneTimestamp.getTime();
        diffIn = diffIn.toUpperCase();
        if (diffIn.equals("MIN"))
            return (diff / (60 * 1000)) <= timeMargin;
        else if (diffIn.equals("SEC"))
            return (diff / 1000) <= timeMargin;
        else if (diffIn.equals("HOUR"))
            return (diff / (60 * 60 * 1000)) <= timeMargin;
        else return isValidTimeDiff(postTimestamp, doneTimestamp, timeMargin, "sec");
    }

    public enum JobType {
        UNKNOWN,
        ELECTRICIAN,
        SWEEPER,
        GARDENER,
        CARPENTER,
        MASON
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

    public static String getAccessToken() throws FileNotFoundException {
        return new Scanner(new File("AccessToken.txt")).useDelimiter("\\Z").next();
    }

}
