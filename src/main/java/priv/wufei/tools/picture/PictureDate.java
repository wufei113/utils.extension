package priv.wufei.tools.picture;

/**
 * 图像数据工厂接口
 *
 * @author WuFei
 */

interface PictureDateInterface<T extends PictureDate> {

    /**
     * 创建ImageDate工厂方法
     *
     * @param filepath 文件路径
     * @param pixels   灰度像素的比较数组（即图像指纹序列）
     * @return ImageDate对象
     */
    T createImageDate(String filepath, int[] pixels);
}

/**
 * 图像数据
 *
 * @author WuFei
 */
public class PictureDate {

    /**
     * 文件路径
     */
    private String filepath;
    /**
     * 灰度像素的比较数组（即图像指纹序列）
     */
    private int[] pixels;

    protected PictureDate(String filepath, int[] pixels) {
        this.filepath = filepath;
        this.pixels = pixels;
    }

    protected String getFilepath() {
        return filepath;
    }

    protected int[] getPixels() {
        return pixels;
    }
}
