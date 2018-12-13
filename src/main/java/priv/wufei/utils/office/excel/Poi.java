package priv.wufei.utils.office.excel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import priv.wufei.utils.basis.functions.ConsumerThrow3;

import java.io.File;
import java.io.IOException;

import static priv.wufei.utils.basis.IOUtils.output;

/**
 * POI框架
 *
 * @author WuFei
 */
public final class Poi {

    /**
     * 不能实例化这个类
     */
    private Poi() {
    }

    /**
     * 创建excel文件
     *
     * @param destFilePath excel输出文件路径
     * @param sheetName    工作表名
     * @param rows         总行数
     * @param columns      总列数
     * @param fillData     对指定行列的单元格填装数据
     * @throws Exception Exception
     */
    public static void write(String destFilePath,
                             String sheetName,
                             int rows,
                             int columns,
                             ConsumerThrow3<HSSFCell, Integer, Integer> fillData) throws Exception {

        //创建工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        //创建工作表
        HSSFSheet sheet = workbook.createSheet(sheetName);

        for (int row = 0; row < rows; row++) {

            for (int col = 0; col < columns; col++) {
                //得到当前单元格
                HSSFCell currentCell = sheet.createRow(row).createCell(col);
                //向单元格添加数据
                fillData.accept(currentCell, row, col);
            }
        }
        //写出到磁盘
        output(destFilePath, workbook::write);
    }

    /**
     * 获得工作薄
     *
     * @param srcFilePath 输入文键地址
     * @return {@link Workbook}
     * @throws IOException IOException
     */
    public static Workbook getWorkbook(String srcFilePath) throws IOException {
        // 获得工作簿
        return WorkbookFactory.create(new File(srcFilePath));
    }

    /**
     * 获得所有工作表
     *
     * @param srcFilePath 输入文键地址
     * @return {@link Sheet}数组
     * @throws IOException IOException
     */
    public static Sheet[] getSheets(String srcFilePath) throws IOException {

        Workbook workbook = getWorkbook(srcFilePath);
        // 获得工作表个数
        int sheetCount = workbook.getNumberOfSheets();

        Sheet[] sheets = new Sheet[sheetCount];
        // 遍历工作表
        for (int i = 0; i < sheetCount; i++) {

            sheets[i] = workbook.getSheetAt(i);
        }
        return sheets;
    }

    /**
     * 获得指定工作表
     *
     * @param srcFilePath 输入文键地址
     * @param sheetName   工作表名
     * @return {@link Sheet}
     * @throws IOException IOException
     */
    public static Sheet getSheet(String srcFilePath, String sheetName) throws IOException {

        Workbook workbook = getWorkbook(srcFilePath);

        return workbook.getSheet(sheetName);
    }

    /**
     * 获得指定工作表的总行数
     *
     * @param sheet 工作表
     * @return int
     */
    public static int getRows(Sheet sheet) {

        return sheet.getPhysicalNumberOfRows();
    }

    /**
     * 获得指定行的列数
     *
     * @param sheet 工作表
     * @param row   行号
     * @return int
     */
    public static int getColumns(Sheet sheet, int row) {

        return sheet.getRow(row).getPhysicalNumberOfCells();
    }

    /**
     * 获得指定工作表的最长一列的列数
     *
     * @param sheet 工作表
     * @return int
     */
    public static int getLongestColumns(Sheet sheet) {

        int columns = 0;

        int lastRowNum = sheet.getLastRowNum();

        for (int i = 0; i <= lastRowNum; i++) {
            //每行的有效单元格
            int cells = getColumns(sheet, i);

            columns = columns > cells ? columns : cells;
        }
        return columns;
    }

    /**
     * 获得指定工作表指定行列的单元格
     *
     * @param sheet 工作表
     * @param row   行号
     * @param col   列号
     * @return {@link Cell}
     */
    public static Cell getCell(Sheet sheet, int row, int col) {

        return sheet.getRow(row).getCell(col);
    }

}
