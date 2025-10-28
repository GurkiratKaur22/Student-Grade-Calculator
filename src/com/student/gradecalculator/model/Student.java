package com.student.gradecalculator.model;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String name;
    private String rollNo;
    private String branch;
    private List<Semester> semesters;

    public Student(String name, String rollNo, String branch) {
        this.name = name;
        this.rollNo = rollNo;
        this.branch = branch;
        this.semesters = new ArrayList<>();
    }

    public String getName() { return name; }
    public String getRollNo() { return rollNo; }
    public String getBranch() { return branch; }
    public List<Semester> getSemesters() { return semesters; }
    public void addSemester(Semester s) { semesters.add(s); }
}
