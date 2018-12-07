
import org.junit.jupiter.api.Test;
import priv.wufei.utils.basis.functions.RunnerThrow;

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

        });
    }

}




