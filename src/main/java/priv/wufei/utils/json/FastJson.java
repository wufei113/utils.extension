package priv.wufei.utils.json;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * FastJson工具
 *
 * @author WuFei
 */
public final class FastJson {

    /**
     * 不能实例化这个类
     */
    private FastJson() {
    }

    /**
     * 对象转JSON字符串
     *
     * @param t   进行转化的对象
     * @param <T> 对象类型
     * @return JSON字符串
     */
    public static <T> String toJSONString(T t) {

        return JSON.toJSONString(t);
    }

    /**
     * 数组转JSON字符串
     *
     * @param arr 数组
     * @return JSON字符串
     */
    public static String toJSONString(Object[] arr) {

        return JSON.toJSONString(arr);
    }

    /**
     * JSON字符串转对象
     *
     * @param jsonString JSON字符串
     * @param clz        对象{@link Class}类型
     * @param <T>        对象类型
     * @return JavaBean对象
     */
    public static <T> T parseObject(String jsonString, Class<T> clz) {

        return JSON.parseObject(jsonString, clz);
    }

    /**
     * JSON字符串转{@link List}集合
     *
     * @param jsonString JSON字符串
     * @param clz        集合的泛型{@link Class}类型
     * @param <T>        集合的泛型
     * @return {@link List}集合
     */
    public static <T> List<T> parseArray(String jsonString, Class<T> clz) {

        return JSON.parseArray(jsonString, clz);
    }

    /**
     * 通过JSON实现深复制
     *
     * @param t   进行复制的对象
     * @param clz 对象{@link Class}类型
     * @param <T> 对象类型
     * @return 复制后的对象
     */
    public static <T> T deepClone(T t, Class<T> clz) {

        return JSON.parseObject(JSON.toJSONString(t), clz);
    }

}