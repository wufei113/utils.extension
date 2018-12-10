package priv.wufei.utils.video;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.io.File.separatorChar;
import static priv.wufei.utils.basis.CmdUtils.execute;
import static priv.wufei.utils.basis.DateTimeUtils.toDecimalTime;
import static priv.wufei.utils.basis.NumberUtils.getNumberOfDigits;
import static priv.wufei.utils.basis.PropertiesUtils.getString;
import static priv.wufei.utils.basis.PropertiesUtils.loadProperties;
import static priv.wufei.utils.basis.StringUtils.isNotBlank;

/**
 * FFmpeg视频工具
 *
 * @author WuFei
 */
public final class FFmpeg {

    private static final String FFMPEG;

    /*加载ffmpeg环境*/
    static {
        Properties props = null;

        try {
            props = loadProperties(FFmpeg.class, "/external-apps.properties");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert props != null;

        FFMPEG = getString(props, "ffmpeg");
    }

    /**
     * 不能实例化这个类
     */
    private FFmpeg() {
    }

    /**
     * 获得视频总时长
     *
     * @param videoFilePath 输入视频磁盘路径
     * @return 时长
     * @throws Exception Exception
     */
    public static double getTotalTime(String videoFilePath) throws Exception {

        List<String> infos = execute(FFMPEG + " -i " + videoFilePath);
        //从视频信息中解析时长
        String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb/s";

        Pattern pattern = Pattern.compile(regexDuration);

        Matcher m = pattern.matcher(infos.toString());

        if (m.find()) {
            return toDecimalTime(m.group(1));
        } else {
            throw new Exception("文件未能解析到时长");
        }
    }

    /**
     * 获得视频帧率(fps)
     *
     * @param videoFilePath 输入视频磁盘路径
     * @return 帧率(fps)
     * @throws Exception Exception
     */
    public static double getFPS(String videoFilePath) throws Exception {

        List<String> infos = execute(FFMPEG + " -i " + videoFilePath);
        /*
         *以下占位符分别表示:
         *编码格式/像素格式/分辨率/数据速率/帧率/帧率
         *tbr一般被当成帧率。当视频的码率为固定码率时，FFmpeg显示tbr为正常的码率值。当视频有变长码率时，FFmpeg打印的tbr为多个码率的均值
         */
        String regexVideo = "Video: (.*?), (.*?), (.*?), (\\d*) kb/s, (.*?) fps, (.*?) tbr[,\\s]";

        Pattern pattern = Pattern.compile(regexVideo);

        Matcher m = pattern.matcher(infos.toString());

        if (m.find()) {
            return Double.parseDouble(m.group(5));
        } else {
            throw new Exception("文件未能解析到帧率");
        }
    }

    /**
     * 视频转换为png图像序列(从头到尾逐帧截图)
     *
     * @param videoFilePath 输入视频磁盘路径
     * @param outPicDirPath 截取的图片的磁盘保存路径(文件夹)
     * @return 输出信息 {@link List}集合
     * @throws Exception Exception
     */
    public static List<String> toPngSequence(String videoFilePath, String outPicDirPath) throws Exception {

        return toPngSequence(videoFilePath, outPicDirPath, -1, "0", null);
    }

    /**
     * 视频转换为png图像序列
     *
     * @param videoFilePath 输入视频磁盘路径
     * @param outPicDirPath 截取的图片的磁盘保存路径(文件夹)
     * @param rate          每秒截取的帧速率<br>
     *                      有效范围(0,30]，超出取默认fps
     * @param timeOff       开始时间(单位:秒)，hh:mm:ss[.xxx]的格式也支持
     * @param duration      持续截取的时长(s)，hh:mm:ss[.xxx]的格式也支持<br>
     *                      如果为null,表示取视频总时长
     * @return 输出信息 {@link List}集合
     * @throws Exception Exception
     */
    public static List<String> toPngSequence(String videoFilePath,
                                             String outPicDirPath,
                                             double rate,
                                             String timeOff,
                                             String duration) throws Exception {

        //视频支持的最大帧速率
        double maxFps = 30;

        if (rate <= 0 || rate > maxFps) {
            rate = getFPS(videoFilePath);
        }

        double totalTime = getTotalTime(videoFilePath);
        //总共要截的图片数目(向上取整)
        int amount = (int) (Math.floor(totalTime) * rate);
        //几位数
        int numberOfDigits = getNumberOfDigits(amount);

        //图像输出路径
        File outFolder = new File(outPicDirPath);
        //文件夹不存在，创建
        if (!outFolder.exists()) {
            outFolder.mkdirs();
        }
        String outDir = outFolder.getAbsolutePath() + separatorChar;

        List<String> commands = new ArrayList<>();

        commands.add(FFMPEG);
        commands.add("-y");       //将覆盖已存在的文件
        commands.add("-ss");      //从指定的时间(s)开始，hh:mm:ss[.xxx]的格式也支持
        commands.add(timeOff);
        commands.add("-r");       //设置帧速率(单位:秒)
        commands.add(rate + "");
        commands.add("-i");       //输入的文件
        commands.add(videoFilePath);
        commands.add("-f");       //指定格式(音频或视频格式)
        commands.add("image2");
        commands.add("-t");       //持续时长(s)，hh:mm:ss[.xxx]的格式也支持
        commands.add(isNotBlank(duration) ? duration : totalTime + "");
        //用指定位数的数字自动从小到大生成文件名
        commands.add(String.format("%s%%%dd.png", outDir, numberOfDigits));

        return execute(commands);
    }

    /**
     * 图像合成为视频
     *
     * @param videoFilePath 输入图片磁盘路径表达式(例如:C:/images/%04d.png)
     * @param outFilePath   视频磁盘输出路径
     * @param rate          合成一秒所需的图片数
     * @param codec         强制使用{@code codec}编解码方式<br>
     *                      null为ffmpeg默认<br>
     *                      [hevc,libx265,h264,libx264,mpeg4]
     * @param format        强制使用{@code format}像素格式<br>
     *                      null为ffmpeg默认<br>
     *                      [yuv420p]
     * @return 输出信息 {@link List}集合
     * @throws Exception Exception
     */
    public static List<String> imageSequenceComposite(String videoFilePath,
                                                      String outFilePath,
                                                      double rate,
                                                      String codec,
                                                      String format) throws Exception {

        List<String> commands = new ArrayList<>();

        //视频输出路径
        File outFile = new File(outFilePath);
        File outParentFile = outFile.getParentFile();
        //文件夹不存在，创建
        if (!outParentFile.exists()) {
            outParentFile.mkdirs();
        }

        commands.add(FFMPEG);
        commands.add("-y");           //将覆盖已存在的文件
        commands.add("-r");           //设置帧速率(单位:秒)
        commands.add(rate + "");
        commands.add("-i");           //输入的文件
        commands.add(videoFilePath);

        if (isNotBlank(codec)) {
            commands.add("-vcodec");  //解码方式
            commands.add(codec);
        }

        if (isNotBlank(format)) {
            commands.add("-pix_fmt"); //像素格式
            commands.add(format);
        }

        commands.add(outFilePath);    //输出路径

        return execute(commands);
    }

}
