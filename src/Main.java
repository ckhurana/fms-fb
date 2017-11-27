import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.*;
import java.util.List;

public class Main {
    private static final String ACCESS_TOKEN = "EAAH2Q9mqaZAcBAMLFZCJbwi6OpvnNt2FnbZCb5YMC4yoRaflnOy84wgU9FRnH4A9mZBtrMdAiNi9LyO8zVsxM9vLhc8wGoDIvc7AV5Qj3h93NXUz7h44mh99u1zb9GZB4LZBrKs7tqOycNKKLdLzTBSkIg976TuMZCfH0mzF6RNq0ctix5XhE3e3nZBokN9FsQoZD";
    private static final String PAGE_ID = "fiiitd";

    public static void main(String[] args) {
        FacebookClient facebookClient = new DefaultFacebookClient(ACCESS_TOKEN);

        // Get our default facebook page for the system
        Page page = facebookClient.fetchObject(PAGE_ID, Page.class);
        println(page.getId() + ": " + page.getName());

        // Fetch all the messages from the page feed
        Connection<Post> postsFetch = facebookClient.fetchConnection(PAGE_ID + "/feed",
                Post.class,
                Parameter.with("fields", "commentsCount,from,message,comments.limit(10){from{id,name},message}")
        );

        for (List<Post> posts : postsFetch) {
            println("\nFeed:");
            for (Post post : posts) {
                println(post.getId() + ": " + post.getMessage() + " :: " + post.getFrom().getName());
                if(post.getComments() != null) {
                    Comments comments = post.getComments();
                    println("\tComments:");
                    for (Comment c: comments.getData()) {
                        println("\t\t"+ c.getId() + ": " + c.getMessage() + " :: " + c.getFrom().getName());
                    }
                }
            }
        }

    }

    public static void println(Object o) {
        System.out.println(o.toString());
    }
}
