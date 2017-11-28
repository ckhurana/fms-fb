package data;

public class Student extends Person {
    private String rollNo;

    public Student(String fbId, String firstName, String lastName, String phone, String rollNo) {
        super(fbId, firstName, lastName, phone);
        this.rollNo = rollNo;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName() + " (" + getPhone() + ")";
    }
}