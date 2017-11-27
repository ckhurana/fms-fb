package data;

public class Employee extends Person {
    enum prof {MAJDOOR, LUHAAR, JANITOR}
    private prof profession;
    public Employee(int id, String fbId, String firstName, String lastName, int phone, prof profession) {
        super(id, fbId, firstName, lastName, phone);
        this.profession = profession;
    }
}
