package com.student.gradecalculator.model;

import java.util.List;

public class GradeCalculator {

    // Convert marks to letter grade
    public static String marksToLetterGrade(int marks) {
        if (marks >= 90) return "O";
        else if (marks >= 80) return "A";
        else if (marks >= 70) return "B";
        else if (marks >= 60) return "C";
        else if (marks >= 50) return "D";
        else return "F";
    }

    // Convert marks to grade point for CGPA calculation
    // A -> 10, B -> 9, C -> 8, D -> 7, F -> 0
    public static int marksToGradePoint(int marks) {
        if (marks >= 90) return 10;
        else if (marks >= 80) return 9;
        else if (marks >= 70) return 8;
        else if (marks >= 60) return 7;
        else if (marks >= 50) return 6;
        else return 0;
    }

    // Calculate CGPA for a semester (average grade points across subjects)
    public static double calculateCGPA(Semester sem) {
        List<Subject> subjects = sem.getSubjects();
        if (subjects.isEmpty()) return 0.0;
        int totalPoints = 0;
        for (Subject s : subjects) {
            totalPoints += marksToGradePoint(s.getMarks());
        }
        return (double) totalPoints / subjects.size();
    }
}
