import priv.wufei.utils.basis.DateTimeUtils;
import priv.wufei.utils.json.FastJson;

/**
 * @author WuFei
 */

public class Debug {

    public static void main(String[] args) {

        System.out.println(DateTimeUtils.nanoTimeTimekeeping(() -> {
            int[] arr = new int[]{1, 2, 3, 4, 5, 6};
            System.out.println(FastJson.toJSONString(arr));

        }));
    }
}




