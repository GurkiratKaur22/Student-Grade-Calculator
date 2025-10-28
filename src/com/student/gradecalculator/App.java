package com.student.gradecalculator;

import javax.swing.SwingUtilities;
import com.student.gradecalculator.ui.GradeReportFrame;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GradeReportFrame f = new GradeReportFrame();
            f.setVisible(true);
        });
    }
}
