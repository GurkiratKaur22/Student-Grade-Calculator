package com.student.gradecalculator.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import com.student.gradecalculator.model.*;
import com.student.gradecalculator.util.FileManager;

public class GradeReportFrame extends JFrame {

    // UI components
    private JTextField nameField, rollField;
    private JComboBox<String> branchBox, semBox, honoursChooser;
    private JCheckBox honoursCheck;
    private DefaultTableModel tableModel;
    private JLabel cgpaLabel;

    // stored CGPAs for year calculation (0 means not yet calculated)
    private double sem3CGPA = 0.0;
    private double sem4CGPA = 0.0;
    // store info about the student for each semester
    private String sem3Name = "", sem3Roll = "", sem3Branch = "";
    private String sem4Name = "", sem4Roll = "", sem4Branch = "";


    // honours options
    private static final String[] HONOURS_OPTIONS = {
        "Quantum Computing & Generative AI",
        "Cybersecurity & Forensics",
        "Software Management & Mobile Applications"
    };

    public GradeReportFrame() {
        setTitle("Student Grade Calculator - Grade Report");
        setSize(920, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));

        // Top info panel
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0;
        topPanel.add(new JLabel("Name of Student:"), c);
        c.gridx = 1; c.gridy = 0; c.weightx = 1.0;
        nameField = new JTextField();
        topPanel.add(nameField, c);

        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        topPanel.add(new JLabel("Roll No:"), c);
        c.gridx = 1; c.gridy = 1; c.weightx = 1.0;
        rollField = new JTextField();
        topPanel.add(rollField, c);

        c.gridx = 0; c.gridy = 2; c.weightx = 0;
        topPanel.add(new JLabel("Branch:"), c);
        c.gridx = 1; c.gridy = 2; c.weightx = 1.0;
        branchBox = new JComboBox<>(new String[] {"Select", "CSBS", "VLSI", "EXCP"});
        topPanel.add(branchBox, c);

        c.gridx = 0; c.gridy = 3; c.weightx = 0;
        topPanel.add(new JLabel("Semester:"), c);
        c.gridx = 1; c.gridy = 3; c.weightx = 1.0;
        semBox = new JComboBox<>(new String[] {"Select", "3", "4"});
        topPanel.add(semBox, c);

        c.gridx = 0; c.gridy = 4; c.weightx = 0;
        honoursCheck = new JCheckBox("Include Honours subject?");
        topPanel.add(honoursCheck, c);

        c.gridx = 1; c.gridy = 4; c.weightx = 1.0;
        honoursChooser = new JComboBox<>(HONOURS_OPTIONS);
        honoursChooser.setEnabled(false);
        topPanel.add(honoursChooser, c);

        // toggle honours chooser when check toggled
        honoursCheck.addActionListener(e -> honoursChooser.setEnabled(honoursCheck.isSelected()));

        add(topPanel, BorderLayout.NORTH);

        // Table center
        String[] cols = {"Subjects (with name)", "Marks (out of 100)", "Grade"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) {
                // allow editing marks column only (index 1)
                return column == 1;
            }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(30);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // Right legend
        JPanel legend = new JPanel();
        legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));
        legend.setBorder(BorderFactory.createTitledBorder("Grade Scale"));
        legend.add(new JLabel("O → above 90"));
        legend.add(new JLabel("A → above 80"));
        legend.add(new JLabel("B → above 70"));
        legend.add(new JLabel("C → above 60"));
        legend.add(new JLabel("D → above 50"));
        legend.add(new JLabel("F → below 50 (Fail)"));
        add(legend, BorderLayout.EAST);

        // Bottom controls
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton loadBtn = new JButton("Load Subjects");
        JButton calcBtn = new JButton("Calculate Grades");
        JButton saveBtn = new JButton("Save Report");
        JButton loadFileBtn = new JButton("Load Report");
        JButton yearBtn = new JButton("Calculate Year CGPA");
        cgpaLabel = new JLabel("Semester CGPA: -");
        bottom.add(loadBtn);
        bottom.add(calcBtn);
        bottom.add(saveBtn);
        bottom.add(loadFileBtn);
        bottom.add(yearBtn);
        bottom.add(cgpaLabel);
        add(bottom, BorderLayout.SOUTH);

        // events
        loadBtn.addActionListener(e -> loadSubjects());
        calcBtn.addActionListener(e -> calculateGradesAndShow());
        saveBtn.addActionListener(e -> saveReport());
        loadFileBtn.addActionListener(e -> loadReportFromFile());
        yearBtn.addActionListener(e -> calculateYearCGPA());

        // UX: double-click on table marks cell: nothing special, editing is allowed.
    }

    // The subject lists for each branch+semester
    private static final Map<String, String[]> SUBJECT_MAP = createSubjectMap();
    private static Map<String, String[]> createSubjectMap() {
        Map<String, String[]> m = new HashMap<>();
        // CSBS sem3
        m.put("CSBS_3", new String[]{
            "Computational Statistics, Probability and Calculus",
            "Data Structures & Algorithms",
            "Computer Organization and Architecture",
            "Discrete Mathematics",
            "Fundamentals of Economics",
            "Object Oriented Programming"
        });
        // CSBS sem4
        m.put("CSBS_4", new String[]{
            "Linear Algebra and Optimization",
            "Formal Language & Automata Theory",
            "Data Science",
            "Design & Analysis of Algorithms",
            "Business Communication & Value Science",
            "Database Management Systems"
        });

        // VLSI sem3
        m.put("VLSI_3", new String[]{
            "Mathematics for Electronic Engineering-I",
            "Electronic Devices and Circuits",
            "Digital System Design",
            "Signals and Systems",
            "Electrical Networks",
            "Object Oriented Programming Laboratory"
        });
        // VLSI sem4
        m.put("VLSI_4", new String[]{
            "Mathematics for Electronic Engineering-II",
            "Analog Electronics Circuits",
            "System Design using FPGA",
            "Analog and Digital Communication",
            "Micro-controllers and Computer Architecture",
            "Open Elective (Generic)"
        });

        // EXCP sem3
        m.put("EXCP_3", new String[]{
            "Vectors and Transforms",
            "Analog Electronic Circuits",
            "Digital Electronics",
            "Data Structures",
            "Networks, Signals & Systems",
            "Object Oriented Programming Laboratory"
        });
        // EXCP sem4
        m.put("EXCP_4", new String[]{
            "Probability, Statistics and Optimization Techniques",
            "Analog and Digital Communication",
            "Analysis of Algorithms",
            "Database Management Systems",
            "Discrete Mathematics",
            "Microprocessors Lab Course"
        });

        return m;
    }

    // Load subjects based on branch + semester; honours adds chosen honours subject
    private void loadSubjects() {
        tableModel.setRowCount(0);
        String branch = (String) branchBox.getSelectedItem();
        String semSel = (String) semBox.getSelectedItem();

        if (branch == null || branch.equals("Select") || semSel == null || semSel.equals("Select")) {
            JOptionPane.showMessageDialog(this, "Please select both Branch and Semester (3 or 4).");
            return;
        }
        String key = branch + "_" + semSel;
        String[] subjects = SUBJECT_MAP.get(key);
        if (subjects == null) {
            JOptionPane.showMessageDialog(this, "Subjects not configured for " + key);
            return;
        }
        for (String s : subjects) {
            tableModel.addRow(new Object[] { s, "", "" });
        }

        if (honoursCheck.isSelected()) {
            String hon = (String) honoursChooser.getSelectedItem();
            if (hon == null) hon = HONOURS_OPTIONS[0];
            tableModel.addRow(new Object[] { hon + " (Honours, Optional)", "", "" });
        }
    }

    // Validate marks and compute grades + semester CGPA
    // Validate marks and compute grades + semester CGPA
private void calculateGradesAndShow() {
    String name = nameField.getText().trim();
    String roll = rollField.getText().trim();
    String branch = (String) branchBox.getSelectedItem();
    String semSel = (String) semBox.getSelectedItem();

    if (name.isEmpty() || roll.isEmpty() || branch == null || branch.equals("Select")
            || semSel == null || semSel.equals("Select")) {
        JOptionPane.showMessageDialog(this, "Please enter name, roll, branch and semester.");
        return;
    }

    Semester sem = new Semester(Integer.parseInt(semSel));
    boolean anyMarks = false;
    boolean hasFail = false;  //added flag

    for (int i = 0; i < tableModel.getRowCount(); i++) {
        String subjectName = Objects.toString(tableModel.getValueAt(i, 0), "");
        String marksStr = Objects.toString(tableModel.getValueAt(i, 1), "").trim();
        if (marksStr.isEmpty()) continue;

        anyMarks = true;
        int marks;
        try {
            marks = Integer.parseInt(marksStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number at row " + (i + 1));
            return;
        }

        if (marks < 0 || marks > 100) {
            JOptionPane.showMessageDialog(this, "Marks must be 0–100 at row " + (i + 1));
            return;
        }

        // check if student failed this subject
        if (marks < 50) {
            hasFail = true;
        }

        String letter = GradeCalculator.marksToLetterGrade(marks);
        tableModel.setValueAt(letter, i, 2);

        Subject s = new Subject(subjectName, marks, subjectName.toLowerCase().contains("honours"));
        s.setGradeLetter(letter);
        sem.addSubject(s);
    }

    if (!anyMarks) {
        JOptionPane.showMessageDialog(this, "No marks entered. Please enter marks for subjects.");
        return;
    }

    // If failed in any subject, show "FAIL" instead of CGPA
    if (hasFail) {
    cgpaLabel.setText("Semester CGPA: FAIL");
    JOptionPane.showMessageDialog(this,
        "The student has failed in one or more subjects.\nSemester Result: FAIL",
        "Semester Result", JOptionPane.ERROR_MESSAGE);

    // Save FAIL result along with student details
    int semNo = sem.getSemNumber();
    if (semNo == 3) {
        sem3CGPA = -1;
        sem3Name = name;
        sem3Roll = roll;
        sem3Branch = branch;
    } else if (semNo == 4) {
        sem4CGPA = -1;
        sem4Name = name;
        sem4Roll = roll;
        sem4Branch = branch;
    }
    return;
}


    //  else calculate CGPA normally
    double cgpa = GradeCalculator.calculateCGPA(sem);
    cgpaLabel.setText("Semester CGPA: " + String.format("%.2f", cgpa));

    int semNo = sem.getSemNumber();
    if (semNo == 3) {
        sem3CGPA = cgpa;
        sem3Name = name;
        sem3Roll = roll;
        sem3Branch = branch;
    } else if (semNo == 4) {
        sem4CGPA = cgpa;
        sem4Name = name;
        sem4Roll = roll;
        sem4Branch = branch;
    }

    // Summary display
    StringBuilder sb = new StringBuilder();
    sb.append("Student: ").append(name).append("\n");
    sb.append("Roll No: ").append(roll).append("\n");
    sb.append("Branch: ").append(branch).append("\n");
    sb.append("Semester: ").append(semNo).append("\n");
    sb.append("Semester CGPA: ").append(String.format("%.2f", cgpa)).append("\n\n");
    sb.append("Grades:\n");
    for (Subject s : sem.getSubjects()) {
        sb.append(s.getName()).append(" -> ").append(s.getGradeLetter()).append("\n");
    }
    JOptionPane.showMessageDialog(this, sb.toString(), "Semester Result", JOptionPane.INFORMATION_MESSAGE);
}


    // Save report to file
    private void saveReport() {
        String name = nameField.getText().trim();
        String roll = rollField.getText().trim();
        String branch = (String) branchBox.getSelectedItem();
        String semSel = (String) semBox.getSelectedItem();

        if (name.isEmpty() || roll.isEmpty() || branch == null || branch.equals("Select")
                || semSel == null || semSel.equals("Select")) {
            JOptionPane.showMessageDialog(this, "Fill name, roll, branch and semester before saving.");
            return;
        }

        // Build student object
        Student student = new Student(name, roll, branch);
        Semester sem = new Semester(Integer.parseInt(semSel));
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String sname = Objects.toString(tableModel.getValueAt(i, 0), "");
            String marksStr = Objects.toString(tableModel.getValueAt(i, 1), "").trim();
            String grade = Objects.toString(tableModel.getValueAt(i, 2), "").trim();
            if (marksStr.isEmpty()) continue;
            int marks = Integer.parseInt(marksStr);
            Subject s = new Subject(sname, marks, sname.toLowerCase().contains("honours"));
            s.setGradeLetter(grade);
            sem.addSubject(s);
        }
        student.addSemester(sem);

        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try {
                FileManager.saveStudent(student, f);
                JOptionPane.showMessageDialog(this, "Report saved to: " + f.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
            }
        }
    }

    // Load report from file and populate UI
    private void loadReportFromFile() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try {
                Student s = FileManager.loadStudent(f);
                if (s == null) {
                    JOptionPane.showMessageDialog(this, "File empty or invalid");
                    return;
                }
                nameField.setText(s.getName());
                rollField.setText(s.getRollNo());
                branchBox.setSelectedItem(s.getBranch());
                tableModel.setRowCount(0);
                if (!s.getSemesters().isEmpty()) {
                    Semester sem = s.getSemesters().get(0);
                    semBox.setSelectedItem(String.valueOf(sem.getSemNumber()));
                    for (Subject sub : sem.getSubjects()) {
                        tableModel.addRow(new Object[] { sub.getName(), String.valueOf(sub.getMarks()), sub.getGradeLetter() });
                    }
                    double cgpa = GradeCalculator.calculateCGPA(sem);
                    cgpaLabel.setText("Semester CGPA: " + String.format("%.2f", cgpa));
                    if (sem.getSemNumber() == 3) sem3CGPA = cgpa;
                    if (sem.getSemNumber() == 4) sem4CGPA = cgpa;
                }
                JOptionPane.showMessageDialog(this, "Report loaded.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage());
            }
        }
    }

    // Calculate year CGPA from stored sem3 and sem4 CGPAs
    private void calculateYearCGPA() {
    if (sem3CGPA == 0.0 || sem4CGPA == 0.0) {
        JOptionPane.showMessageDialog(this,
            "Please calculate and save both Semester 3 and Semester 4 CGPAs first.");
        return;
    }

    // Check if both semesters belong to the same student
    if (!sem3Name.equalsIgnoreCase(sem4Name) ||
        !sem3Roll.equalsIgnoreCase(sem4Roll) ||
        !sem3Branch.equalsIgnoreCase(sem4Branch)) {

        JOptionPane.showMessageDialog(this,
            "Semester 3 and Semester 4 belong to different students.\n" +
            "Please ensure both semesters are calculated for the same student before computing the Year CGPA.",
            "Mismatch Detected", JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (sem3CGPA == -1 || sem4CGPA == -1) {
    JOptionPane.showMessageDialog(this,
        "Student has failed in one or both semesters.\nOverall Year Result: FAIL",
        "Year CGPA", JOptionPane.ERROR_MESSAGE);
    return;
}

double yearCGPA = (sem3CGPA + sem4CGPA) / 2.0;
String msg = String.format(
    "Name: %s\nRoll No: %s\nBranch: %s\n\nSemester 3 CGPA: %.2f\nSemester 4 CGPA: %.2f\n\nOverall Year CGPA: %.2f",
    sem3Name, sem3Roll, sem3Branch, sem3CGPA, sem4CGPA, yearCGPA);


    JOptionPane.showMessageDialog(this, msg, "Year CGPA", JOptionPane.INFORMATION_MESSAGE);
}

}
