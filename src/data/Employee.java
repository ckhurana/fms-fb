package data;

import util.Utilities;

public class Employee extends Person {
    private Utilities.JobType profession;
    private boolean isBusy;
    private int taskCount; // Total no. of tasks completed

    public Employee(String fbId, String firstName, String lastName, String phone, Utilities.JobType profession) {
        super(fbId, firstName, lastName, phone);
        this.profession = profession;
        this.isBusy = false;
        this.taskCount = 0;
    }

    public Utilities.JobType getProfession() {
        return profession;
    }

    public void setProfession(Utilities.JobType profession) {
        this.profession = profession;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName() + " (" + getProfession().name() + ")";
    }
}
