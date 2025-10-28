package com.student.gradecalculator.model;

import java.util.ArrayList;
import java.util.List;

public class Semester {
    private int semNumber;
    private List<Subject> subjects;

    public Semester(int semNumber) {
        this.semNumber = semNumber;
        this.subjects = new ArrayList<>();
    }

    public int getSemNumber() { return semNumber; }
    public List<Subject> getSubjects() { return subjects; }
    public void addSubject(Subject s) { subjects.add(s); }

    void setCgpa(double d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
