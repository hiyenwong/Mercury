package io.hiyen.mercury.utils;

import com.google.common.collect.Lists;
import io.hiyen.mercury.utils.constants.WebType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 归档工具
 *
 * @author Hi Yen Wong
 * @date 2023/2/20 23:22
 */
public class ArchiveUtils {
    ArchiveUtils() {
        throw new IllegalStateException("Unity class");
    }

    /**
     * 将一个文件解压到某一个固定目录
     *
     * @param inputFile
     * @param destinationFilePath
     * @return
     * @throws IOException
     */
    public static List<String> unzip(String inputFile, String destinationFilePath) throws IOException {
        return unzip(inputFile, destinationFilePath, false);
    }

    public static List<String> unzip(String inputFile, String destinationFilePath, boolean isBuff) throws IOException {
        String id = String.valueOf(SnowFlowerUtils.getInstance().nextId());
        String destPath = destinationFilePath + WebType.UNDER_LINE + id;
        File files = isBuff ? uncompress(inputFile, destPath) : unzipFile(inputFile, destPath);
        List<String> fileLists = Lists.newArrayList();
        assert files != null;
        File[] countFile = files.listFiles();
        assert countFile != null;
        for (File file1 : countFile) {
            fileLists.add(file1.getAbsolutePath());
        }
        return fileLists;
    }

    public static String zip(String dirPath, String fileName, Boolean isBuff) throws IOException {
        if (null == isBuff) {
            isBuff = true;
        }
        File fileToZip = new File(dirPath);
        FileOutputStream fos = new FileOutputStream(fileName);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        if (isBuff) {
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            compress(fileToZip, fileName, zipOut, bos);
            zipOut.close();
        } else {
            zipFile(fileToZip, fileToZip.getName(), zipOut);
            zipOut.close();
        }
        fos.close();
        return fileName;
    }

    public static String zip(String dirPath, String fileName) throws IOException {
        return zip(dirPath, fileName, false);
    }

    /**
     * 解压
     *
     * @param zipFilePath
     * @param destDirPath
     * @throws IOException
     */

    public static File unzipFile(String zipFilePath, String destDirPath) throws IOException {
        byte[] buffer = new byte[1024];
        File destDir = new File(destDirPath);
        ZipInputStream zis = new ZipInputStream(Files.newInputStream(Paths.get(zipFilePath)));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }
                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.close();
        return destDir;
    }

    /**
     * 创建文件
     *
     * @param destinationDir
     * @param zipEntry
     * @return
     * @throws IOException
     */
    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());
        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();
        ExceptionUtils.isTrue(destFilePath.startsWith(destDirPath + File.separator))
                .throwMessage("Entry is outside of the target dir: " + zipEntry.getName());
        return destFile;
    }

    public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith(WebType.FORWARD_SLASH)) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + WebType.FORWARD_SLASH));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    /*
    =========== Buffer Stream =================================
     */

    /**
     * 压缩
     *
     * @param fileToZip
     * @param name
     * @param zipOut
     * @param bos
     * @throws IOException
     */
    public static void compress(File fileToZip,
                                String name, ZipOutputStream zipOut, BufferedOutputStream bos) throws IOException {
        if (name == null) {
            name = fileToZip.getName();
        }
        //如果路径为目录（文件夹）
        if (fileToZip.isDirectory()) {
            //取出文件夹中的文件（或子文件夹）
            File[] listFiles = fileToZip.listFiles();
            assert listFiles != null;
            if (listFiles.length == 0) {
                //如果文件夹为空，则只需在目的地zip文件中写入一个目录进入
                zipOut.putNextEntry(new ZipEntry(name + "/"));
            } else {
                //如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
                for (File listFile : listFiles) {
                    compress(listFile, name + "/" + listFile.getName(), zipOut, bos);
                }
            }
        } else {
            //如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
            zipOut.putNextEntry(new ZipEntry(name));
            FileInputStream fos = new FileInputStream(fileToZip);
            BufferedInputStream bis = new BufferedInputStream(fos);
            int len = -1;
            //将源文件写入到zip文件中
            byte[] buf = new byte[1024];
            while ((len = bis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            bos.flush();
            bis.close();
        }
    }

    public static File uncompress(String inputFile, String destinationFilePath) throws BusinessException, IOException {
        //获取当前压缩文件
        File srcFile = new File(inputFile);
        // 判断源文件是否存在
        ExceptionUtils.isTrue(srcFile.exists()).throwMessage(srcFile.getPath() + " File does not exist");
        //开始解压
        //构建解压输入流
        ZipInputStream zIn = new ZipInputStream(Files.newInputStream(srcFile.toPath()));
        ZipEntry entry = null;
        File file = new File(destinationFilePath);
        while ((entry = zIn.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                file = new File(destinationFilePath, entry.getName());
                if (!file.exists()) {
                    //创建此文件的上级目录
                    new File(file.getParent()).mkdirs();
                }
                OutputStream out = Files.newOutputStream(file.toPath());
                BufferedOutputStream bos = new BufferedOutputStream(out);
                int len = -1;
                byte[] buf = new byte[1024];
                while ((len = zIn.read(buf)) != -1) {
                    bos.write(buf, 0, len);
                }
                // 关流顺序，先打开的后关闭
                bos.close();
                out.close();
            }
        }
        return new File(file.getParent());
    }
}
