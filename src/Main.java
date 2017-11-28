import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.*;
import data.Student;

import java.util.List;

public class Main {
    private static final String ACCESS_TOKEN = "EAAH2Q9mqaZAcBALyKgnegJ8ZC01EV9yuphZByCnPjYZCcOTlqEATGMTeFtFaMXRZAu1YZAN534XaQ5douIXHfEyfKkZAVycNMunH4UureIyxKPUgsal7b3vX6PzawfbX9wnhY65ZCf4J80aTJvLeoef1x898BVFpbOzPZChCYKTldqVJGDjAVmwNWDQ7VrZCr0VMAyKv4HMQ1McQZDZD";
    private static final String PAGE_ID = "fiiitd";

    public static void main(String[] args) {
        FacebookClient facebookClient = new DefaultFacebookClient(ACCESS_TOKEN);
        DbConnect dbConnect = new DbConnect();

        // Get our default facebook page for the system
        Page page = facebookClient.fetchObject(PAGE_ID, Page.class);
        println(page.getId() + ": " + page.getName());

        // Fetch all the messages from the page feed
        Connection<Post> postsFetch = facebookClient.fetchConnection(PAGE_ID + "/feed",
                Post.class,
                Parameter.with("fields", "id, message, from{id, name}, updated_time, comments.limit(10){id, from{id, name}, message}")
        );

        for (List<Post> posts : postsFetch) {
            println("\nFeed:");
            for (Post post : posts) {
                String id = post.getId();
                CategorizedFacebookType user = post.getFrom();
                String msg = post.getMessage();
                println("\n" + id + ": " + msg + " :: " + user.getName());

                String[] command = msg.toUpperCase().split(" ");
                switch (command[0]) {
                    case "ADDS":
                        Student student = new Student(user.getId(), user.getName().split(" ")[0], user.getName().split(" ")[1], command[2], command[1]);
                        int i = dbConnect.addStudent(student);
                        if (i > 0) {
                            println("Succesfully added user: " + student);
                        }
                        break;
                }

                if (msg.startsWith("ADDS ")) {

                }

                if(post.getComments() != null) {
                    Comments comments = post.getComments();
                    println("\tComments:");
                    for (Comment c: comments.getData()) {
                        println("\t\t"+ c.getId() + ": " + c.getMessage() + " :: " + c.getFrom().getName());
                    }
                }
            }
        }



//        dbConnect.readDb();

    }

    public static void println(Object o) {
        System.out.println(o.toString());
    }
}
