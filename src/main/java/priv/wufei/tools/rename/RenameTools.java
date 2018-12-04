package priv.wufei.tools.rename;

import priv.wufei.utils.basis.DateTimeUtils;
import priv.wufei.utils.basis.FileUtils;
import priv.wufei.utils.basis.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

        var diffTime = DateTimeUtils.nanoTimeTimekeeping(() -> {

            //临时标记字符串
            var bakStr = ".temp";

            var folder = new File(dirPath);

            var stream = Arrays.stream(Objects.requireNonNull(folder.listFiles()));
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

                FileUtils.renameTo(s1, temp2);
                FileUtils.renameTo(s2, temp1);

                tempFilePaths.add(temp1);
                tempFilePaths.add(temp2);
            }

            for (var tempPath : tempFilePaths) {
                //替换掉临时标记字符串
                var newPath = StringUtils.replaceLast(tempPath, bakStr, "");

                FileUtils.renameTo(tempPath, newPath);
            }
        });
        System.out.println("___________总用时___________\n" + diffTime);
    }

}
