package yo.men.gui;

import yo.men.comparator.Comparator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
                JFileChooser fileChooser = new JFileChooser(".");

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
                JFileChooser fileChooser = new JFileChooser(".");

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
                progressBar.setValue(0);
            }
        });
        btnCompare.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (firstFile != null && secondFile != null) {

                    Thread th = new Thread() {
                        @Override
                        public void run() {
                            try {
                                Comparator.compare(firstFile, secondFile, false);
                                JOptionPane.showMessageDialog(null, "Zakończono porównywanie.\nZmiany zostały naniesione w pliku " + secondFile, "Ukończono", JOptionPane.INFORMATION_MESSAGE);
                                btnCompare.setEnabled(true);
                                btnClear.setEnabled(true);
                                btnSelectFirstFile.setEnabled(true);
                                btnSelectSecondFile.setEnabled(true);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Podczas pracy programu wystąpił błąd:\n" + ex.getMessage(), "Wystąpił błąd", JOptionPane.ERROR_MESSAGE);
                                btnCompare.setEnabled(true);
                                btnClear.setEnabled(true);
                                btnSelectFirstFile.setEnabled(true);
                                btnSelectSecondFile.setEnabled(true);
                            }
                        }
                    };
                    btnCompare.setEnabled(false);
                    btnClear.setEnabled(false);
                    btnSelectFirstFile.setEnabled(false);
                    btnSelectSecondFile.setEnabled(false);
                    th.start();
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
