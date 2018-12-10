
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.jupiter.api.Test;
import priv.wufei.utils.basis.functions.RunnerThrow;

import java.io.File;
import java.io.FileOutputStream;

import static priv.wufei.utils.basis.DateTimeUtils.nanoTimeTimekeeping;

/**
 * @author WuFei
 */
public class Debug {

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

            // 创建工作薄
            HSSFWorkbook workbook = new HSSFWorkbook();
            // 创建工作表
            HSSFSheet sheet = workbook.createSheet("sheet1");

            for (int row = 0; row < 10; row++) {
                HSSFRow rows = sheet.createRow(row);
                for (int col = 0; col < 10; col++) {
                    // 向工作表中添加数据
                    rows.createCell(col).setCellValue("data" + row + col);
                }
            }

            File xlsFile = new File("C:\\Users\\wufei\\Desktop\\poi.xls");
            FileOutputStream xlsStream = new FileOutputStream(xlsFile);
            workbook.write(xlsStream);
        });
    }

}




