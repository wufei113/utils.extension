package priv.wufei.utils.audio;

import priv.wufei.utils.basis.CmdUtils;
import priv.wufei.utils.basis.PropertiesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * FFmpeg音频工具
 *
 * @author WuFei
 */
public final class FFmpeg {

    private static final String FFMPEG;

    /*加载ffmpeg环境*/
    static {
        Properties props = null;

        try {
            props = PropertiesUtils.loadProperties(FFmpeg.class, "/priv/wufei/utils/external-apps.properties");
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert props != null;

        FFMPEG = PropertiesUtils.getString(props, "ffmpeg");
    }

    /**
     * 不能实例化这个类
     */
    private FFmpeg() {
    }

    /**
     * mp3转wav
     *
     * @param mp3FilePath 输入mp3磁盘路径
     * @param wavFilePath 输出wav磁盘路径
     * @param rate        采样频率[8000，11025，16000，22050，44100]
     * @param channels    声道数[1，2]
     * @param codec       音频编码方案[pcm_u8，pcm_s16le ，pcm_s16be，pcm_u16le，pcm_u16be]
     * @return 输出信息 {@link List}集合
     * @throws Exception Exception
     */
    public static List<String> mp3ToWav(String mp3FilePath,
                                        String wavFilePath,
                                        int rate,
                                        int channels,
                                        String codec) throws Exception {

        List<String> commands = new ArrayList<>();

        commands.add(FFMPEG);
        commands.add("-y");       //将覆盖已存在的文件
        commands.add("-i");       //输入的文件
        commands.add(mp3FilePath);
        commands.add("-ar");      //设置音频的采样频率(单位:秒)
        commands.add(rate + "");
        commands.add("-ac");      //设置声道数
        commands.add(channels + "");
        commands.add("-acodec");  //音频编码方案
        commands.add(codec);
        commands.add(wavFilePath);

        return CmdUtils.execute(commands);
    }

}
