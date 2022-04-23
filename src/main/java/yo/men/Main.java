package yo.men;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Console;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Collection;

public class Main {
    public static void main(String[] args) throws IOException {

        Console sc = System.console();
        if (sc == null) {
            System.err.println("Nie wykryto konsoli");
            System.exit(1);
        }

        System.out.println("Podaj lokalizacje pliku bazowego: ");
        String fileLocation = sc.readLine();

        System.out.println("Podaj lokalizacje pliku do porownania: ");
        String fileLocation2 = sc.readLine();

        long startTime = System.currentTimeMillis();

        FileInputStream file = new FileInputStream(fileLocation);
        Workbook actual = new XSSFWorkbook(file);

        FileInputStream file2 = new FileInputStream(fileLocation2);
        Workbook modified = new XSSFWorkbook(file2);

        List<Data> actualData = new LinkedList<>();
        List<Data> modifiedData = new LinkedList<>();

        System.out.println();
        System.out.println("Reading " + fileLocation);
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
        System.out.println("Reading " + fileLocation2);
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
        }
        System.out.println("Searching for removed cells...");
        toColor.addAll(searchRemoved(actualData, modifiedData));
        long time = System.currentTimeMillis()-startTime;
        System.out.println("\nDetected " + toColor.size() + " modified cells in " + time + " ms");

        if (!toColor.isEmpty()) {
            for (Data data : toColor) {
                Sheet sheet2 = modified.getSheetAt(data.getSheetIndex());
                modified.setSheetName(data.getSheetIndex(), data.getSheetName());
                CellReference cellAddr2 = data.getCellAddr();
                Row row2 = sheet2.getRow(cellAddr2.getRow());

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
            FileOutputStream out = new FileOutputStream(fileLocation2);
            modified.write(out);
            out.close();

            modified.close();
        } else {
            System.out.println("Arkusze są identyczne");
        }
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

    private static Collection<? extends Data> searchRemoved(List<Data> data1List, List<Data> data2List) {
        Collection<Data> diff = new ArrayList<>();
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
        }
        return diff;
    }

    public static class Data {
        private int sheetIndex;
        private String sheetName;
        private CellReference cellAddr;
        private Object value;

        Data(int sheetIndex, CellReference cellAddr, Object value) {
            this.sheetIndex = sheetIndex;
            this.cellAddr = cellAddr;
            this.value = value;
        }

        public void setSheetIndex(int sheetIndex) {
            this.sheetIndex = sheetIndex;
        }

        public void setSheetName(String sheetName) {
            this.sheetName = sheetName;
        }

        public void setCellAddr(CellReference cellAddr) {
            this.cellAddr = cellAddr;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public int getSheetIndex() {
            return sheetIndex;
        }

        public String getSheetName() {
            return sheetName;
        }

        public CellReference getCellAddr() {
            return cellAddr;
        }

        public Object getValue() {
            return value;
        }
    }
}
