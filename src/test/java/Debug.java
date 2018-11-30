import bean.Person;
import priv.wufei.tools.bytecode.Asm;
import priv.wufei.utils.basis.DateTimeUtils;
import priv.wufei.utils.basis.IOUtils;

import java.lang.reflect.Method;

/**
 * @author WuFei
 */

public class Debug {

    public static void main(String[] args) {

        System.out.println(DateTimeUtils.nanoTimeTimekeeping(() -> {

            String typeName = Person.class.getTypeName();

            String[] mn = {"eat", "setName", "getAge"};

            Method before = Debug.class.getDeclaredMethod("before");

            Method after = Debug.class.getDeclaredMethod("after");

            byte[] proxy = Asm.proxy(typeName, mn, before, after);

            IOUtils.output("C:\\Users\\wufei\\Desktop\\asm4.class", (bos) -> bos.write(proxy));

        }));
    }

    public static void before() {
        System.out.println("前");
    }

    public static void after() {
        System.out.println("后");
    }


}


