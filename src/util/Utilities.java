package util;

import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Comment;
import com.restfb.types.Post;
import data.Employee;

import java.awt.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.*;

public class Utilities {

    public static long getTimeDiff(Timestamp post, Timestamp done, String diffIn) {
        long diff = done.getTime() - post.getTime();
        diffIn = diffIn.toUpperCase();
        switch (diffIn) {
            case "MIN":
                return (diff / (60 * 1000));
            case "SEC":
                return (diff / 1000);
            case "HOUR":
                return (diff / (60 * 60 * 1000));
            default:
                return (diff / 1000);
        }
    }

    public static boolean isValidTimeDiff(Timestamp postTimestamp, Timestamp doneTimestamp, long timeMargin, String diffIn) {
        diffIn = diffIn.toUpperCase();
        long diff = getTimeDiff(postTimestamp, doneTimestamp, diffIn);
        return diff <= timeMargin;
    }

    public static ArrayList<Employee> getDefaulters(HashMap<String, Employee> employees, HashMap<String, Integer> tasksAssigned, int threshold) {
        ArrayList<Employee> result = new ArrayList<>();
        for (String id : tasksAssigned.keySet()) {
            if (tasksAssigned.get(id) - employees.get(id).getTaskCount() > threshold)
                result.add(employees.get(id));
        }
        return result;
    }

    public static void reportEmployees(ArrayList<Employee> defaulterEmployees) {
        for (Employee e : defaulterEmployees) {
            println("\tWarning sent to employee: " + e.getFirstName() + " " + e.getLastName() + " because of low performance (" + e.getTaskCount() + " tasks)");
        }
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
            return JobType.valueOf(t);
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
//
//    public static boolean addComment(FacebookClient facebookClient, String postId, String msg, String employeeId) {
//        User user = facebookClient.fetchObject(employeeId, User.class, Parameter.with("fields", "id, name"));
//        ArrayList<User> tags = new ArrayList<>();
//        tags.add(user);
//        Comment comment = facebookClient.publish(postId + "/comments", Comment.class, Parameter.with("message", msg), Parameter.with("to", tags));
//        if (comment != null)
//            return true;
//        else
//            return false;
//    }

    public static String getAccessToken() throws FileNotFoundException {
        return new Scanner(new File("AccessToken.txt")).useDelimiter("\\Z").next();
    }

}
