package yo.men.gui;

import yo.men.comparator.Comparator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private JLabel lbFirstFile;
    private JLabel lbSecondFile;
    private JButton btnSelectFirstFile;
    private JButton btnSelectSecondFile;
    private JProgressBar progressBar;
    private JButton btnClear;
    private JButton btnCompare;

    private String firstFile;
    private String secondFile;

    public MainFrame() {
        setContentPane(mainPanel);
        setTitle("Excel porównywarka");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        btnSelectFirstFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();

                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    firstFile = path;
                    lbFirstFile.setText(path);
                }
            }
        });
        btnSelectSecondFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();

                int result = fileChooser.showOpenDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    secondFile = path;
                    lbSecondFile.setText(path);
                }
            }
        });
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                firstFile = null;
                lbFirstFile.setText("Wybierz pierwszy plik");
                secondFile = null;
                lbSecondFile.setText("Wybierz drugi plik");
            }
        });
        btnCompare.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (firstFile != null && secondFile != null) {
                    try {
                        Comparator.compare(firstFile, secondFile, false);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Podczas pracy programu wystąpił błąd: " + ex.getMessage(), "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
                        throw new RuntimeException(ex);
                    }
                } else {
                    if (firstFile == null) {
                        JOptionPane.showMessageDialog(null, "Nie wybrano pierwszego pliku", "Nie odnaleziono pliku", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    JOptionPane.showMessageDialog(null, "Nie wybrano drugiego pliku", "Nie odnaleziono pliku", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }
}
