package priv.wufei.tools.bytecode;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import priv.wufei.utils.basis.ArrayUtils;
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
    private static final int API = Opcodes.ASM7;

    /**
     * 对一个指定方法进行代理
     *
     * @param classTypeName    进行代理的类的二进制名称(如{@code "java.lang.Object"})
     * @param proxyMethodNames 进行代理的方法名数组
     * @param before           前拦截方法(必须是静态方法)
     * @param after            后拦截方法(必须是静态方法)
     * @return 新构建的class文件的二进制数组
     */
    public static byte[] proxy(String classTypeName,
                               String[] proxyMethodNames,
                               Method before,
                               Method after) {

        byte[] bytes = null;

        try {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            ClassVisitor cv = Asm.methodProxy(cw, proxyMethodNames,
                    (classVisitor, methodVisitor) -> Asm.methodProxyOperate(methodVisitor,
                            (mv) -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, before.getDeclaringClass().getTypeName(),
                                    before.getName(), Type.getMethodDescriptor(before), false),
                            (mv) -> mv.visitMethodInsn(Opcodes.INVOKESTATIC, after.getDeclaringClass().getTypeName(),
                                    after.getName(), Type.getMethodDescriptor(after), false)));

            /*
             * ClassReader 将class解析成byte 数组
             * 然后会通过accept方法去按顺序调用绑定对象（继承了ClassVisitor的实例）的方法
             * 可以视为一个事件的生产者
             */
            ClassReader cr = new ClassReader(classTypeName);

            cr.accept(cv, API);

            bytes = cw.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 指定方法代理
     *
     * @param classVisitor {@link ClassVisitor}对象
     * @param methodNames  进行代理的方法名数组
     * @param aop          自定义代理操作(返回null为删除方法)
     * @return 进行操作后的 {@link ClassVisitor} 对象
     */
    public static ClassVisitor methodProxy(ClassVisitor classVisitor,
                                           String[] methodNames,
                                           TwoFunction<ClassVisitor, MethodVisitor, MethodVisitor> aop) {

        return new ClassVisitor(API, classVisitor) {

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

                MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
                //寻找指定名称的方法
                if (ArrayUtils.contains(methodNames, name)) {
                    //访问需要修改的方法
                    MethodVisitor proxy = null;

                    try {
                        proxy = aop.apply(this, mv);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return proxy;
                }
                return mv;
            }
        };
    }

    /**
     * 指定方法改名
     *
     * @param classVisitor  {@link ClassVisitor}对象
     * @param methodName    方法名
     * @param newMethodName 新方法名
     * @return 进行操作后的 {@link ClassVisitor} 对象
     */
    public static ClassVisitor methodRename(ClassVisitor classVisitor,
                                            String methodName,
                                            String newMethodName) {

        return new ClassVisitor(API, classVisitor) {

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

                //寻找指定名称的方法
                if (methodName.equals(name)) {

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
     * @param before        前拦截
     * @param after         后拦截
     * @return 进行操作后的 {@link MethodVisitor} 对象
     */
    public static MethodVisitor methodProxyOperate(MethodVisitor methodVisitor,
                                                   OneConsumer<MethodVisitor> before,
                                                   OneConsumer<MethodVisitor> after) {

        return new MethodVisitor(API, methodVisitor) {

            /**
             * 此方法在访问方法的头部时被访问到，仅被访问一次
             */
            @Override
            public void visitCode() {
                try {
                    //前拦截
                    before.accept(this);
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
                if (opcode == Opcodes.IRETURN || opcode == Opcodes.RETURN) {
                    try {
                        //后拦截
                        after.accept(this);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                super.visitInsn(opcode);
            }
        };
    }

}
