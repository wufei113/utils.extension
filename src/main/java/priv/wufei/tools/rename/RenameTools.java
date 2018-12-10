package priv.wufei.tools.rename;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static priv.wufei.utils.basis.DateTimeUtils.nanoTimeTimekeeping;
import static priv.wufei.utils.basis.FileUtils.renameTo;
import static priv.wufei.utils.basis.StringUtils.replaceLast;

/**
 * 重命名工具
 *
 * @author WuFei
 */
public final class RenameTools {

    /**
     * 不能实例化这个类
     */
    private RenameTools() {
    }

    /**
     * 文件倒序重命名
     *
     * @param dirPath 存放要重命名文件的文件夹的磁盘路径
     * @throws Exception Exception
     */
    public static void reverseSort(String dirPath) throws Exception {

        var diffTime = nanoTimeTimekeeping(() -> {

            //临时标记字符串
            var bakStr = ".temp";

            var folder = new File(dirPath);

            var stream = Arrays.stream(requireNonNull(folder.listFiles()));
            //过滤掉文件夹,并进行一次排序
            var files = stream.filter(File::isFile).sorted().collect(Collectors.toList());

            var size = files.size() - 1;
            //存放临时文件路径
            List<String> tempFilePaths = new ArrayList<>((int) (1.5 * size));

            for (int i = 0, j = size; i < j; i++, j--) {

                var s1 = files.get(i).getAbsolutePath();
                var s2 = files.get(j).getAbsolutePath();

                var temp1 = s1 + bakStr;
                var temp2 = s2 + bakStr;

                renameTo(s1, temp2);
                renameTo(s2, temp1);

                tempFilePaths.add(temp1);
                tempFilePaths.add(temp2);
            }

            for (var tempPath : tempFilePaths) {
                //替换掉临时标记字符串
                var newPath = replaceLast(tempPath, bakStr, "");

                renameTo(tempPath, newPath);
            }
        });
        System.out.println("___________总用时___________" + diffTime);
    }

}
