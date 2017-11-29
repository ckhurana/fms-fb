import data.Employee;
import data.Student;
import data.Task;
import util.Utilities;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DbConnect {
    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int addStudent(Student student) {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://localhost/fiiitd?user=root&password=Pass@123&useSSL=false");
            preparedStatement = connection.prepareStatement("INSERT INTO student (`fbId`, `firstName`, `lastName`, `phone`, `rollNo`) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setString(1, student.getFbId());
            preparedStatement.setString(2, student.getFirstName());
            preparedStatement.setString(3, student.getLastName());
            preparedStatement.setString(4, student.getPhone());
            preparedStatement.setString(5, student.getRollNo());
            return preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                Utilities.println("Trying to insert Duplicate entry!!! Skipping !!!");
            }
        } finally {
            close();
        }
        return -1;
    }

    public int addTask(Task task) {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://localhost/fiiitd?user=root&password=Pass@123&useSSL=false");
            preparedStatement = connection.prepareStatement("INSERT INTO task (`fbPostId`, `complaint`, `userId`, `loc`, `timestamp`, `type`, `isError`) VALUES (?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, task.getFbPostId());
            preparedStatement.setString(2, task.getComplaint());
            preparedStatement.setString(3, task.getUserId());
            preparedStatement.setString(4, task.getLoc());
            preparedStatement.setTimestamp(5, task.getTimestamp());
            preparedStatement.setInt(6, task.getType().ordinal());
            preparedStatement.setBoolean(7, task.isError());
            return preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                Utilities.println("Trying to insert Duplicate entry!!! Skipping !!!");
            } else {
                e.printStackTrace();
            }
        } finally {
            close();
        }
        return -1;
    }


    public boolean checkStudent(String uid) {
        int count = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://localhost/fiiitd?user=root&password=Pass@123&useSSL=false");
            preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM student WHERE `fbId` = ?");
            preparedStatement.setString(1, uid);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (ClassNotFoundException | SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                Utilities.println("Trying to insert Duplicate entry!!! Skipping !!!");
            }
        } finally {
            close();
        }
        if (count == 1)
            return true;
        else
            return false;
    }

    public int addEmployee(Employee emp) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/fiiitd?user=root&password=Pass@123&useSSL=false");
            preparedStatement = connection.prepareStatement("INSERT INTO employee (`fbId`, `firstName`, `lastName`, `phone`, `profession`) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setString(1, emp.getFbId());
            preparedStatement.setString(2, emp.getFirstName());
            preparedStatement.setString(3, emp.getLastName());
            preparedStatement.setString(4, emp.getPhone());
            preparedStatement.setInt(5, emp.getProfession().ordinal());
            return preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                Utilities.println("Trying to insert Duplicate entry!!! Skipping!!!");
            }
//            e.printStackTrace();
        } finally {
            close();
        }
        return -1;
    }

    public boolean hasTask(String postId) {
        int count = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://localhost/fiiitd?user=root&password=Pass@123&useSSL=false");
            preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM task WHERE `fbPostId` = ?");
            preparedStatement.setString(1, postId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return count == 1;
    }

    public boolean updateTask(Task task) {
        int count = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/fiiitd?user=root&password=Pass@123&useSSL=false");
            preparedStatement = connection.prepareStatement("UPDATE task SET `complaint` = ?, " +
                    "`loc` = ?, `timestamp` = ?, `type` = ?, `isError` = ?, `isProcessed` = ?, `isDone` = ?, `isLate` = ?, " +
                    "`employeeId` = ?, `feedback` = ? WHERE `fbPostId` = ?");
            preparedStatement.setString(1, task.getComplaint());
            preparedStatement.setString(2, task.getLoc());
            preparedStatement.setTimestamp(3, task.getTimestamp());
            preparedStatement.setInt(4, task.getType().ordinal());
            preparedStatement.setBoolean(5, task.isError());
            preparedStatement.setBoolean(6, task.isProcessed());
            preparedStatement.setBoolean(7, task.isDone());
            preparedStatement.setBoolean(8, task.isLate());
            preparedStatement.setString(9, task.getEmployeeId());
            preparedStatement.setInt(10, task.getFeedback());
            preparedStatement.setString(11, task.getFbPostId());
            count = preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return count == 1;
    }

    public boolean updateEmployee(Employee emp) {
        int count = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/fiiitd?user=root&password=Pass@123&useSSL=false");
            preparedStatement = connection.prepareStatement("UPDATE employee SET `isBusy` = ?, `taskCount` = ? " +
                    "WHERE `fbId` = ?");
            preparedStatement.setBoolean(1, emp.isBusy());
            preparedStatement.setInt(2, emp.getTaskCount());
            preparedStatement.setString(3, emp.getFbId());
            count = preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return count == 1;
    }

    public Task getTask(String postId) {
        Task t = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://localhost/fiiitd?user=root&password=Pass@123&useSSL=false");
            preparedStatement = connection.prepareStatement("SELECT * FROM task WHERE `fbPostId` = ?");
            preparedStatement.setString(1, postId);
            ResultSet rs = preparedStatement.executeQuery();
            ArrayList<Task> tasks = parseTasks(rs);
            if (tasks.size() > 0)
                t = tasks.get(0);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return t;
    }

    public ArrayList<Task> getNewTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://localhost/fiiitd?user=root&password=Pass@123&useSSL=false");
            preparedStatement = connection.prepareStatement("SELECT * FROM task WHERE `isProcessed` = 0 AND `isError` = 0");
            ResultSet rs = preparedStatement.executeQuery();
            tasks = parseTasks(rs);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return tasks;
    }

    public ArrayList<Employee> getAvailableEmployees() {
        ArrayList<Employee> employees = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://localhost/fiiitd?user=root&password=Pass@123&useSSL=false");
            preparedStatement = connection.prepareStatement("SELECT * FROM employee WHERE `isBusy` = 0");
            ResultSet rs = preparedStatement.executeQuery();
            employees = parseEmployees(rs);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return employees;
    }

    public ArrayList<Task> getProcessedTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://localhost/fiiitd?user=root&password=Pass@123&useSSL=false");
            preparedStatement = connection.prepareStatement("SELECT * FROM task WHERE `isProcessed` = 1 AND `isDone` = 0");
            ResultSet rs = preparedStatement.executeQuery();
            tasks = parseTasks(rs);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return tasks;
    }

    public HashMap<String,Employee> getEmployees() {
        HashMap<String, Employee> employees = new HashMap<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://localhost/fiiitd?user=root&password=Pass@123&useSSL=false");
            preparedStatement = connection.prepareStatement("SELECT * FROM employee;");
            ResultSet rs = preparedStatement.executeQuery();
            for (Employee e: parseEmployees(rs)) {
                employees.put(e.getFbId(), e);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return employees;
    }

    public ArrayList<Task> getFeedbackPendingTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://localhost/fiiitd?user=root&password=Pass@123&useSSL=false");
            preparedStatement = connection.prepareStatement("SELECT * FROM task WHERE `isDone` = 1 AND `feedback` = -1;");
            ResultSet rs = preparedStatement.executeQuery();
            tasks = parseTasks(rs);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return tasks;
    }


    private ArrayList<Task> parseTasks(ResultSet rs) throws SQLException {
        ArrayList<Task> tasks = new ArrayList<>();
        while (rs.next()) {
            Task t = new Task(rs.getString("fbPostId"),
                    rs.getString("complaint"),
                    rs.getString("userId"),
                    rs.getString("loc"),
                    Utilities.JobType.values()[rs.getInt("type")],
                    rs.getTimestamp("timestamp"));
            t.setEmployeeId(rs.getString("employeeId"));
            t.setDone(rs.getBoolean("isDone"));
            t.setError(rs.getBoolean("isError"));
            t.setProcessed(rs.getBoolean("isProcessed"));
            t.setId(rs.getInt("id"));

            tasks.add(t);
        }
        return tasks;
    }

    private ArrayList<Employee> parseEmployees(ResultSet rs) throws SQLException {
        ArrayList<Employee> employees = new ArrayList<>();
        while (rs.next()) {
            Employee e = new Employee(rs.getString("fbId"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("phone"),
                    Utilities.JobType.values()[rs.getInt("profession")]);
            e.setBusy(rs.getBoolean("isBusy"));
            e.setTaskCount(rs.getInt("taskCount"));
            employees.add(e);
        }
        return employees;
    }

    public HashMap<String, Integer> getTotalEmployeeTasks() {
        HashMap<String, Integer> res = new HashMap<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://localhost/fiiitd?user=root&password=Pass@123&useSSL=false");
            preparedStatement = connection.prepareStatement("SELECT `employeeId`, COUNT(*) FROM task GROUP BY `employeeId`");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                res.put(rs.getString("employeeId"), rs.getInt(2));
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return res;
    }

}
