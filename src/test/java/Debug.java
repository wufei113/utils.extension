import priv.wufei.utils.basis.DateTimeUtils;
import priv.wufei.utils.video.FFmpeg;

import java.io.File;

/**
 * @author WuFei
 */

public class Debug {

    public static void main(String[] args) {

        System.out.println(DateTimeUtils.nanoTimeTimekeeping(() -> {
            String p1 = "C:\\Users\\wufei\\Desktop\\00";
            String p2 = "C:\\Users\\wufei\\Desktop\\out.mp4";
            FFmpeg.imageSequenceComposite(
                    p1 + File.separatorChar + "%04d.png", p2,
                    24 / 1.001, "libx264", "yuv420p");


        }));
    }

}




