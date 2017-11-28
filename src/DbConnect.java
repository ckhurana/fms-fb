import data.Student;

import java.sql.*;

public class DbConnect {
    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;


    public void readDb() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://localhost/fiiitd?user=root&password=Pass@123");
            statement = connection.createStatement();
            preparedStatement = connection.prepareStatement("INSERT INTO student (`fbId`, `firstName`, `lastName`, `phone`, `rollNo`) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setString(1, "baababa");
            preparedStatement.setString(2, "Chirag");
            preparedStatement.setString(3, "Khurana");
            preparedStatement.setString(4, "9711012240");
            preparedStatement.setString(5, "MT17010");

            int a = preparedStatement.executeUpdate();
//            resultSet = statement.executeQuery("DESCRIBE student");
            while (resultSet.next()) {
                Main.println(resultSet.getString(1));
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

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
            statement = connection.createStatement();
            preparedStatement = connection.prepareStatement("INSERT INTO student (`fbId`, `firstName`, `lastName`, `phone`, `rollNo`) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setString(1, student.getFbId());
            preparedStatement.setString(2, student.getFirstName());
            preparedStatement.setString(3, student.getLastName());
            preparedStatement.setString(4, student.getPhone());
            preparedStatement.setString(5, student.getRollNo());
            return preparedStatement.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                Main.println("Trying to insert Duplicate entry!!! Skipping !!!");
            }
        } finally {
            close();
        }
        return -1;
    }
}
