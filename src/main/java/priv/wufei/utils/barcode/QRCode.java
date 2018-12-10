package priv.wufei.utils.barcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import priv.wufei.utils.basis.enums.LanguageEncode;
import priv.wufei.utils.basis.enums.RenderingHint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import static priv.wufei.utils.basis.ImageUtils.createBufferedImage;
import static priv.wufei.utils.basis.ImageUtils.customOperate;
import static priv.wufei.utils.basis.ImageUtils.setBorderStroke;

/**
 * 二维码工具
 *
 * @author WuFei
 */
public final class QRCode {

    /**
     * 不能实例化这个类
     */
    private QRCode() {
    }

    /**
     * 生成二维码(黑白)
     *
     * @param content 存储内容
     * @param size    二维码尺寸
     * @return {@link BufferedImage}
     * @throws WriterException WriterException
     */
    public static BufferedImage write(String content, int size) throws WriterException {

        return write(content, size, Color.BLACK, Color.WHITE);
    }

    /**
     * 生成二维码(自定义颜色)
     *
     * @param content   存储内容
     * @param size      二维码尺寸
     * @param highColor 高于平均值的颜色
     * @param lowColor  低于平均值的颜色
     * @return {@link BufferedImage}
     * @throws WriterException WriterException
     */
    public static BufferedImage write(String content, int size, Color highColor, Color lowColor) throws WriterException {

        //高于平均值的rgb
        int high = highColor.getRGB();
        //底于平均值的rgb
        int low = lowColor.getRGB();

        BufferedImage image;

        image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        //定义二维码的参数
        Map<EncodeHintType, Object> hints = new HashMap<>(8);
        //设置编码
        hints.put(EncodeHintType.CHARACTER_SET, LanguageEncode.UTF8.getEncode());
        //设置纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //四边空白区域大小
        hints.put(EncodeHintType.MARGIN, 1);

        //生成二维码
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, size, size, hints);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {

                image.setRGB(x, y, bitMatrix.get(x, y) ? high : low);
            }
        }

        return image;
    }

    /**
     * 生成二维码(黑白带logo)
     *
     * @param content      存储内容
     * @param size         二维码尺寸
     * @param logoFilePath logo图片磁盘路径
     * @return {@link BufferedImage}
     * @throws Exception Exception
     */
    public static BufferedImage write(String content, int size, String logoFilePath) throws Exception {

        BufferedImage matrixImage = write(content, size);

        return setLogo(matrixImage, logoFilePath);
    }

    /**
     * 设置logo
     *
     * @param matrixImage  源二维码图片
     * @param logoFilePath logo图片磁盘路径
     * @return 返回带有logo的二维码图片
     * @throws Exception Exception
     */
    private static BufferedImage setLogo(BufferedImage matrixImage, String logoFilePath) throws Exception {

        int width = matrixImage.getWidth();
        int height = matrixImage.getHeight();

        //读取Logo图片
        BufferedImage logo = createBufferedImage(logoFilePath);

        customOperate(matrixImage, (g2d) -> {

            g2d.setColor(Color.WHITE);
            //抗锯齿
            g2d.setRenderingHints(RenderingHint.ANTIALIAS_ON.getRenderingHints());
            //开始绘制logo
            g2d.drawImage(logo, width * 2 / 5, height * 2 / 5, width / 5, height / 5, null);

            //设置圆弧线条
            setBorderStroke(g2d, new BasicStroke(width / 40.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            //指定弧度的圆角矩形
            RoundRectangle2D.Double r1 = new RoundRectangle2D.Double(width * 2 / 5.0, height * 2 / 5.0,
                    width / 5.0, height / 5.0, width / 20.0, height / 20.0);
            // 绘制圆弧矩形
            g2d.draw(r1);

            //设置logo外围灰色边框
            g2d.setColor(Color.GRAY);
            //间隙距离
            int gap = width / 200;
            setBorderStroke(g2d, new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            RoundRectangle2D.Double r2 = new RoundRectangle2D.Double(width / 5.0 * 2 + gap, height / 5.0 * 2 + gap,
                    width / 5.0 - 2 * gap, height / 5.0 - 2 * gap, width / 20.0, height / 20.0);
            g2d.draw(r2);
        });
        return matrixImage;
    }

    /**
     * 读取二维码
     *
     * @param image 二维码图片
     * @return 二维码储存的信息
     * @throws NotFoundException NotFoundException
     */
    public static String read(BufferedImage image) throws NotFoundException {
        //定义二维码解码参数
        Map<DecodeHintType, Object> hints = new HashMap<>(4);
        //设置编码
        hints.put(DecodeHintType.CHARACTER_SET, LanguageEncode.UTF8.getEncode());
        //花更多的时间用于寻找图上的编码，优化准确性，但不优化速度
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

        //解析
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        HybridBinarizer hybridBinarizer = new HybridBinarizer(source);
        BinaryBitmap binaryBitmap = new BinaryBitmap(hybridBinarizer);
        //读取二维码结果
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        Result result = multiFormatReader.decode(binaryBitmap, hints);

        return result.getText();
    }

}
