package priv.wufei.utils.xml;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import priv.wufei.utils.basis.function.ConsumerThrow1;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * dom4j工具
 *
 * @author WuFei
 */
public final class Dom4j {

    /**
     * 不能实例化这个类
     */
    private Dom4j() {
    }

    /**
     * 解析
     *
     * @param srcFilePath 输入xml文件路径
     * @param diy         对根节点的自定义操作
     * @throws Exception Exception
     */
    public static void parse(String srcFilePath, ConsumerThrow1<Element> diy) throws Exception {

        parse(new File(srcFilePath), diy);
    }

    /**
     * 解析
     *
     * @param srcFile 输入xml文件
     * @param diy     对根节点的自定义操作
     * @throws Exception Exception
     */
    public static void parse(File srcFile, ConsumerThrow1<Element> diy) throws Exception {

        // 创建SAXReader的对象reader
        SAXReader reader = new SAXReader();
        // 通过reader对象的read方法加载.xml文件,获取document对象。
        Document document = reader.read(srcFile);
        // 通过document对象获取根节点
        Element root = document.getRootElement();

        diy.accept(root);
    }

    /**
     * 递归遍历打印DOM树信息
     *
     * @param element 节点
     * @param i       用于格式化
     */
    public static void traversal(Element element, int i) {

        String space = "    ".repeat(i);
        System.out.println();
        System.out.println(space + "节点名：" + element.getName());
        // 获取element的属性名以及属性值
        List<Attribute> attrs = element.attributes();
        for (Attribute attr : attrs) {
            System.out.println(space + "  " + "属性名：" + attr.getName() + "  属性值：" + attr.getValue().trim());
        }

        //获取节点迭代器
        Iterator<Element> iterator = element.elementIterator();

        //如果节点不是文本
        if (!element.isTextOnly()) {
            // 遍历迭代器，获取节点中的信息
            while (iterator.hasNext()) {
                Element child = iterator.next();
                traversal(child, i + 1);
            }
        } else {
            System.out.println(space + "节点值：" + element.getStringValue().trim());
        }
    }

}
