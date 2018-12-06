package priv.wufei.tools.picture;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;

import static priv.wufei.utils.basis.ImageUtils.changeColorSpace;
import static priv.wufei.utils.basis.ImageUtils.createBufferedImage;
import static priv.wufei.utils.basis.ImageUtils.scale;

/**
 * 图片相似度比较
 * <p>
 * 感知哈希算法 下面是一个最简单的实现：
 * <p>
 * 第一步，缩小尺寸。 将图片缩小到8x8的尺寸，总共64个像素。这一步的作用是去除图片的细节，只保留结构、明暗等基本信息，摒弃不同尺寸、比例带来的图片差异。
 * <p>
 * 第二步，简化色彩。 将缩小后的图片，转为64级灰度。也就是说，所有像素点总共只有64种颜色。
 * <p>
 * 第三步，计算平均值。 计算所有64个像素的灰度平均值。
 * <p>
 * 第四步，比较像素的灰度。 将每个像素的灰度，与平均值进行比较。大于或等于平均值，记为1；小于平均值，记为0。
 * <p>
 * 第五步，计算哈希值。
 * 将上一步的比较结果，组合在一起，就构成了一个64位的整数，这就是这张图片的指纹。组合的次序并不重要，只要保证所有图片都采用同样次序就行了。<br>
 * <p>
 * 得到指纹以后，就可以对比不同的图片，看看64位中有多少位是不一样的。在理论上，这等同于计算”汉明距离”（Hamming distance）。<br>
 * 如果不相同的数据位不超过5，就说明两张图片很相似；如果大于10，就说明这是两张不同的图片。<br>
 * 这种算法的优点是简单快速，不受图片大小缩放的影响，缺点是图片的内容不能变更。如果在图片上加几个文字，它就认不出来了。所以，它的最佳用途是根据缩略图，找出原图
 *
 * @author WuFei
 */
public final class PerceptualHashAlgorithm {

    /**
     * 不能实例化这个类
     */
    private PerceptualHashAlgorithm() {
    }

    /**
     * 得到图像指纹序列
     *
     * @param px           缩略图宽高
     * @param srcImageFile 文件
     * @return int[]
     */
    protected static int[] getFingerprint(int px, File srcImageFile) {
        try {
            //获取图像
            BufferedImage image = createBufferedImage(srcImageFile);
            //判断是不是图像,不是继续操作,否则返回null
            if (null != image) {
                //缩小成px乘px的缩略图
                image = scale(image, px, px);
                //转换至灰度
                image = changeColorSpace(image, ColorSpace.CS_GRAY, null);
                //获取灰度像素数组
                int[] pixels = image.getRGB(0, 0, px, px, null, 0, px);
                //获取平均灰度颜色
                int averageColor = getAverageOfPixelArray(pixels);
                //获取灰度像素的比较数组（即图像指纹序列）
                pixels = getPixelDeviateWeightsArray(pixels, averageColor);

                return pixels;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取灰度图的平均像素颜色值
     */
    private static int getAverageOfPixelArray(int[] pixels) {

        long sumRed = 0;

        for (int rgba : pixels) {
            Color color = new Color(rgba, true);
            //三原色的值都一样,用那个都可以
            sumRed += color.getRed();
        }
        return (int) (sumRed / pixels.length);
    }

    /**
     * 获取灰度图的像素比较数组（平均值的离差）
     */
    private static int[] getPixelDeviateWeightsArray(int[] pixels, int averageColor) {

        int[] dest = new int[pixels.length];

        for (int i = 0; i < pixels.length; i++) {
            Color color = new Color(pixels[i], true);
            dest[i] = color.getRed() - averageColor > 0 ? 1 : 0;
        }
        return dest;
    }

    /**
     * 获取两个缩略图的平均像素比较数组的汉明距离（距离越大差异越大）
     *
     * @param a 第一个缩略图的平均像素数组
     * @param b 第二个缩略图的平均像素数组
     * @return int
     */
    protected static int getHammingDistance(int[] a, int[] b) {

        int sum = 0;

        for (int i = 0; i < a.length; i++) {
            sum += a[i] == b[i] ? 0 : 1;
        }
        return sum;
    }

    /**
     * 通过汉明距离计算相似度
     *
     * @param px              缩略图宽高
     * @param hammingDistance 汉明距离
     * @return double
     */
    protected static double calSimilarity(int px, int hammingDistance) {

        int length = px * px;

        double similarity = (length - hammingDistance) / (double) length;

        //使用指数曲线调整相似度结果
        similarity = Math.pow(similarity, 2);

        return similarity;
    }

}