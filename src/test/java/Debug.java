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
                    p1 + File.separatorChar + "%04d.png", p2, 23.98,null);



         /*   String md5 = DigestUtils.MD5.getMD5(p2);
            String md51 = DigestUtils.MD5.getMD5(p3);
            System.out.println(md51.equals(md5));*/
            //  ffmpeg -r 2  -i C:/Users/wufei/Desktop/ww/*.png -vframes 25 -pix_fmt yuv420p C:/Users/wufei/Desktop/ww/out.mp4

        }));
    }

}




