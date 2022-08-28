package yo.men.comparator;

import lukfor.progress.TaskService;
import lukfor.progress.tasks.ITaskRunnable;
import lukfor.progress.tasks.monitors.ITaskMonitor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import yo.men.Main;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Comparator {
    ////przetestować czy w przypadku javy headless będzie działać bo może brakować JProgressBar
    public static void compare(String fileLoc1, String fileLoc2, boolean cli) throws IOException {
        long startTime = System.currentTimeMillis();

        FileInputStream file = new FileInputStream(fileLoc1);
        Workbook actual = new XSSFWorkbook(file);

        FileInputStream file2 = new FileInputStream(fileLoc2);
        Workbook modified = new XSSFWorkbook(file2);

        List<Data> actualData = new LinkedList<>();
        List<Data> modifiedData = new LinkedList<>();

        System.out.println();
        System.out.println("Reading " + fileLoc1);
        for (int i = 0; i < actual.getNumberOfSheets(); i++) {
            Sheet sheet = actual.getSheetAt(i);
            for (Row myrow : sheet) {
                for (Cell mycell : myrow) {
                    Data cellData;
                    CellReference cellAddr = new CellReference(mycell.getRowIndex(), mycell.getColumnIndex());
                    switch (mycell.getCellType()) {
                        case NUMERIC:
                            cellData = new Data(i, cellAddr, mycell.getNumericCellValue());
                            cellData.setSheetName(sheet.getSheetName());
                            actualData.add(cellData);
                            break;
                        case STRING:
                            cellData = new Data(i, cellAddr, mycell.getStringCellValue());
                            cellData.setSheetName(sheet.getSheetName());
                            actualData.add(cellData);
                            break;
                    }

                }
            }
        }
        System.out.println("Reading " + fileLoc2);
        for (int i = 0; i < modified.getNumberOfSheets(); i++) {
            Sheet sheet = modified.getSheetAt(i);
            for (Row myrow : sheet) {
                for (Cell mycell : myrow) {
                    Data cellData;
                    switch (mycell.getCellType()) {
                        case NUMERIC:
                            cellData = new Data(i, new CellReference(mycell.getRowIndex(), mycell.getColumnIndex()), mycell.getNumericCellValue());
                            cellData.setSheetName(sheet.getSheetName());
                            modifiedData.add(cellData);
                            break;
                        case STRING:
                            cellData = new Data(i, new CellReference(mycell.getRowIndex(), mycell.getColumnIndex()), mycell.getStringCellValue());
                            cellData.setSheetName(sheet.getSheetName());
                            modifiedData.add(cellData);
                            break;
                    }
                }
            }
        }

        System.out.println("First file data size: " + actualData.size());
        System.out.println("Second file data size: " + modifiedData.size());
        System.out.println("Comparing...\n");

        List<Data> toColor = new ArrayList<>();

        if (cli) {
            ITaskRunnable task = new ITaskRunnable() {

                @Override
                public void run(ITaskMonitor monitor) {

                    long max = Math.max(actualData.size(), modifiedData.size());
                    monitor.begin("Wyszukiwanie zmienionych komorek", max);

                    for (Data data : modifiedData) {
                        Sheet sheet2 = modified.getSheetAt(data.getSheetIndex());
                        CellReference cellAddr2 = data.getCellAddr();
                        Row row2 = sheet2.getRow(cellAddr2.getRow());
                        Cell cell2 = row2.getCell(cellAddr2.getCol());

                        Sheet sheet1 = actual.getSheetAt(data.getSheetIndex());
                        Row row1 = sheet1.getRow(cellAddr2.getRow());

                        try {
                            if (row1.getCell(cellAddr2.getCol(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK) != null) {
                                Cell cell1 = row1.getCell(cellAddr2.getCol());
                                Data cell1Data = searchData(actualData, data); // Na podstawie zmodyfikowanych danych szukana jest komórka w oryginalnym arkuszu

                                if (isModified(data, cell1Data)) {
                                    toColor.add(data);
                                }
                            } else {
                                toColor.add(data);
                            }
                        } catch (NullPointerException ignored) {
                            toColor.add(data);
                        }
                        monitor.worked(1);
                    }

                    monitor.done();

                }
            };

            //Wyłączenie koloru paska
            TaskService.setAnimated(true);
            TaskService.setAnsiColors(false);
            TaskService.setTarget(System.err);

            TaskService.run(task);
        } else {
            long max = Math.max(actualData.size(), modifiedData.size()) * 2L; // mnożenie x2 dlatego że jest 1 progeressbar w gui, a 2 taski

            long worked = 0;
            for (Data data : modifiedData) {
                Sheet sheet2 = modified.getSheetAt(data.getSheetIndex());
                CellReference cellAddr2 = data.getCellAddr();
                Row row2 = sheet2.getRow(cellAddr2.getRow());
                Cell cell2 = row2.getCell(cellAddr2.getCol());

                Sheet sheet1 = actual.getSheetAt(data.getSheetIndex());
                Row row1 = sheet1.getRow(cellAddr2.getRow());

                try {
                    if (row1.getCell(cellAddr2.getCol(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK) != null) {
                        Cell cell1 = row1.getCell(cellAddr2.getCol());
                        Data cell1Data = searchData(actualData, data); // Na podstawie zmodyfikowanych danych szukana jest komórka w oryginalnym arkuszu

                        if (isModified(data, cell1Data)) {
                            toColor.add(data);
                        }
                    } else {
                        toColor.add(data);
                    }
                } catch (NullPointerException ignored) {
                    toColor.add(data);
                }
                worked++;
                int progress = (int) Math.round(100.0 * ((double)worked / (double)max));
                Main.getGui().getProgressBar().setValue(progress);
            }
        }

        System.out.println("Searching for removed cells...");
        toColor.addAll(searchRemoved(actualData, modifiedData, cli));

        long time = System.currentTimeMillis()-startTime;
        System.out.println("\nDetected " + toColor.size() + " modified cells in " + time + " ms");

        if (!toColor.isEmpty()) {
            for (Data data : toColor) {
                Sheet sheet2 = modified.getSheetAt(data.getSheetIndex());
                modified.setSheetName(data.getSheetIndex(), data.getSheetName());
                CellReference cellAddr2 = data.getCellAddr();
                Row row2 = sheet2.getRow(cellAddr2.getRow());
                if (row2 == null) {
                    sheet2.createRow(cellAddr2.getRow());
                    row2 = sheet2.getRow(cellAddr2.getRow());
                }

                Cell cell2 = row2.getCell(cellAddr2.getCol(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                /*
                if (cell2 == null) {
                    row2.createCell(cellAddr2.getCol());
                    cell2.setCellValue("<Removed>");
                }
                */

                CellStyle style = modified.createCellStyle();
                style.setFillForegroundColor(IndexedColors.RED.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cell2.setCellStyle(style);
            }
            FileOutputStream out = new FileOutputStream(fileLoc2);
            modified.write(out);
            out.close();

            modified.close();
        } else {
            System.out.println("Arkusze są identyczne");
            if (!cli) {
                JOptionPane.showMessageDialog(null, "Nie znaleziono zmian. Arkusze są identyczne.", "Ukończono", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private static Data searchData(List<Data> data1List, Data data2) {
        Data toret = null;

        for (Data data1 : data1List) {
            if (data1.getCellAddr().equals(data2.getCellAddr())) {
                if (data1.getSheetIndex() == data2.getSheetIndex()) {
                    toret = data1;
                }
            }
        }
        return toret;
    }

    private static Collection<? extends Data> searchRemoved(List<Data> data1List, List<Data> data2List, boolean cli) {

        Collection<Data> diff = new ArrayList<>();

        if (cli) {
            ITaskRunnable task = new ITaskRunnable() {

                @Override
                public void run(ITaskMonitor monitor) {

                    long max = Math.max(data1List.size(), data2List.size());
                    monitor.begin("Szukanie usunietych danych", max);

                    for (Data data1 : data1List) {
                        String addr1 = data1.getCellAddr().formatAsString();
                        boolean removed = true;
                        for (Data data2 : data2List) {
                            String addr2 = data2.getCellAddr().formatAsString();
                            if (data1.getSheetIndex() == data2.getSheetIndex()) {
                                if (addr1.equals(addr2)) {
                                    //diff.add(data2.getCellAddr());
                                    removed = false;
                                    break;
                                }
                            }
                        }
                        if (removed) {
                            diff.add(data1);
                        }
                        monitor.worked(1);
                    }

                    monitor.done();

                }
            };
            TaskService.run(task);
        } else {
            long max = Math.max(data1List.size(), data2List.size()) * 2L; // mnożenie x2 dlatego że jest 1 progeressbar w gui, a 2 taski
            long worked = 0;
            int progress = Main.getGui().getProgressBar().getValue();
            for (Data data1 : data1List) {
                String addr1 = data1.getCellAddr().formatAsString();
                boolean removed = true;
                for (Data data2 : data2List) {
                    String addr2 = data2.getCellAddr().formatAsString();
                    if (data1.getSheetIndex() == data2.getSheetIndex()) {
                        if (addr1.equals(addr2)) {
                            //diff.add(data2.getCellAddr());
                            removed = false;
                            break;
                        }
                    }
                }
                if (removed) {
                    diff.add(data1);
                }
                worked++;
                int taskProgress = (int) Math.round(100.0 * ((double)worked / (double)max));
                int totalProgress = progress + taskProgress;
                Main.getGui().getProgressBar().setValue(totalProgress);
            }
        }

        return diff;
    }

    private static boolean isModified(Data data2, Data data1) {
        if (data1 == null) {
            //System.out.println("data1 is null");
            return true;
        }
        if (data2 == null) {
            //System.out.println("data2 is null");
            return true;
        }
        if (!data1.getCellAddr().toString().equals(data2.getCellAddr().toString())) {
            //System.out.println("addr not equal ( " + data1.getCellAddr().toString() + "/" + data2.getCellAddr().toString() + " )");
            return true;
        }
        if (!data1.getValue().equals(data2.getValue())) {
            //System.out.println("value not equal ( " + data1.getValue() + "/" + data2.getValue() + " )");
            return true;
        }
        //System.out.println("not modified");
        return false;
    }
}
