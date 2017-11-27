package data;

public class Student extends Person {
    private String rollNo;

    public Student(int id, String fbId, String firstName, String lastName, int phone, String rollNo) {
        super(id, fbId, firstName, lastName, phone);
        this.rollNo = rollNo;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }
}
