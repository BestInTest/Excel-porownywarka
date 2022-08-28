package yo.men.comparator;

import org.apache.poi.ss.util.CellReference;

public class Data {
    private int sheetIndex;
    private String sheetName;
    private CellReference cellAddr;
    private Object value;

    public Data(int sheetIndex, CellReference cellAddr, Object value) {
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
