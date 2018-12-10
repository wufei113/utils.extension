
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;
import priv.wufei.utils.basis.functions.RunnerThrow;

import java.io.File;
import java.io.IOException;

import static priv.wufei.utils.basis.DateTimeUtils.nanoTimeTimekeeping;

/**
 * @author WuFei
 */
public class Debug {

    private static final String desktop = "C:\\Users\\wufei\\Desktop\\";

    static void test(RunnerThrow diy) {
        // Logger logger = LoggerFactory.getLogger(Debug.class);
        try {
            System.out.println(nanoTimeTimekeeping(diy));
        } catch (Exception e) {
            e.printStackTrace();
            // logger.error(e.getMessage(), e);
        }
    }


    @Test
    void test() {

        test(() -> {
            Sheet[] sheets = getSheets(desktop + "xx.xls");

            Sheet sheet = sheets[0];
            // 获得行数
            int rows = sheet.getLastRowNum() + 1;
            // 获得列数，先获得一行，在得到改行列数
            Row tmp = sheet.getRow(0);
            int cols = tmp.getLastCellNum();
            // 读取数据
            for (int row = 0; row < rows; row++) {
                Row r = sheet.getRow(row);
                for (int col = 0; col < cols; col++) {
                    System.out.printf("%10s", r.getCell(col).getStringCellValue());
                }
                System.out.println();
            }
        });
    }

    public static Workbook getWorkbook(String srcFilePath) throws IOException {
        // 获得工作簿
        return WorkbookFactory.create(new File(srcFilePath));
    }

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

    public static Sheet getSheet(String srcFilePath, String sheetName) throws IOException {

        Workbook workbook = getWorkbook(srcFilePath);

        return workbook.getSheet(sheetName);
    }



    /*

     */
}




