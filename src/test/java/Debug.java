import priv.wufei.utils.basis.DateTimeUtils;

/**
 * @author WuFei
 */

public class Debug {

    public static void main(String[] args) {

        try {
            System.out.println(DateTimeUtils.nanoTimeTimekeeping(() -> {
                System.out.println(String.format("我是%s", "神"));
                System.out.println(11);
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}




