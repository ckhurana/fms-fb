package data;

public class Employee extends Person {
    enum prof {MAJDOOR, LUHAAR, JANITOR}
    private prof profession;
    public Employee(String fbId, String firstName, String lastName, String phone, prof profession) {
        super(fbId, firstName, lastName, phone);
        this.profession = profession;
    }
}
