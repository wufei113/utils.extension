package priv.wufei.tools.picture;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static priv.wufei.tools.picture.PerceptualHashAlgorithm.calSimilarity;
import static priv.wufei.tools.picture.PerceptualHashAlgorithm.getFingerprint;
import static priv.wufei.tools.picture.PerceptualHashAlgorithm.getHammingDistance;
import static priv.wufei.utils.basis.DateTimeUtils.nanoTimeTimekeeping;
import static priv.wufei.utils.basis.FileUtils.getFileName;
import static priv.wufei.utils.basis.FileUtils.getFiles;
import static priv.wufei.utils.basis.FileUtils.renameTo;
import static priv.wufei.utils.basis.ThreadUtils.getThreadPool;

/**
 * 图像工具
 *
 * @author WuFei
 */
public final class PictureTools {

    /**
     * 不能实例化这个类
     */
    private PictureTools() {
    }

    /**
     * 图像相似度识别（最佳用途：根据缩略图，找出原图）（感知哈希算法）<br>
     * 相似图像加前缀 "_similar__"以示区别
     *
     * @param srcDirPath 要处理的文件夹路径(最好只有一级目录)
     * @throws Exception Exception
     */
    public static void imageSimilarityRecognition(String srcDirPath) throws Exception {

        var diffTime = nanoTimeTimekeeping(() -> {

            //缩小成px乘px的缩略图
            var px = 8;
            //允许的最大差距汉明距离
            var diff = 5;
            //实例化接口
            PictureDateInterface<PictureDate> imageDateInterface = PictureDate::new;
            //存放图像数据
            List<PictureDate> imageDates = new ArrayList<>();
            //存放相似图像的路径
            Set<String> similar = new HashSet<>();
            //得到目录(文件)下所有文件集合,不包括文件夹
            var files = getFiles(new File(srcDirPath));
            //创建线程池
            var threadPool = getThreadPool(4, 4,
                    1000, TimeUnit.MILLISECONDS, 500, "吴飞");
            //进行遍历
            for (var file : files) {
                Runnable runnable = () -> {
                    System.out.println("正在处理: " + file.getAbsolutePath());
                    //得到图像指纹序列
                    var pixels = getFingerprint(px, file);
                    imageDates.add(imageDateInterface.createImageDate(file.getAbsolutePath(), pixels));
                };
                threadPool.execute(runnable);
            }
            //有序关闭线程池
            threadPool.shutdown();
            //没处理完，不让走
            while (threadPool.getCompletedTaskCount() < files.size()) {
                Thread.sleep(1000);
            }

            var size = imageDates.size();
            if (size > 0) {
                for (var j = 0; j < size; j++) {
                    for (var k = j + 1; k < size; k++) {
                        //获取两个图的汉明距离
                        var pixels1 = imageDates.get(j).getPixels();
                        var pixels2 = imageDates.get(k).getPixels();
                        var hammingDistance = getHammingDistance(pixels1, pixels2);
                        //大于diff的差异过大
                        if (hammingDistance < diff) {
                            //通过汉明距离计算相似度，取值范围 [0.0, 1.0]
                            var similarity = calSimilarity(px, hammingDistance);

                            var filepath1 = imageDates.get(j).getFilepath();
                            var filepath2 = imageDates.get(k).getFilepath();

                            similar.add(filepath1);
                            similar.add(filepath2);

                            var filename1 = getFileName(filepath1);
                            var filename2 = getFileName(filepath2);
                            //输出相似度信息
                            System.out.print("\"" + filename1 + "\"和\"" + filename2 + "\"");
                            System.out.println("  相似度：" + String.format("%1$.2f", (similarity * 100)) + "%");
                        }
                    }
                }
                if (similar.size() > 0) {
                    //相似图像加前缀 "_similar__"
                    similar.parallelStream().forEach((oldPath) -> {
                        var oldFile = new File(oldPath);
                        var newFile = new File(oldFile.getParent(), "_similar__" + oldFile.getName());
                        renameTo(oldFile, newFile);
                    });
                } else {
                    System.out.println("未发现相似图像");
                }
            } else {
                System.out.println("未发现图像");
            }
            System.out.println("结束");
        });
        System.out.println("___________总用时___________\n" + diffTime);
    }

}