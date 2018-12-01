import bean.Person;
import priv.wufei.utils.basis.DateTimeUtils;
import priv.wufei.utils.basis.ReflectUtils;
import priv.wufei.utils.bytecode.Javassist;

/**
 * @author WuFei
 */

public class Debug {

    public Debug(int i) {
    }

    public Debug() {
    }

    public static void main(String[] args) {
        System.out.println(DateTimeUtils.nanoTimeTimekeeping(() -> {
            Class<Person> personClass = Javassist.proxy(Person.class, "System.out.println(\"前\");",
                    "System.out.println(\"后\");", "sleep", boolean.class);

            Person person = ReflectUtils.getInstance(personClass);
            person.sleep(false);

        }));
    }

    public static void before() {
        System.out.println("前");
    }

    public static void after() {
        System.out.println("后");
    }

}




