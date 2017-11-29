import com.restfb.*;
import com.restfb.types.*;
import data.Employee;
import data.Student;
import data.Task;
import util.Utilities;

import java.io.FileNotFoundException;
import java.lang.Thread;
import java.sql.Timestamp;
import java.util.*;

public class Main {
    private static final String ACCESS_TOKEN = "EAACEdEose0cBAIsC5P2CschmmM3V36U9obMtGJd2EiBz19fY3uLSZC62CwZBgknEmoBvCOOyFhJrv5xeDXs6MsQvAMfHZBYlRk1EnzZBZAFgSOIJ9E9fyGzA0MSCsGkIoKKTNktnhDqepbw5ZAvMSJUmXp0g3ZCUK2B5ZC1XngFmZC5CSm52S8MQdCc9IwuIBVQn2KZAddKfnV0wZDZD";
    private static final String PAGE_ID = "fiiitd";
    private static final long TIME_MARGIN = 60 * 24;
    private static final int REPORT_THRESHOLD = 10;
    private static final long REFRESH_RATE = 1000 * 120;

    private static int counter = 0;

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        FacebookClient facebookClient = new DefaultFacebookClient(Utilities.getAccessToken(), Version.LATEST);
        DbConnect dbConnect = new DbConnect();

        // Get our default facebook page for the system
        Page page = facebookClient.fetchObject(PAGE_ID, Page.class);
        Utilities.println(page.getId() + ": " + page.getName());

        while (true) {
            try {
                counter++;

                if (counter % 15 == 0) {
                    facebookClient = new DefaultFacebookClient(Utilities.getAccessToken(), Version.LATEST);
                    dbConnect = new DbConnect();
                }

                // Fetch all the messages from the page feed
                Connection<Post> postsFetch = facebookClient.fetchConnection(PAGE_ID + "/feed",
                        Post.class,
                        Parameter.with("fields", "id, message, from{id, name}, updated_time, created_time, comments.limit(10){id, from{id, name}, message}")
                );

                Utilities.println("\n---- Checking New Posts ----");

                for (List<Post> posts : postsFetch) {
                    Utilities.println("\nFeed:");
                    for (Post post : posts) {
                        String id = post.getId();
                        CategorizedFacebookType user = post.getFrom();
                        String msg = post.getMessage();
                        Utilities.println("\n" + id + ": " + msg + " :: " + user.getName() + " :: " + post.getCreatedTime());

                        // POST a Comment
                        // facebookClient.publish(post.getId() + "/comments", Comment.class, Parameter.with("message", "test"));

                        String[] command = msg.toUpperCase().split(" ");
                        int res;
                        switch (command[0]) {
                            case "ADDS":
                                Student student = new Student(user.getId(), user.getName().split(" ")[0], user.getName().split(" ")[1], command[1], command[2]);
                                res = dbConnect.addStudent(student);
                                if (res > 0) {
                                    Utilities.println("Successfully added student: " + student);
                                }
                                break;
                            case "ADDE":
                                Employee emp = new Employee(user.getId(), user.getName().split(" ")[0], user.getName().split(" ")[1], command[1], Utilities.JobType.valueOf(command[2]));
                                res = dbConnect.addEmployee(emp);
                                if (res > 0) {
                                    Utilities.println("Successfully added employee: " + emp);
                                }
                                break;
                            case "REQ":
                                Task task;
                                if (dbConnect.checkStudent(user.getId())) {
                                    if (command.length < 3) {
                                        task = new Task(post.getId(), String.join(" ",
                                                Arrays.asList(command).subList(1, command.length)),
                                                user.getId(), "", Utilities.JobType.UNKNOWN, new Timestamp(post.getCreatedTime().getTime()));
                                        task.setError(true);
                                        if (!dbConnect.hasTask(post.getId()))
                                            Utilities.addComment(facebookClient, post, "Incorrect command format, please fix the command.");
                                        else
                                            task = null;
                                    } else {
                                        String loc = command[1];
                                        Utilities.JobType type = Utilities.getJobType(command[2]);
                                        String complaint = String.join(" ", Arrays.asList(command).subList(3, command.length));
                                        Timestamp timestamp = new Timestamp(post.getCreatedTime().getTime());
                                        task = new Task(post.getId(), complaint, user.getId(), loc, type, timestamp);
                                        if (dbConnect.hasTask(post.getId())) {
                                            Task task2 = dbConnect.getTask(post.getId());
                                            if(task2.isError()) {
                                                task.setTimestamp(new Timestamp(post.getUpdatedTime().getTime()));
                                                if(dbConnect.updateTask(task)) {
                                                    Utilities.println("Request from " + user.getName() + " successfully registered.");
                                                }
                                            }
                                            task = null;
                                        }
                                    }
                                    if (task != null) {
                                        if (task.getType().equals(Utilities.JobType.UNKNOWN))
                                            task.setError(true);
                                        if (dbConnect.addTask(task) > 0) {
                                            if (!task.isError())
                                                Utilities.println("Successfully added Task for processing!!!");
                                        }
                                    }
                                }
                                break;
                            default:
                                Utilities.println("Unidentified command detected!!!");

                        }

                        if(post.getComments() != null) {
                            Comments comments = post.getComments();
                            Utilities.println("\tComments:");
                            for (Comment c: comments.getData()) {
                                Utilities.println("\t\t"+ c.getId() + ": " + c.getMessage() + " :: " + c.getFrom().getName());
                            }
                        }
                    }
                }


                Utilities.println("\n---- Checking Completion Status ----");

                // TODO: To check the completed tasks
                ArrayList<Task> processedTasks = dbConnect.getProcessedTasks();
                HashMap<String, Employee> employees = dbConnect.getEmployees();

                for (Task task : processedTasks) {
                    if (task.getFeedback() == -2) {
                        Comments comments = facebookClient.fetchObject(task.getFbPostId() + "/comments", Comments.class,
                                Parameter.with("fields", "message, from{id}, created_time"));
                        for (Comment comment : comments.getData()) {
                            String mesg = comment.getMessage().toUpperCase().trim();
                            String uid = comment.getFrom().getId();
                            Employee e = employees.get(task.getEmployeeId());
                            if (mesg.equals("DONE")
                                    && (uid.equals(e.getFbId()) || uid.equals(page.getId()))) {
                                Utilities.println("Task " + task.getId() + " completed successfully.");
                                if(Utilities.isValidTimeDiff(task.getTimestamp(), new Timestamp(comment.getCreatedTime().getTime()), TIME_MARGIN, "sec"))
                                    //TODO: check time difference within limit
                                    e.setTaskCount(e.getTaskCount() + 1);
                                else
                                    task.setLate(true);
                                e.setBusy(false);
                                task.setDone(true);
                                task.setFeedback(-1);

                                dbConnect.updateEmployee(e);
                                dbConnect.updateTask(task);
                                String msg = "FEEDBACK: Thank You for using our system.\nPlease rate the services you received from this model on a scale (0 - 5) based on satisfaction.\n" +
                                        "NOTE: Just mention a number between 0 - 5, nothing else.";
                                Utilities.addComment(facebookClient, task.getFbPostId(), msg);
                                break;
                            }
                        }
                    }
                }

                Utilities.println("\n---- Checking Feedback Status ----");

                // TODO: to check for negative feedback
                ArrayList<Task> feedbackPendingTasks = dbConnect.getFeedbackPendingTasks();

                for (Task task : feedbackPendingTasks) {
                    boolean flag = false;
                    Comments comments = facebookClient.fetchObject(task.getFbPostId() + "/comments", Comments.class);
                    for (Comment comment : comments.getData()) {
                        if (comment.getMessage().trim().toUpperCase().startsWith("FEEDBACK:"))
                            flag = true;
                        if (comment.getFrom().getId().equals(task.getUserId()) && comment.getMessage().trim().length() > 0 && comment.getMessage().trim().length() < 2 && flag) {
                            try {
                                int feedback = Integer.parseInt(comment.getMessage().trim());
                                if (feedback >= 0 && feedback <= 5) {
                                    task.setFeedback(feedback);
                                    String msg;
                                    if (feedback < 3) {
                                        Employee e = employees.get(task.getEmployeeId());
                                        e.setTaskCount(e.getTaskCount() - 1);
                                        dbConnect.updateEmployee(e);
                                        msg = "Thanks for the feedback. We'll try to improve our services.";
                                    } else
                                        msg = "Thanks for the positive feedback. We love to serve our users.";
                                    dbConnect.updateTask(task);
                                    Utilities.addComment(facebookClient, task.getFbPostId(), msg);
                                    break;
                                } else
                                    throw new Exception();
                            } catch (Exception e) {
                                Utilities.addComment(facebookClient, task.getFbPostId(), "Incorrect feedback format, please enter an integer between 0 - 5.");
                            }
                        }
                    }
                }


                Utilities.println("\n---- Checking Employee Status ----");
                // TODO: check employee integrity



                Utilities.println("\n---- Assigning Tasks ----");
                // Tasks getting assigned
                ArrayList<Task> newTasks = dbConnect.getNewTasks();
                ArrayList<Employee> availableEmployees = dbConnect.getAvailableEmployees();
                newTasks.sort(Comparator.comparing(Task::getTimestamp));
                availableEmployees.sort(Comparator.comparing(Employee::getTaskCount));
                for (Task task : newTasks) {
                    for (Employee e : availableEmployees) {
                        if (e.getProfession().equals(task.getType())) {
                            assignTask(dbConnect, facebookClient, task, e);
                            employees.remove(e);
                            break;
                        }
                    }
                }

                try {
                    Thread.sleep(REFRESH_RATE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Thread.sleep(REFRESH_RATE);
                continue;
            }

        }
    }

    public static void assignTask(DbConnect dbConnect, FacebookClient facebookClient, Task task, Employee employee) {
        if (!task.isProcessed() && !employee.isBusy() && !task.isError()) {
            employee.setBusy(true);
            task.setProcessed(true);
            task.setEmployeeId(employee.getFbId());
            if (dbConnect.updateTask(task)) {
                if(dbConnect.updateEmployee(employee)) {
                    String msg = "NOTIFICATION: Task successfully assigned to " + employee.getFirstName() + " " + employee.getLastName() + " (Mob. No.: " + employee.getPhone() + ")";
                    Utilities.println("Successfully assigned task for task " + task.getId() + " to " + employee.getFirstName());
                    Utilities.addComment(facebookClient, task.getFbPostId(), msg, employee.getFbId());
                } else {
                    task.setProcessed(false);
                    task.setEmployeeId("");
                    dbConnect.updateTask(task);
                }
            }
        }
    }
}
