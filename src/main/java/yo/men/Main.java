package yo.men;

import yo.men.comparator.Comparator;
import yo.men.gui.MainFrame;

import javax.swing.*;
import java.io.Console;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    private static MainFrame gui;

    public static void main(String[] args) {
        try {

            String fileLocation;
            String fileLocation2;
            String arg = Arrays.toString(args).toLowerCase(Locale.ROOT);

            if (arg.contains("--cli")) {

                Console sc = System.console();
                if (sc != null) {
                    System.out.println("Podaj lokalizacje pliku bazowego: ");
                    fileLocation = sc.readLine();

                    System.out.println("Podaj lokalizacje pliku do porownania: ");
                    fileLocation2 = sc.readLine();

                    Comparator.compare(fileLocation, fileLocation2, true);
                } else {
                    if (arg.contains("--ignore-console")) {
                        System.err.println("Nie wykryto konsoli. Znaki specjalne moga nie dzialac poprawnie\n");

                        Scanner scanner = new Scanner(System.in);

                        System.out.println("Podaj lokalizacje pliku bazowego: ");
                        fileLocation = scanner.nextLine();

                        System.out.println("Podaj lokalizacje pliku do porownania: ");
                        fileLocation2 = scanner.nextLine();

                        Comparator.compare(fileLocation, fileLocation2, true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Nie wykryto konsoli. Uruchom program z konsoli lub korzystajac ze skryptu uruchamiajacego.", "Blad konsoli | github.com/BestInTest/Excel-porownywarka", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                        return;
                    }
                }
            } else {
                gui = new MainFrame();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MainFrame getGui() {
        return gui;
    }

}
