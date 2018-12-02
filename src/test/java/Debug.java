import priv.wufei.utils.audio.FFmpeg;
import priv.wufei.utils.basis.DateTimeUtils;

/**
 * @author WuFei
 */

public class Debug {

    public static void main(String[] args) {

        System.out.println(DateTimeUtils.nanoTimeTimekeeping(() -> {
            String p1 = "C:\\Users\\wufei\\Desktop\\悼锋.mp3";
            String p2 = "C:\\Users\\wufei\\Desktop\\悼锋.wav";
            String p3 = "C:\\Users\\wufei\\Desktop\\悼锋.pcm";
            FFmpeg.mp3ToWav(p1, p2, 44100, 2, "pcm_u8");
        }));
    }

}




