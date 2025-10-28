package com.student.gradecalculator.util;

import com.student.gradecalculator.model.*;
import java.io.*;
import java.util.*;

public class FileManager {

    // Save student report in a simple CSV-like text format
    public static void saveStudent(Student student, File file) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            // header: name,roll,branch
            bw.write(student.getName() + "," + student.getRollNo() + "," + student.getBranch());
            bw.newLine();
            for (Semester sem : student.getSemesters()) {
                bw.write("Semester," + sem.getSemNumber());
                bw.newLine();
                for (Subject sub : sem.getSubjects()) {
                    bw.write(sub.getName().replace(",", " ") + "," + sub.getMarks() + "," + sub.getGradeLetter());
                    bw.newLine();
                }
            }
        }
    }

    // Load a student file saved by saveStudent
    public static Student loadStudent(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String header = br.readLine();
            if (header == null) return null;
            String[] headParts = header.split(",", 3);
            String name = headParts.length > 0 ? headParts[0] : "";
            String roll = headParts.length > 1 ? headParts[1] : "";
            String branch = headParts.length > 2 ? headParts[2] : "";

            Student student = new Student(name, roll, branch);
            String line;
            Semester currentSem = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Semester,")) {
                    String[] sec = line.split(",", 2);
                    int semNo = Integer.parseInt(sec[1].trim());
                    currentSem = new Semester(semNo);
                    student.addSemester(currentSem);
                } else {
                    String[] parts = line.split(",", 3);
                    if (parts.length >= 2 && currentSem != null) {
                        String sname = parts[0];
                        int marks = Integer.parseInt(parts[1].trim());
                        String grade = parts.length >= 3 ? parts[2] : "";
                        Subject s = new Subject(sname, marks, sname.toLowerCase().contains("honours"));
                        s.setGradeLetter(grade);
                        currentSem.addSubject(s);
                    }
                }
            }
            return student;
        }
    }
}
