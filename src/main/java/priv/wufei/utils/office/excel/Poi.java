package priv.wufei.utils.office.excel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import priv.wufei.utils.basis.functions.ConsumerThrow3;

import static priv.wufei.utils.basis.IOUtils.output;

/**
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

            HSSFRow currentRow = sheet.createRow(row);

            for (int col = 0; col < columns; col++) {
                //得到当前单元格
                HSSFCell currentCell = currentRow.createCell(col);
                //向单元格添加数据
                fillData.accept(currentCell, row, col);
            }
        }
        //写出到磁盘
        output(destFilePath, workbook::write);
    }
}
