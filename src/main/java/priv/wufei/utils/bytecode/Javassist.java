package priv.wufei.utils.bytecode;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import priv.wufei.utils.basis.ArrayUtils;
import priv.wufei.utils.basis.ReflectUtils;

/**
 * Javassist字节码工具
 *
 * @author WuFei
 */
public final class Javassist {

    /**
     * 不能实例化这个类
     */
    private Javassist() {
    }

    /**
     * 作为子类时的标志<br>
     * 必须"$"打头
     */
    public static final String CHILD_CLASS_SIGN = "$Proxy";

    /**
     * <p>
     * 示例方法
     * </p>
     * 对一个方法进行代理(将动态生成类改造成原始类的子类)
     *
     * @param clz            进行代理的类的{@link Class}类型
     * @param methodBefore   前拦截方法字符串
     * @param methodAfter    后拦截方法字符串
     * @param methodName     进行代理的方法名
     * @param parameterTypes 进行代理的方法参数数组
     * @param <T>            原始类类型
     * @return 新构建的class文件的二进制数组
     * @throws Exception Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> proxy(Class<T> clz,
                                     String methodBefore,
                                     String methodAfter,
                                     String methodName,
                                     Class<?>... parameterTypes) throws Exception {

        ClassPool pool = ClassPool.getDefault();

        String parentClassName = clz.getTypeName();
        CtClass cc = pool.get(parentClassName);
        /*将动态生成类改造成原始类的子类*/
        //设置新类名
        cc.setName(parentClassName + CHILD_CLASS_SIGN);
        //设置父类
        cc.setSuperclass(pool.get(parentClassName));

        /*对方法参数数组进行转换*/
        CtClass[] ctClasses = null;

        if (ArrayUtils.isNotEmpty(parameterTypes)) {

            int length = parameterTypes.length;
            String[] arr = new String[length];

            for (int i = 0; i < length; i++) {
                arr[i] = parameterTypes[i].getTypeName();
            }
            ctClasses = pool.get(arr);
        }
        /*得到欲代理的方法*/
        CtMethod newMethod = cc.getDeclaredMethod(methodName, ctClasses);

        /*进行代理*/
        //前拦截
        newMethod.insertBefore(methodBefore);
        //后拦截
        newMethod.insertAfter(methodAfter);

        /*转换为二进制数组*/
        byte[] bytes = cc.toBytecode();

        return (Class<T>) ReflectUtils.defineClass(parentClassName + CHILD_CLASS_SIGN, bytes);
    }

}
