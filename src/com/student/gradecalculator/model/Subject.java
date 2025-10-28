package com.student.gradecalculator.model;

public class Subject {
    private String name;
    private int marks;
    private String gradeLetter;
    private boolean isHonours;

    public Subject(String name, int marks, boolean isHonours) {
        this.name = name;
        this.marks = marks;
        this.isHonours = isHonours;
        this.gradeLetter = "";
    }

    public String getName() 
    { 
        return name; 
    }
    public int getMarks() { return marks; }
    public boolean isHonours() { return isHonours; }
    public String getGradeLetter() { return gradeLetter; }

    public void setMarks(int marks) { this.marks = marks; }
    public void setGradeLetter(String gradeLetter) { this.gradeLetter = gradeLetter; }
}
