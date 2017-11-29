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
//    private static final long TIME_MARGIN = 60 * 24;
    private static final long TIME_MARGIN = 2;
//    private static final int REPORT_THRESHOLD = 10;
    private static final int REPORT_THRESHOLD = 1;
    private static final long REFRESH_RATE = 1000 * 20;

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

                        String[] command = msg.toUpperCase().split(" ");
                        int res;
                        switch (command[0]) {
                            case "ADDS":
                                Student student = new Student(user.getId(), user.getName().split(" ")[0], user.getName().split(" ")[1], command[1], command[2]);
                                res = dbConnect.addStudent(student);
                                if (res > 0) {
                                    Utilities.println("Successfully added student: " + student);
                                    Utilities.addComment(facebookClient, post.getId(), "Successfully registered as student.");
                                }
                                break;
                            case "ADDE":
                                Employee emp = new Employee(user.getId(), user.getName().split(" ")[0], user.getName().split(" ")[1], command[1], Utilities.JobType.valueOf(command[2]));
                                res = dbConnect.addEmployee(emp);
                                if (res > 0) {
                                    Utilities.println("Successfully added employee: " + emp);
                                    Utilities.addComment(facebookClient, post.getId(), "Successfully registered as employee.");
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
                                            Utilities.addComment(facebookClient, post.getId(), "Incorrect command format, please fix the command.");
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
                                Utilities.addComment(facebookClient, post.getId(), "Unidentified command detected!!!");
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
                                    && (uid.equals(e.getFbId()) || uid.equals(page.getId()) || uid.equals(task.getUserId()))) {
                                Utilities.println("Task " + task.getId() + " completed successfully.");
                                Utilities.println("LOG: " + task.getTimestamp() + " : " + new Timestamp(comment.getCreatedTime().getTime()));
                                Utilities.println("LOG: " + Utilities.getTimeDiff(task.getTimestamp(), new Timestamp(comment.getCreatedTime().getTime()), "SEC"));
                                if(Utilities.isValidTimeDiff(task.getTimestamp(), new Timestamp(comment.getCreatedTime().getTime()), TIME_MARGIN, "sec")) {
                                    e.setTaskCount(e.getTaskCount() + 1);
                                    Utilities.println("LOG: " + Utilities.getTimeDiff(task.getTimestamp(), new Timestamp(comment.getCreatedTime().getTime()), "SEC"));
                                } else
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

                ArrayList<Task> feedbackPendingTasks = dbConnect.getFeedbackPendingTasks();

                for (Task task : feedbackPendingTasks) {
                    boolean flag = false;
                    Comments comments = facebookClient.fetchObject(task.getFbPostId() + "/comments", Comments.class);
                    for (Comment comment : comments.getData()) {
                        if (comment.getMessage().trim().toUpperCase().startsWith("FEEDBACK:"))
                            flag = true;
                        else {
                            try {
                                if (comment.getFrom().getId().equals(task.getUserId())
                                        && comment.getMessage().trim().length() > 0
                                        && comment.getMessage().trim().length() < 2
                                        && comments.getData().indexOf(comment) == comments.getData().size() - 1
                                        && flag) {
                                    int feedback = Integer.parseInt(comment.getMessage().trim());
                                    if (feedback >= 0 && feedback <= 5) {
                                        task.setFeedback(feedback);
                                        String msg;
                                        if (feedback < 3) {
                                            Employee e = employees.get(task.getEmployeeId());
                                            if (!task.isLate())
                                                e.setTaskCount(e.getTaskCount() - 1);
                                            dbConnect.updateEmployee(e);
                                            msg = "Thanks for the feedback. We'll try to improve our services.";
                                        } else
                                            msg = "Thanks for the positive feedback. We love to serve our users.";
                                        dbConnect.updateTask(task);
                                        Utilities.addComment(facebookClient, task.getFbPostId(), msg);
                                        break;
                                    } else {
                                        throw new Exception();
                                    }
                                } else if (comment.getFrom().getId().equals(task.getUserId())) {
                                    throw new Exception();
                                }
                            } catch (Exception e) {
                                if(comments.getData().get(comments.getData().size() - 1).equals(comment)) {
                                    Utilities.println("Invalid Input!!!");
                                    Utilities.addComment(facebookClient, task.getFbPostId(), "Incorrect feedback format, please enter an integer between 0 - 5.");
                                }

                            }
                        }
                    }
                }


                Utilities.println("\n---- Checking Employee Status ----");
                // TODO: check employee integrity
                HashMap<String, Integer> tasksAssigned = dbConnect.getTotalEmployeeTasks();
                ArrayList<Employee> defaulterEmployees = Utilities.getDefaulters(employees, tasksAssigned, REPORT_THRESHOLD);
                Utilities.reportEmployees(defaulterEmployees);




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
                    Utilities.addComment(facebookClient, task.getFbPostId(), msg);
                } else {
                    task.setProcessed(false);
                    task.setEmployeeId("");
                    dbConnect.updateTask(task);
                }
            }
        }
    }
}
