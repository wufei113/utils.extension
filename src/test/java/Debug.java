import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import priv.wufei.utils.basis.DateTimeUtils;
import priv.wufei.utils.basis.FileUtils;

/**
 * @author WuFei
 */

public class Debug {

    static Logger logger = LogManager.getLogger(Debug.class);

    public static void main(String[] args) {


        try {
            System.out.println(DateTimeUtils.nanoTimeTimekeeping(() -> {

                FileUtils.getProjectPathOfJAR(int.class);

            }));
        } catch (Exception e) {
            logger.error(e::getMessage, e);
        }
    }


}




