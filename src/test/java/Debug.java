import bean.Person;
import javassist.ClassPool;
import javassist.CtClass;
import priv.wufei.utils.basis.DateTimeUtils;
import priv.wufei.utils.bytecode.Asm;

import java.lang.reflect.Method;

/**
 * @author WuFei
 */

public class Debug {

    //  static Logger logger = LogManager.getLogger(Debug.class);

    public static void main(String[] args) {


        try {
            System.out.println(DateTimeUtils.nanoTimeTimekeeping(() -> {

                Method before = Debug.class.getDeclaredMethod("before");
                Method after = Debug.class.getDeclaredMethod("after");

                Class<Person> personClass = Asm.proxyExample(Person.class, before, after, "eat");

                byte[] classFileBytes = gg(personClass);

            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void before() {
        System.out.println("前");
    }

    public static void after() {
        System.out.println("后");
    }


    public static byte[] gg(Class<?> clz) throws Exception {

        ClassPool pool = ClassPool.getDefault();
        String parentClassName = clz.getTypeName();
        CtClass cc = pool.get(parentClassName);
        return cc.toBytecode();
    }

}




