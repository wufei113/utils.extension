package priv.wufei.utils.bytecode;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import priv.wufei.utils.basis.ReflectUtils;
import priv.wufei.utils.basis.function.OneConsumer;
import priv.wufei.utils.basis.function.TwoFunction;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * ASM字节码工具
 *
 * @author WuFei
 */
public final class Asm {

    /**
     * 不能实例化这个类
     */
    private Asm() {
    }

    /**
     * ASM版本
     */
    public static final int API = Opcodes.ASM7;
    /**
     * 构造方法标志
     */
    public static final String CONSTRUCTOR_SIGN = "<init>";
    /**
     * 作为子类时的标志<br>
     * 必须"$"打头
     */
    public static final String CHILD_CLASS_SIGN = "$Proxy";

    /**
     * 将动态生成类改造成原始类的子类<br>
     * 改变 class文件二进制类名, 将其命名为 Parent$Proxy，将其父类指定为Parent。<br>
     * 改变构造函数，将其中对父类构造函数的调用转换为对Parent构造函数的调用。
     *
     * @param classVisitor {@link ClassVisitor}对象
     * @return 进行操作后的 {@link ClassVisitor} 对象
     */
    public static ClassVisitor changeToChildClass(ClassVisitor classVisitor) {

        return new ClassVisitor(API, classVisitor) {

            /**
             * 新的父类名
             */
            String enhancedSuperName;

            @Override
            public void visit(int version, int access, String name,
                              String signature, String superName,
                              String[] interfaces) {
                // 改变类命名
                String enhancedName = name + CHILD_CLASS_SIGN;
                // 改变父类
                enhancedSuperName = name;
                super.visit(version, access, enhancedName, signature,
                        enhancedSuperName, interfaces);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

                MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

                MethodVisitor wrappedMv = mv;
                //是构造方法
                if (CONSTRUCTOR_SIGN.equals(name)) {

                    wrappedMv = new ChangeToChildConstructorMethodAdapter(mv, enhancedSuperName);
                }
                return wrappedMv;
            }

            /**
             * 将负责把Parent的构造函数改造成其子类Parent$Proxy的构造函数
             */
            class ChangeToChildConstructorMethodAdapter extends MethodVisitor {

                /**
                 * 新的父类名
                 */
                private String superClassName;

                public ChangeToChildConstructorMethodAdapter(MethodVisitor mv,
                                                             String superClassName) {

                    super(API, mv);
                    this.superClassName = superClassName;
                }

                @Override
                public void visitMethodInsn(int opcode, String owner, String name,
                                            String desc, boolean isInterface) {
                    // 调用父类的构造函数时
                    if (opcode == Opcodes.INVOKESPECIAL && CONSTRUCTOR_SIGN.equals(name)) {
                        // 改写父类为 superClassName
                        owner = superClassName;
                    }
                    super.visitMethodInsn(opcode, owner, name, desc, isInterface);
                }
            }
        };
    }

    /**
     * 指定方法进行代理
     *
     * @param classVisitor {@link ClassVisitor}对象
     * @param method       进行代理的方法
     * @param diy          自定义代理操作(返回null为删除方法)
     * @return 进行操作后的 {@link ClassVisitor} 对象
     */
    public static ClassVisitor methodProxy(ClassVisitor classVisitor,
                                           Method method,
                                           TwoFunction<ClassVisitor, MethodVisitor, MethodVisitor> diy) {

        //方法名
        final String methodName = method.getName();
        //描述符
        final String descriptor = Type.getMethodDescriptor(method);

        return new ClassVisitor(API, classVisitor) {

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

                try {
                    MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
                    //寻找指定名称的方法
                    if (methodName.equals(name) && descriptor.equals(desc)) {

                        return diy.apply(this, mv);
                    }
                    return mv;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    /**
     * 所有方法都进行代理<br>
     * 指定是否代理构造方法
     *
     * @param classVisitor       {@link ClassVisitor}对象
     * @param isConstructorProxy 是否把构造方法也进行代理(true:进行代理;false:不进行代理)
     * @param diy                自定义代理操作(返回null为删除方法)
     * @return 进行操作后的 {@link ClassVisitor} 对象
     */
    public static ClassVisitor methodProxy(ClassVisitor classVisitor,
                                           boolean isConstructorProxy,
                                           TwoFunction<ClassVisitor, MethodVisitor, MethodVisitor> diy) {

        return new ClassVisitor(API, classVisitor) {

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

                try {
                    MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);

                    if (isConstructorProxy || !CONSTRUCTOR_SIGN.equals(name)) {

                        return diy.apply(this, mv);
                    }
                    return mv;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    /**
     * 指定方法改名(新名称注意规范)
     *
     * @param classVisitor  {@link ClassVisitor}对象
     * @param method        方法
     * @param newMethodName 新的方法名
     * @return 进行操作后的 {@link ClassVisitor} 对象
     */
    public static ClassVisitor methodRename(ClassVisitor classVisitor,
                                            Method method,
                                            String newMethodName) {

        //方法名
        final String methodName = method.getName();
        //描述符
        final String descriptor = Type.getMethodDescriptor(method);

        return new ClassVisitor(API, classVisitor) {

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

                //寻找指定名称的方法
                if (methodName.equals(name) && descriptor.equals(desc)) {

                    name = newMethodName;
                }
                return cv.visitMethod(access, name, desc, signature, exceptions);
            }
        };
    }

    /**
     * 方法代理操作
     *
     * @param methodVisitor {@link MethodVisitor}对象
     * @param methodBefore  前拦截
     * @param methodAfter   后拦截
     * @return 进行操作后的 {@link MethodVisitor} 对象
     */
    public static MethodVisitor methodProxyOperate(MethodVisitor methodVisitor,
                                                   OneConsumer<MethodVisitor> methodBefore,
                                                   OneConsumer<MethodVisitor> methodAfter) {

        return new MethodVisitor(API, methodVisitor) {

            /**
             * 此方法在访问方法的头部时被访问到，仅被访问一次
             */
            @Override
            public void visitCode() {
                try {
                    //前拦截
                    methodBefore.accept(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.visitCode();
            }

            /**
             * 此方法可以获取方法中每一条指令的操作类型，被访问多次
             * @param opcode  要访问的指令的操作码
             */
            @Override
            public void visitInsn(int opcode) {

                //在方法结尾处添加新指令，则应判断
                boolean b1 = opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN;
                boolean b2 = opcode == Opcodes.ATHROW;

                if (b1 || b2) {
                    try {
                        //后拦截
                        methodAfter.accept(this);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                super.visitInsn(opcode);
            }
        };
    }

    /**
     * <p>
     * 示例方法
     * </p>
     * 对一个方法进行代理(将动态生成类改造成原始类的子类)
     *
     * @param clz            进行代理的类的{@link Class}类型
     * @param methodBefore   前拦截方法(必须是静态方法)
     * @param methodAfter    后拦截方法(必须是静态方法)
     * @param methodName     进行代理的方法名
     * @param parameterTypes 方法形参类型数组
     * @return 新构建的class文件的二进制数组
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> proxyExample(Class<T> clz,
                                            Method methodBefore,
                                            Method methodAfter,
                                            String methodName,
                                            Class<?>... parameterTypes) {

        Class childClass = null;

        try {
            Method method = clz.getDeclaredMethod(methodName, parameterTypes);

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            //改为被代理的子类
            ClassVisitor cv1 = changeToChildClass(cw);
            //对指定方法进行代理
            ClassVisitor cv2 = methodProxy(cv1, method,
                    (classVisitor, methodVisitor) -> methodProxyOperate(methodVisitor,
                            (mv) -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, methodBefore.getDeclaringClass().getTypeName(),
                                    methodBefore.getName(), Type.getMethodDescriptor(methodBefore), false),
                            (mv) -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, methodAfter.getDeclaringClass().getTypeName(),
                                    methodAfter.getName(), Type.getMethodDescriptor(methodAfter), false)));

            String parentClassName = clz.getTypeName();
            /*
             * ClassReader 将class解析成byte 数组
             * 然后会通过accept方法去按顺序调用绑定对象（继承了ClassVisitor的实例）的方法
             * 可以视为一个事件的生产者
             */
            ClassReader cr = new ClassReader(parentClassName);

            cr.accept(cv2, API);

            byte[] bytes = cw.toByteArray();

            childClass = ReflectUtils.defineClass(parentClassName + CHILD_CLASS_SIGN, bytes);

        } catch (IOException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return childClass;
    }

}
