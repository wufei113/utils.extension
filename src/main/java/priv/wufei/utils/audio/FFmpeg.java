package priv.wufei.utils.audio;

import priv.wufei.utils.basis.CmdUtils;
import priv.wufei.utils.basis.PropertiesUtils;

import java.util.List;
import java.util.Properties;

/**
 * FFmpeg工具
 *
 * @author WuFei
 */
public final class FFmpeg {

    private static final String FFMPEG;

    /**
     * 不能实例化这个类
     */
    private FFmpeg() {
    }

    /*加载ffmpeg环境*/
    static {
        Properties props = PropertiesUtils.loadProperties(FFmpeg.class, "/priv/wufei/utils/application.properties");

        FFMPEG = PropertiesUtils.getString(props, "ffmpeg");
    }

    /**
     * mp3转wav
     *
     * @param mp3FilePath 输入mp3磁盘路径
     * @param wavFilePath 输出wav磁盘路径
     * @param frequency   采样频率[8000，11025，16000，22050，44100]
     * @param channels    声道数[1，2]
     * @param pcmFormat   编码方案[pcm_u8，pcm_s16le ，pcm_s16be，pcm_u16le，pcm_u16be]
     * @return 输出信息 {@link List}集合
     */
    public static List<String> mp3ToWav(String mp3FilePath,
                                        String wavFilePath,
                                        int frequency,
                                        int channels,
                                        String pcmFormat) {

        StringBuffer command = new StringBuffer();

        command.append(FFMPEG);
        command.append(" -i ");       //输入的文件
        command.append(mp3FilePath);
        command.append(" -ar ");      //设置音频的采样频率
        command.append(frequency);
        command.append(" -ac ");      //设置声道数
        command.append(channels);
        command.append(" -acodec ");  //音频编码方案
        command.append(pcmFormat);
        command.append(" -y ");       //将覆盖已存在的文件
        command.append(wavFilePath);

        return CmdUtils.execute(String.valueOf(command));
    }

}
