import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static priv.wufei.utils.basis.DateTimeUtils.nanoTimeTimekeeping;

/**
 * @author WuFei
 */

public class Debug {

    static Logger logger = LoggerFactory.getLogger(Debug.class);

    public static void main(String[] args) {

        try {
            System.out.println(nanoTimeTimekeeping(() -> {


            }));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}




